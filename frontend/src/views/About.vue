<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="main-card">
        <section class="hero-block">
          <h1>关于我们</h1>
          <p class="lead">
            毛茸茸之家是一个面向校园与社区场景的流浪动物救助与云领养平台，围绕宠物档案展示、
            领养申请、社区互动、物资协同和透明公示，帮助更多毛孩子从流浪走向稳定生活。
          </p>
        </section>

        <section class="grid">
          <div class="info-block">
            <h3>我们的使命</h3>
            <p>坚持"科学救助、透明运营、领养代替购买"的理念，让救助行动从一次性善意变成可持续的长期机制。</p>
            <p>平台不仅展示待领养宠物，也提供物资认领、每周更新、透明公示和爱心活动，帮助公众更直观地了解真实救助过程。</p>
          </div>

          <div class="info-block">
            <h3>我们在做什么</h3>
            <p>我们持续进行校园与社区流浪动物的巡查、救助、医疗、绝育、寄养和领养安置，并记录每一只动物的状态与故事。</p>
            <p>通过后台内容运营与审核流程，平台也支持管理员统一维护宠物档案、领养申请、爱心活动和物资认领记录。</p>
          </div>
        </section>

        <section class="timeline-block">
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

        <section class="contact-block">
          <h3>联系与公示</h3>
          <p><strong>地址：</strong>杭州市浙江工业大学</p>
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
  </div>
</template>

<style scoped>
.page {
  padding: 40px 20px 56px;
  background-color: #faf7f2;
  min-height: 100vh;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

.main-card {
  background: #fffaf6;
  border-radius: 12px;
  padding: 32px 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.hero-block {
  padding: 0 0 8px;
}

.hero-block h1 {
  margin: 0 0 14px;
  font-size: 32px;
  color: #2e241e;
}

.lead {
  margin: 0;
  color: #66584f;
  line-height: 1.95;
}

.hero-block + .grid {
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid #f0e8dd;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.info-block h3 {
  margin: 0 0 10px;
  color: #2e241e;
  font-size: 18px;
  border-left: 4px solid var(--el-color-primary);
  padding-left: 10px;
}

.info-block p {
  margin: 0 0 8px;
  color: #66584f;
  line-height: 1.85;
  font-size: 14px;
}

.timeline-block {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0e8dd;
}

.timeline-block h3 {
  margin: 0 0 16px;
  color: #2e241e;
  font-size: 18px;
  border-left: 4px solid var(--el-color-primary);
  padding-left: 10px;
}

.timeline {
  display: grid;
  gap: 14px;
}

.step {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

.step-no {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #fff1e6;
  color: #c96c35;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.step h4 {
  margin: 0 0 4px;
  color: #3a2d26;
  font-size: 15px;
}

.step p {
  margin: 0;
  color: #66584f;
  line-height: 1.7;
  font-size: 14px;
}

.contact-block {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0e8dd;
}

.contact-block h3 {
  margin: 0 0 12px;
  color: #2e241e;
  font-size: 18px;
  border-left: 4px solid var(--el-color-primary);
  padding-left: 10px;
}

.contact-block p {
  margin: 0 0 8px;
  color: #66584f;
  line-height: 1.8;
  font-size: 14px;
}

.actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .page {
    padding: 24px 14px 42px;
  }

  .grid {
    grid-template-columns: 1fr;
  }

  .main-card {
    padding: 20px;
  }
}
</style>
