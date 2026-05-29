import axios from 'axios'
import * as cheerio from 'cheerio'
import fs from 'fs'
import path from 'path'

const LIST_URL = 'http://www.szadoptpet.com/list/4.html'
const BASE = 'http://www.szadoptpet.com'

function esc(str) {
  return str.replace(/\\/g, '\\\\').replace(/'/g, "\\'").replace(/\n/g, '\\n')
}

async function fetchDetail(url) {
  const { data: html } = await axios.get(url, { timeout: 15000 })
  const $ = cheerio.load(html)
  const title = $('.aboutBrief .items .title').text().trim()
  const content = $('.aboutBrief .items .describe').text().trim()
  return { title, content }
}

async function main() {
  console.log('Fetching list page...')
  const { data: html } = await axios.get(LIST_URL, { timeout: 15000 })
  const $ = cheerio.load(html)

  const raw = []
  $('.lastNews-list ul li').each((i, el) => {
    const link = $(el).find('a.headline')
    const title = link.text().trim()
    const href = link.attr('href')
    const date = $(el).find('span.time').text().trim()
    if (title && href) {
      const url = href.startsWith('http') ? href : `${BASE}${href}`
      raw.push({ title, url, date })
    }
  })

  console.log(`Found ${raw.length} articles:\n`)
  for (let i = 0; i < raw.length; i++) {
    console.log(`  [${i + 1}] ${raw[i].title}  (${raw[i].date})`)
  }

  // Merge duplicate "狗语词典大全"
  const dogDict = raw.filter(a => a.title === '狗语词典大全')
  const others = raw.filter(a => a.title !== '狗语词典大全')

  console.log('\nFetching detail pages...')

  const results = []

  for (const a of others) {
    console.log(`  Fetching: ${a.title}`)
    try {
      const d = await fetchDetail(a.url)
      results.push({ title: d.title || a.title, content: d.content, category: '小常识' })
    } catch (e) {
      console.log(`    Failed: ${e.message}`)
    }
  }

  // Handle duplicate dog dictionary
  if (dogDict.length > 0) {
    let mergedContent = ''
    for (const a of dogDict) {
      console.log(`  Fetching: ${a.title} (${a.date})`)
      try {
        const d = await fetchDetail(a.url)
        if (d.content) mergedContent += (mergedContent ? '\n\n---\n\n' : '') + d.content
      } catch (e) {
        console.log(`    Failed: ${e.message}`)
      }
    }
    results.push({ title: '狗语词典大全', content: mergedContent, category: '小常识' })
  }

  // Output SQL
  let sql = "-- Knowledge seed data scraped from szadoptpet.com\n"
  sql += "-- Source: " + LIST_URL + "\n"
  sql += "-- Generated: " + new Date().toISOString() + "\n\n"

  for (const r of results) {
    sql += `INSERT INTO knowledge (title, content, category, sort_order) VALUES ('${esc(r.title)}', '${esc(r.content)}', '${esc(r.category)}', 0);\n\n`
  }

  const outPath = path.resolve('knowledge_seed.sql')
  fs.writeFileSync(outPath, sql, 'utf-8')
  console.log(`\nSQL written to ${outPath} (${results.length} articles)`)
  console.log('Done.')
}

main().catch(console.error)
