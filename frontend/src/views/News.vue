<script setup lang="ts">
import { ref } from 'vue'

const newsList = ref([
  {
    id: 1,
    title: '爱心义卖！2026《汪喵漫游世界》公益台历现货发售',
    summary: '2026汪喵公益台历义卖开始啦！我们今年的任务是卖出2000本台历，凑够冬季取暖费！请大家支持汪喵！谢谢！我们总说，想带心爱的它去看遍世界。',
    image: 'https://picsum.photos/800/400?random=1',
    date: '2026-03-01'
  },
  {
    id: 2,
    title: '给毛孩子的爱与仪式感！生日大餐吃了，年夜饭还会远吗？',
    summary: '在汪汪喵呜孤儿院，每个曾漂泊的毛孩子，都能拥有属于自己的“仪式感时刻”。戴着可爱围兜的狗狗们，围着粉粉的生日蛋糕歪头卖萌。',
    image: 'https://picsum.photos/800/400?random=2',
    date: '2026-02-28'
  },
  {
    id: 3,
    title: 'HI，亲们，一起帮流浪猫拼好窝吗？宫猫同款哦！',
    summary: '立秋了，冬天还会远吗？汪汪喵呜孤儿院流浪猫户外暖冬猫屋开始筹备了！2025年是我们连续第十年做暖冬猫屋。',
    image: 'https://picsum.photos/800/400?random=3',
    date: '2026-02-15'
  },
  {
    id: 4,
    title: '15岁的“双喜”16岁的“杰克”17岁的“嘿嘿”永远留在了2025年！',
    summary: '再见，老朋友们！爱是善始善终。救助不仅是给它们一个庇护所，更是给予生命一个家，一个有尊严、有质量的晚年和一场温暖的告别。',
    image: 'https://picsum.photos/800/400?random=4',
    date: '2026-01-10'
  }
])
</script>

<template>
  <div class="page">
    <div class="container">
      <h2 class="page-title">救助资讯</h2>
      
      <div class="news-list">
        <div class="news-item" v-for="item in newsList" :key="item.id">
          <div class="news-img">
            <img :src="item.image" :alt="item.title" />
          </div>
          <div class="news-content">
            <h3 class="news-title">{{ item.title }}</h3>
            <p class="news-date">{{ item.date }}</p>
            <p class="news-summary">{{ item.summary }}</p>
            <el-button link type="primary">阅读全文</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 40px 20px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

.page-title {
  font-size: 32px;
  text-align: center;
  margin-bottom: 40px;
  color: #333;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.news-item {
  display: flex;
  gap: 30px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: transform 0.2s;
}

.news-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}

.news-img {
  width: 280px;
  height: 180px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
}

.news-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-content {
  flex: 1;
}

.news-title {
  font-size: 20px;
  margin: 0 0 10px;
  color: #333;
  line-height: 1.4;
}

.news-date {
  font-size: 12px;
  color: #999;
  margin-bottom: 15px;
}

.news-summary {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

@media (max-width: 768px) {
  .news-item {
    flex-direction: column;
    gap: 15px;
  }
  .news-img {
    width: 100%;
    height: 200px;
  }
}
</style>
