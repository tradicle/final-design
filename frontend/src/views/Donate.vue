<script setup lang="ts">
import { computed, reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDonationRecords, getUrgentNeeds, type DonationRecord, type UrgentNeed } from '../api/donation'
import { createDonationClaim } from '../api/donation-claim'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const donationRecords = ref<DonationRecord[]>([])
const urgentNeeds = ref<UrgentNeed[]>([])
const claimDialogVisible = ref(false)
const submitting = ref(false)
const submittedNeedIds = ref<number[]>([])
const currentNeed = ref<UrgentNeed | null>(null)
const currentPage = ref(1)
const pageSize = 8
const claimForm = reactive({
  quantity: '',
  contactName: '',
  phone: '',
  wechat: '',
  pickupDate: '',
  remark: '',
})

function resetClaimForm() {
  claimForm.quantity = ''
  claimForm.contactName = userStore.user?.nickname || userStore.user?.username || ''
  claimForm.phone = ''
  claimForm.wechat = ''
  claimForm.pickupDate = ''
  claimForm.remark = ''
}

function openClaim(need: UrgentNeed) {
  if (!userStore.user) {
    ElMessage.warning('请先登录后提交认领')
    router.push('/login')
    return
  }
  currentNeed.value = need
  resetClaimForm()
  claimDialogVisible.value = true
}

async function submitClaim() {
  if (!currentNeed.value) return
  if (!claimForm.quantity.trim() || !claimForm.contactName.trim() || !claimForm.phone.trim()) {
    ElMessage.warning('请填写认领数量、联系人和手机号')
    return
  }
  submitting.value = true
  try {
    const res = await createDonationClaim({
      needName: currentNeed.value.name,
      needGap: currentNeed.value.gap,
      quantity: claimForm.quantity.trim(),
      contactName: claimForm.contactName.trim(),
      phone: claimForm.phone.trim(),
      wechat: claimForm.wechat.trim(),
      pickupDate: claimForm.pickupDate.trim(),
      remark: claimForm.remark.trim(),
    })
    if (res.code === 0) {
      submittedNeedIds.value = [...submittedNeedIds.value, currentNeed.value.id]
      claimDialogVisible.value = false
      ElMessage.success('认领提交成功，请等待管理员审核')
    } else {
      ElMessage.error(res.message || '认领提交失败')
    }
  } finally {
    submitting.value = false
  }
}

const pagedDonationRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return donationRecords.value.slice(start, start + pageSize)
})

onMounted(async () => {
  try {
    const [recordsRes, urgentRes] = await Promise.all([getDonationRecords(), getUrgentNeeds()])
    if (recordsRes.code === 0) donationRecords.value = recordsRes.data
    if (urgentRes.code === 0) urgentNeeds.value = urgentRes.data
  } catch (e) { console.error(e) }
})
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
            <div class="scope-item highlight">
              <div class="index">2</div>
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
            <p><strong>物资接收地址：</strong> 深圳市南山区沙河街道睿印商城 B2 层下沉广场喵喵领养小屋</p>
            <p><strong>收件人：</strong> 汪汪喵呜物资组</p>
            <p><strong>联系电话：</strong> 0755-86035169</p>
          </div>
        </div>

        <div class="section urgent-section" v-if="urgentNeeds.length > 0">
          <div class="section-header">
            <h2>急需物资清单</h2>
          </div>
          <div class="urgent-grid">
            <div class="urgent-card" v-for="need in urgentNeeds" :key="need.id">
              <div>
                <h3>{{ need.name }}</h3>
                <p>当前缺口：{{ need.gap }}</p>
                <p>更新时间：{{ need.updatedAt || '待更新' }}</p>
              </div>
              <el-button
                type="primary"
                :disabled="submittedNeedIds.includes(need.id)"
                @click="openClaim(need)"
              >
                {{ submittedNeedIds.includes(need.id) ? '已提交认领' : '我要认领' }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- Donation Records -->
        <div class="section records-section">
          <div class="section-header">
            <h2>物品捐助公示</h2>
            <span class="sub-text">我们诚挚地感谢以下捐助者和捐助团体</span>
          </div>
          
          <el-table :data="pagedDonationRecords" stripe style="width: 100%" class="records-table">
            <el-table-column prop="date" label="日期" width="180" />
            <el-table-column prop="donor" label="捐助者" width="180" />
            <el-table-column prop="item" label="捐助物品/内容" />
          </el-table>
          
          <div class="pagination">
            <el-pagination
              v-if="donationRecords.length > pageSize"
              v-model:current-page="currentPage"
              background
              layout="prev, pager, next"
              :page-size="pageSize"
              :total="donationRecords.length"
            />
          </div>
        </div>
      </div>
    </div>
  </div>

  <el-dialog v-model="claimDialogVisible" title="认领急需物资" width="520px">
    <div class="claim-form">
      <el-form label-width="92px">
        <el-form-item label="物资名称">
          <el-input :model-value="currentNeed?.name || ''" readonly />
        </el-form-item>
        <el-form-item label="当前缺口">
          <el-input :model-value="currentNeed?.gap || ''" readonly />
        </el-form-item>
        <el-form-item label="认领数量">
          <el-input v-model="claimForm.quantity" placeholder="请输入认领数量" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="claimForm.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="claimForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="微信号">
          <el-input v-model="claimForm.wechat" placeholder="选填" />
        </el-form-item>
        <el-form-item label="预计送达">
          <el-input v-model="claimForm.pickupDate" placeholder="选填，例如 2026-06-01" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="claimForm.remark" type="textarea" :rows="3" placeholder="选填" />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <el-button @click="claimDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitClaim">提交认领</el-button>
    </template>
  </el-dialog>
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

.urgent-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.urgent-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 14px;
  min-height: 190px;
  padding: 22px;
  border: 1px solid #ebe4db;
  border-radius: 12px;
  background: #fffaf6;
}

.urgent-card h3 {
  margin: 0 0 10px;
  color: #5a3e2d;
  font-size: 18px;
}

.urgent-card p {
  margin: 0 0 8px;
  color: #7a685b;
  line-height: 1.7;
}

.claim-form {
  padding-top: 6px;
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
