<script setup lang="ts">
import { ref } from 'vue'

// Mock donation records
const donationRecords = ref([
  { id: 1, donor: '张**', item: '幼猫猫粮 10kg', date: '2026-03-01' },
  { id: 2, donor: '李**', item: '宠物尿垫 2包', date: '2026-02-28' },
  { id: 3, donor: '爱心人士', item: '义工服务 4小时', date: '2026-02-25' },
  { id: 4, donor: '王**', item: '旧衣物 一批', date: '2026-02-20' },
  { id: 5, donor: '赵**', item: '罐头 24罐', date: '2026-02-18' },
])
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="header">
        <h1>爱心捐赠</h1>
        <p class="subtitle">您的每一次援手，都是它们生命中的光</p>
      </div>

      <div class="content-wrapper">
        <!-- Donation Scope -->
        <div class="section scope-section">
          <div class="section-header">
            <h2>捐助范围</h2>
          </div>
          <div class="scope-content">
            <div class="scope-item">
              <div class="index">1</div>
              <div class="text">
                我们非常乐意接受爱心朋友们经常性的捐助，但仅限于小猫小狗们的
                <strong>食物、衣物、玩具等实物</strong>。
              </div>
            </div>
            <div class="scope-item">
              <div class="index">2</div>
              <div class="text">
                我们也非常希望爱心朋友们能为小猫小狗们
                <strong>捐出您的时间</strong>（如参与志愿者服务）。
              </div>
            </div>
            <div class="scope-item highlight">
              <div class="index">3</div>
              <div class="text">
                我们暂 <strong>不接受现金</strong> 的捐助。
              </div>
            </div>
            <div class="note">
              备注: 我们会对您的捐助做好记录，并定期在网站上公布。
            </div>
          </div>
        </div>

        <!-- Contact Info -->
        <div class="section contact-section">
          <div class="section-header">
            <h2>联系方式</h2>
          </div>
          <div class="contact-card">
            <p><strong>物资接收地址：</strong> 某某市某某区流浪动物救助中心</p>
            <p><strong>收件人：</strong> 汪汪喵呜物资组</p>
            <p><strong>联系电话：</strong> 010-12345678</p>
            <p><strong>志愿者报名：</strong> 请访问“志愿者”板块</p>
          </div>
        </div>

        <!-- Donation Records -->
        <div class="section records-section">
          <div class="section-header">
            <h2>物品捐助公示</h2>
            <span class="sub-text">我们诚挚地感谢以下捐助者和捐助团体</span>
          </div>
          
          <el-table :data="donationRecords" stripe style="width: 100%" class="records-table">
            <el-table-column prop="date" label="日期" width="180" />
            <el-table-column prop="donor" label="捐助者" width="180" />
            <el-table-column prop="item" label="捐助物品/内容" />
          </el-table>
          
          <div class="pagination">
            <el-pagination background layout="prev, pager, next" :total="50" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 40px 20px;
  background-color: #f9f9f9;
  min-height: 100vh;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 50px;
}

.header h1 {
  font-size: 36px;
  color: #333;
  margin-bottom: 15px;
  letter-spacing: 2px;
}

.subtitle {
  font-size: 16px;
  color: #666;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.section {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.section-header {
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: baseline;
  gap: 15px;
}

.section-header h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
  border-left: 5px solid var(--el-color-primary);
  padding-left: 15px;
}

.sub-text {
  color: #999;
  font-size: 14px;
}

/* Scope Section */
.scope-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.scope-item {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.scope-item .index {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  font-weight: bold;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.scope-item .text {
  font-size: 16px;
  line-height: 1.8;
  color: #555;
  padding-top: 2px;
}

.scope-item.highlight .text {
  color: #f56c6c;
}

.note {
  margin-top: 20px;
  background-color: #fff8e6;
  padding: 15px;
  border-radius: 6px;
  color: #e6a23c;
  font-size: 14px;
}

/* Contact Section */
.contact-card {
  background-color: #f8f9fa;
  padding: 25px;
  border-radius: 8px;
  border: 1px dashed #dcdfe6;
}

.contact-card p {
  margin-bottom: 12px;
  color: #606266;
  font-size: 15px;
}

.contact-card p:last-child {
  margin-bottom: 0;
}

/* Records Section */
.records-table {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .section {
    padding: 20px;
  }
}
</style>
