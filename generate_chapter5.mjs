import {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  HeadingLevel, AlignmentType, WidthType, BorderStyle,
  TableLayoutType, ShadingType, convertInchesToTwip
} from 'docx';
import * as fs from 'fs';

// Read content from JSON data file
const data = JSON.parse(fs.readFileSync('c:/Users/Administrator/Desktop/毕业设计/chapter5_data.json', 'utf-8'));

function T(text) {
  return new TextRun({ text, font: { name: "宋体" }, size: 24 });
}
function TB(text) {
  return new TextRun({ text, font: { name: "宋体" }, size: 24, bold: true });
}
function TH(text, level) {
  const sizes = { 1: 32, 2: 28, 3: 24 };
  return new Paragraph({
    spacing: { line: 360, before: 120, after: 120 },
    heading: level,
    children: [new TextRun({ text, font: { name: "黑体" }, size: sizes[level] || 24, bold: true })],
  });
}
function BP(text) {
  return new Paragraph({
    spacing: { line: 360, before: 60, after: 60 },
    indent: { firstLine: convertInchesToTwip(0.29) },
    alignment: AlignmentType.JUSTIFIED,
    children: [T(text)],
  });
}
function CP(text) {
  return new Paragraph({
    spacing: { line: 360, before: 60, after: 60 },
    alignment: AlignmentType.CENTER,
    children: [TB(text)],
  });
}
function TC(text, w, opts = {}) {
  return new TableCell({
    width: { size: w || 2500, type: WidthType.DXA },
    shading: opts.shading ? { type: ShadingType.SOLID, color: "F2F2F2" } : undefined,
    verticalAlign: "center",
    children: [new Paragraph({
      spacing: { before: 40, after: 40 },
      alignment: opts.center !== false ? AlignmentType.CENTER : AlignmentType.LEFT,
      children: [opts.bold ? TB(text) : new TextRun({ text, font: { name: "宋体" }, size: 21 })],
    })],
  });
}
function InfoTable(rows) {
  return new Table({
    width: { size: 9000, type: WidthType.DXA },
    layout: TableLayoutType.FIXED,
    borders: {
      top: { style: BorderStyle.SINGLE, size: 1 },
      bottom: { style: BorderStyle.SINGLE, size: 1 },
      left: { style: BorderStyle.SINGLE, size: 1 },
      right: { style: BorderStyle.SINGLE, size: 1 },
      insideHorizontal: { style: BorderStyle.SINGLE, size: 1 },
      insideVertical: { style: BorderStyle.SINGLE, size: 1 },
    },
    rows: rows.map(([label, value]) => new TableRow({
      children: [
        TC(label, 2800, { bold: true, shading: true }),
        TC(value, 6200, { center: false }),
      ],
    })),
  });
}
function HeaderRow(cells) {
  return new TableRow({
    children: cells.map(([text, w]) => TC(text, w, { bold: true, shading: true })),
  });
}
function DataRow(cells) {
  return new TableRow({
    children: cells.map(([text, w]) => TC(text, w, { center: false })),
  });
}
function FullTable(headers, rows) {
  const colWidth = 9000 / headers.length;
  return new Table({
    width: { size: 9000, type: WidthType.DXA },
    layout: TableLayoutType.FIXED,
    borders: {
      top: { style: BorderStyle.SINGLE, size: 1 },
      bottom: { style: BorderStyle.SINGLE, size: 1 },
      left: { style: BorderStyle.SINGLE, size: 1 },
      right: { style: BorderStyle.SINGLE, size: 1 },
      insideHorizontal: { style: BorderStyle.SINGLE, size: 1 },
      insideVertical: { style: BorderStyle.SINGLE, size: 1 },
    },
    rows: [
      new TableRow({ children: headers.map(h => TC(h, colWidth, { bold: true, shading: true })) }),
      ...rows.map(r => new TableRow({ children: r.map(c => TC(c, colWidth, { center: false })) })),
    ],
  });
}
function Spacer() { return new Paragraph({ spacing: { before: 60 }, children: [] }); }

// Build document content from data
const children = [];

for (const block of data.blocks) {
  switch (block.type) {
    case 'h1':
      children.push(TH(block.text, HeadingLevel.HEADING_1));
      break;
    case 'h2':
      children.push(TH(block.text, HeadingLevel.HEADING_2));
      break;
    case 'h3':
      children.push(TH(block.text, HeadingLevel.HEADING_3));
      break;
    case 'p':
      children.push(BP(block.text));
      break;
    case 'caption':
      children.push(CP(block.text));
      break;
    case 'infoTable':
      children.push(InfoTable(block.rows));
      children.push(Spacer());
      break;
    case 'fullTable':
      children.push(FullTable(block.headers, block.rows));
      children.push(Spacer());
      break;
    case 'spacer':
      children.push(Spacer());
      break;
  }
}

const doc = new Document({
  styles: {
    default: {
      document: {
        run: { font: { name: "宋体" }, size: 24 },
      },
    },
  },
  sections: [{
    properties: {
      page: {
        margin: { top: convertInchesToTwip(1), bottom: convertInchesToTwip(1), left: convertInchesToTwip(1.2), right: convertInchesToTwip(1.2) },
      },
    },
    children,
  }],
});

const buffer = await Packer.toBuffer(doc);
const outPath = 'c:/Users/Administrator/Desktop/毕业设计/第五章_系统详细设计与实现.docx';
fs.writeFileSync(outPath, buffer);
console.log('Generated:', outPath);
