<script setup lang="ts"></script>

<template>
  <div class="page">
    <el-card class="hero-card">
      <h2>志愿者招募</h2>
      <p>
        对于无数等待帮助的流浪猫来说，志愿者的参与就是改变命运的起点。欢迎加入我们的志愿者队伍，
        用稳定、长期的行动帮助毛孩子从流浪走向安稳。
      </p>
    </el-card>

    <div class="grid">
      <el-card class="volunteer-card">
        <h3>寄养志愿者（限北京地区）</h3>
        <p>为流浪猫提供临时家庭环境，帮助它们恢复健康和信任，提升领养成功率。</p>
      </el-card>
      <el-card class="volunteer-card">
        <h3>场所看护志愿者（限北京地区）</h3>
        <p>参与日常清洁、喂食、护理、互动，保障场所内猫咪稳定生活。</p>
      </el-card>
      <el-card class="volunteer-card">
        <h3>摄影志愿者（限北京地区）</h3>
        <p>为待领养猫咪拍摄高质量照片，展示精神状态和个体特点，提升被看见的机会。</p>
      </el-card>
      <el-card class="volunteer-card">
        <h3>其他志愿者（交通 / 设计 / 活动 / 宣传）</h3>
        <p>根据阶段任务开放招募，适合有专项技能和可持续投入时间的志愿者。</p>
      </el-card>
    </div>

    <el-alert type="info" :closable="false" class="tip">
      <template #title>地区说明</template>
      目前与领养项目和场所看护相关的线下志愿者岗位主要面向北京地区，其他地区可联系当地动保团队参与。
    </el-alert>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
  max-width: 1000px;
  margin: 0 auto;
}
.hero-card {
  margin-bottom: 20px;
}
.hero-card h2 {
  margin: 0 0 10px;
  color: #303133;
}
.hero-card p {
  margin: 0;
  color: #606266;
  line-height: 1.8;
}
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}
.volunteer-card h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #303133;
}
.volunteer-card p {
  margin: 0;
  color: #606266;
  line-height: 1.7;
}
.tip {
  margin-top: 6px;
}
@media (max-width: 768px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
