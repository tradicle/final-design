<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="page">
    <div class="container">
      <section class="hero-card">
        <p class="eyebrow">About Us</p>
        <h1>关于我们</h1>
        <p class="lead">
          汪汪喵呜孤儿院是一个面向校园与社区场景的流浪动物救助与云领养平台，围绕宠物档案展示、
          领养申请、社区互动、物资协同和透明公示，帮助更多毛孩子从流浪走向稳定生活。
        </p>
      </section>

      <section class="grid">
        <el-card class="info-card" shadow="never">
          <h3>我们的使命</h3>
          <p>坚持“科学救助、透明运营、领养代替购买”的理念，让救助行动从一次性善意变成可持续的长期机制。</p>
          <p>平台不仅展示待领养宠物，也提供物资认领、每周更新、透明公示和爱心活动，帮助公众更直观地了解真实救助过程。</p>
        </el-card>

        <el-card class="info-card" shadow="never">
          <h3>我们在做什么</h3>
          <p>我们持续进行校园与社区流浪动物的巡查、救助、医疗、绝育、寄养和领养安置，并记录每一只动物的状态与故事。</p>
          <p>通过后台内容运营与审核流程，平台也支持管理员统一维护宠物档案、领养申请、爱心活动和物资认领记录。</p>
        </el-card>
      </section>

      <section class="timeline-card">
        <h3>领养流程</h3>
        <div class="timeline">
          <div class="step">
            <span class="step-no">1</span>
            <div>
              <h4>浏览宠物档案</h4>
              <p>进入领养中心查看待领养宠物的基础信息、救助故事和发现位置。</p>
            </div>
          </div>
          <div class="step">
            <span class="step-no">2</span>
            <div>
              <h4>阅读领养须知</h4>
              <p>确认领养责任、科学饲养要求和回访约定，再提交申请资料。</p>
            </div>
          </div>
          <div class="step">
            <span class="step-no">3</span>
            <div>
              <h4>提交并等待审核</h4>
              <p>管理员将在后台查看申请信息、审核状态并给出通过或拒绝说明。</p>
            </div>
          </div>
        </div>
      </section>

      <section class="contact-card">
        <h3>联系与公示</h3>
        <p><strong>地址：</strong>深圳市南山区沙河街道睿印商城 B2 层下沉广场喵喵领养小屋</p>
        <p><strong>电话：</strong>0755-86035169</p>
        <p><strong>服务时间：</strong>周一至周日 9:00-18:00</p>
        <div class="actions">
          <el-button type="primary" @click="router.push('/transparency')">查看透明公示</el-button>
          <el-button @click="router.push('/activities')">查看爱心活动</el-button>
          <el-button @click="router.push('/animals')">进入领养中心</el-button>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 32px 20px 56px;
  background: linear-gradient(180deg, #fff7ef 0%, #ffffff 20%);
}

.container {
  max-width: 1080px;
  margin: 0 auto;
}

.hero-card {
  padding: 10px 0 30px;
}

.eyebrow {
  margin: 0 0 10px;
  color: #c96c35;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 1.4px;
}

.hero-card h1 {
  margin: 0 0 14px;
  font-size: 38px;
  color: #2e241e;
}

.lead {
  margin: 0;
  max-width: 780px;
  color: #66584f;
  line-height: 1.95;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
  margin-bottom: 18px;
}

.info-card,
.timeline-card,
.contact-card {
  border-radius: 18px;
  border: 1px solid #f0dcc8;
  box-shadow: 0 10px 28px rgba(44, 30, 24, 0.06);
}

.info-card h3,
.timeline-card h3,
.contact-card h3 {
  margin: 0 0 14px;
  color: #2e241e;
  font-size: 22px;
}

.info-card p,
.contact-card p {
  margin: 0 0 10px;
  color: #66584f;
  line-height: 1.9;
}

.timeline-card {
  padding: 26px;
  background: #fff;
  margin-bottom: 18px;
}

.timeline {
  display: grid;
  gap: 16px;
}

.step {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

.step-no {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: #fff1e6;
  color: #c96c35;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
}

.step h4 {
  margin: 0 0 6px;
  color: #3a2d26;
}

.step p {
  margin: 0;
  color: #66584f;
  line-height: 1.8;
}

.contact-card {
  padding: 26px;
  background: #fffdfa;
}

.actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 18px;
}

@media (max-width: 768px) {
  .page {
    padding: 24px 14px 42px;
  }

  .grid {
    grid-template-columns: 1fr;
  }

  .hero-card h1 {
    font-size: 30px;
  }
}
</style>

