<script setup lang="ts">
import { computed, reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUrgentNeeds, type UrgentNeed } from '../api/donation'
import { createDonationClaim } from '../api/donation-claim'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const urgentNeeds = ref<UrgentNeed[]>([])
const claimDialogVisible = ref(false)
const submitting = ref(false)
const submittedNeedIds = ref<number[]>([])
const currentNeed = ref<UrgentNeed | null>(null)
const currentPage = ref(1)
const pageSize = 10

const claimForm = reactive({
  quantity: '',
  contactName: '',
  phone: '',
  wechat: '',
  pickupDate: '',
  remark: '',
})

interface DonationRecord {
  date: string
  donor: string
  item: string
  quantity: number
  unit: string
  remark: string
}

const donationRecords: DonationRecord[] = [
  { date: '2026-05-28', donor: '李明月', item: '皇家猫粮K36', quantity: 4, unit: '袋', remark: '幼猫专用' },
  { date: '2026-05-27', donor: '深圳宠物爱心社', item: '豆腐猫砂', quantity: 20, unit: '箱', remark: '原味' },
  { date: '2026-05-26', donor: '王建国', item: '犬用体内驱虫药', quantity: 50, unit: '片', remark: '中型犬剂量' },
  { date: '2026-05-25', donor: '陈小雅', item: '宠物尿垫', quantity: 12, unit: '包', remark: 'L码加厚款' },
  { date: '2026-05-24', donor: '张伟强', item: '顽皮鲜肉猫条', quantity: 30, unit: '盒', remark: '混合口味' },
  { date: '2026-05-23', donor: '南山义工联', item: '宠物消毒液', quantity: 15, unit: '瓶', remark: '宠乐安品牌' },
  { date: '2026-05-22', donor: '赵雨婷', item: '猫抓板', quantity: 10, unit: '个', remark: '大号瓦楞纸' },
  { date: '2026-05-21', donor: '匿名爱心人士', item: '比瑞吉狗粮', quantity: 6, unit: '袋', remark: '15kg装' },
  { date: '2026-05-20', donor: '刘思远', item: '宠物毛毯', quantity: 18, unit: '条', remark: '加绒保暖款' },
  { date: '2026-05-19', donor: '喵汪之家志愿者', item: '猫罐头', quantity: 48, unit: '罐', remark: '希宝金罐' },
  { date: '2026-05-18', donor: '周明辉', item: '皇家猫粮K36', quantity: 3, unit: '袋', remark: '成猫款' },
  { date: '2026-05-17', donor: '黄丽华', item: '宠物玩具球', quantity: 25, unit: '个', remark: '橡胶发声球' },
  { date: '2026-05-16', donor: '福田爱宠群', item: '犬用沐浴露', quantity: 8, unit: '瓶', remark: '低敏配方' },
  { date: '2026-05-15', donor: '林俊杰', item: '幼犬奶粉', quantity: 12, unit: '罐', remark: '贝帮品牌' },
  { date: '2026-05-14', donor: '郑晓雯', item: '豆腐猫砂', quantity: 16, unit: '箱', remark: '绿茶味' },
  { date: '2026-05-13', donor: '深圳科技园义工', item: '宠物湿巾', quantity: 30, unit: '包', remark: '无酒精配方' },
  { date: '2026-05-12', donor: '何志鹏', item: '犬用牵引绳', quantity: 14, unit: '条', remark: '中型犬适用' },
  { date: '2026-05-11', donor: '匿名爱心人士', item: '猫粮试吃装', quantity: 60, unit: '份', remark: '多品牌混合' },
  { date: '2026-05-10', donor: '孙婷婷', item: '宠物指甲剪', quantity: 10, unit: '套', remark: '带锉刀' },
  { date: '2026-05-09', donor: '龙华流浪动物救助', item: '妙鲜包', quantity: 36, unit: '袋', remark: '鸡肉味' },
  { date: '2026-05-08', donor: '马天宇', item: '宠物航空箱', quantity: 4, unit: '个', remark: '中号' },
  { date: '2026-05-07', donor: '宝安宠物医院', item: '猫三联疫苗', quantity: 15, unit: '支', remark: '妙三多' },
  { date: '2026-05-06', donor: '吴小燕', item: '宠物食盆', quantity: 20, unit: '个', remark: '不锈钢双盆' },
  { date: '2026-05-05', donor: '匿名爱心人士', item: '犬用磨牙棒', quantity: 40, unit: '包', remark: '中大型犬' },
  { date: '2026-05-04', donor: '罗湖区志愿者', item: '宠物尿垫', quantity: 10, unit: '包', remark: 'XL码' },
  { date: '2026-05-03', donor: '许志强', item: '皇家猫粮K36', quantity: 5, unit: '袋', remark: '10kg装' },
  { date: '2026-05-02', donor: '深圳大学义工社', item: '猫爬架', quantity: 3, unit: '个', remark: '多层实木' },
  { date: '2026-05-01', donor: '蔡佳玲', item: '宠物益生菌', quantity: 24, unit: '盒', remark: '布拉迪酵母' },
  { date: '2026-04-30', donor: '南山社区爱心群', item: '比瑞吉狗粮', quantity: 8, unit: '袋', remark: '小型犬款' },
  { date: '2026-04-29', donor: '韩雨桐', item: '猫薄荷玩具', quantity: 22, unit: '个', remark: '含猫薄荷填充' },
]

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
  return donationRecords.slice(start, start + pageSize)
})

onMounted(async () => {
  try {
    const urgentRes = await getUrgentNeeds()
    if (urgentRes.code === 0) urgentNeeds.value = urgentRes.data
  } catch (e) { console.error(e) }
})
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="main-card">
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

        <div class="section records-section">
          <div class="section-header">
            <h2>物品捐助公示</h2>
            <span class="sub-text">我们诚挚地感谢以下捐助者和捐助团体</span>
          </div>

          <el-table :data="pagedDonationRecords" stripe style="width: 100%" class="records-table">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="donor" label="捐助者" width="160" />
            <el-table-column prop="item" label="物品名称" width="160" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column prop="remark" label="备注" min-width="140" />
          </el-table>

          <div class="pagination">
            <el-pagination
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

.section {
  padding: 0;
}

.section + .section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0e8dd;
}

.section-header {
  margin-bottom: 16px;
  display: flex;
  align-items: baseline;
  gap: 15px;
}

.section-header h2 {
  font-size: 22px;
  color: #333;
  margin: 0;
  border-left: 4px solid var(--el-color-primary);
  padding-left: 12px;
}

.sub-text {
  color: #999;
  font-size: 13px;
}

/* Scope Section */
.scope-content {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.scope-item {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.scope-item .index {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  font-weight: bold;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 14px;
}

.scope-item .text {
  font-size: 15px;
  line-height: 1.7;
  color: #555;
  padding-top: 1px;
}

.scope-item.highlight .text {
  color: #f56c6c;
}

.note {
  margin-top: 8px;
  background-color: #fff8e6;
  padding: 12px 15px;
  border-radius: 6px;
  color: #e6a23c;
  font-size: 13px;
}

.urgent-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 14px;
}

.urgent-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 12px;
  min-height: 170px;
  padding: 18px;
  border: 1px solid #ebe4db;
  border-radius: 10px;
  background: #fffdf9;
}

.urgent-card h3 {
  margin: 0 0 8px;
  color: #5a3e2d;
  font-size: 17px;
}

.urgent-card p {
  margin: 0 0 6px;
  color: #7a685b;
  line-height: 1.6;
  font-size: 14px;
}

.claim-form {
  padding-top: 6px;
}

/* Records Section */
.records-section {
  border-top: 1px solid #f0e8dd;
}

.records-table {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .main-card {
    padding: 20px;
  }
}
</style>
