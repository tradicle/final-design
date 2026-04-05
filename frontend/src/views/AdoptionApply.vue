<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAnimalDetail, type Animal } from '../api/animal'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const animal = ref<Animal | null>(null)

const form = reactive({
  applicantName: '',
  age: 25,
  job: '',
  income: '',
  address: '',
  phone: '',
  wechat: '',
  housing: 'OWN', // OWN, RENT
  experience: 'NONE', // NONE, HAD, HAVE
  familyMembers: '',
  reason: '',
})

async function loadAnimal() {
  const id = route.query.animalId
  if (!id) return
  try {
    const res = await getAnimalDetail(Number(id))
    if (res.code === 0) {
      animal.value = res.data
    }
  } catch (e) {
    // ignore
  }
}

async function onSubmit() {
  if (!form.applicantName || !form.phone || !form.address) {
    ElMessage.warning('请填写必要信息')
    return
  }
  
  loading.value = true
  try {
    // Simulate API call
    await new Promise((r) => setTimeout(r, 800))
    ElMessage.success('申请已提交，我们会尽快联系您审核！')
    router.push('/animals')
  } catch (e) {
    ElMessage.error('提交失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadAnimal)
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="header">
        <h1>领养申请</h1>
        <p v-if="animal">您正在申请领养：<span class="pet-name">{{ animal.name }}</span> (编号: {{ animal.animalNo || animal.id }})</p>
      </div>

      <el-form label-width="120px" class="apply-form" size="large">
        <div class="section-title">基本信息</div>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" required>
              <el-input v-model="form.applicantName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input-number v-model="form.age" :min="18" :max="80" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职业">
              <el-input v-model="form.job" placeholder="请输入职业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="月收入">
              <el-select v-model="form.income" placeholder="请选择收入范围">
                <el-option label="3000以下" value="LEVEL1" />
                <el-option label="3000-8000" value="LEVEL2" />
                <el-option label="8000-15000" value="LEVEL3" />
                <el-option label="15000以上" value="LEVEL4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="section-title">联系方式</div>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号码" required>
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="微信号">
              <el-input v-model="form.wechat" placeholder="方便回访联系" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细住址" required>
          <el-input v-model="form.address" placeholder="请输入详细家庭住址" />
        </el-form-item>

        <div class="section-title">家庭与经验</div>

        <el-form-item label="住房情况">
          <el-radio-group v-model="form.housing">
            <el-radio label="OWN">自有住房</el-radio>
            <el-radio label="RENT">租房（已获房东同意）</el-radio>
            <el-radio label="OTHER">其他</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="家庭成员">
          <el-input v-model="form.familyMembers" placeholder="家庭成员构成及他们对养宠的态度" />
        </el-form-item>

        <el-form-item label="养宠经验">
          <el-radio-group v-model="form.experience">
            <el-radio label="NONE">无经验</el-radio>
            <el-radio label="HAD">曾经养过</el-radio>
            <el-radio label="HAVE">目前正在养</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="申请理由">
          <el-input 
            v-model="form.reason" 
            type="textarea" 
            :rows="4" 
            placeholder="请简述您的领养理由，以及对未来的养宠规划（如喂养、医疗、不离不弃的承诺等）" 
          />
        </el-form-item>

        <div class="form-footer">
          <el-button @click="router.back()">返回</el-button>
          <el-button type="primary" :loading="loading" @click="onSubmit">提交申请</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 40px 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  padding: 40px 60px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.header {
  text-align: center;
  margin-bottom: 40px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

.header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.pet-name {
  color: var(--el-color-primary);
  font-weight: bold;
  font-size: 18px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 20px 0 15px;
  padding-left: 10px;
  border-left: 4px solid var(--el-color-primary);
}

.form-footer {
  margin-top: 40px;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>
