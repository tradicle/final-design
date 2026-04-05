<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

function handleAgree() {
  if (route.query.from === 'apply' && route.query.animalId) {
    router.push({ path: '/adoption/apply', query: { animalId: route.query.animalId } })
  } else {
    router.push('/animals')
  }
}
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="header">
        <h1>领养须知</h1>
        <p class="subtitle">请您在申请领养前仔细阅读以下条款，这不仅是对生命的尊重，也是对彼此的承诺。</p>
      </div>

      <div class="content">
        <div class="rule-section">
          <h3><span class="icon">01</span> 基本条件</h3>
          <ul>
            <li>领养人必须年满21岁，有稳定的住所、工作及较好的经济收入。</li>
            <li>不接受异地领养，只接受本市及周边地区领养，便于联系和回访。</li>
            <li>领养时需提供身份证原件与复印件、电话号码、微信号和详细的家庭住址。</li>
          </ul>
        </div>

        <div class="rule-section">
          <h3><span class="icon">02</span> 费用说明</h3>
          <ul>
            <li>需缴纳领养押金（小型犬RMB300元/只，大型犬RMB500元/只，猫咪RMB100元/只)，由领养之家开具收据。</li>
            <li>如领养半年后宠物健康的被带回领养之家体检合格，需凭此收据退还押金；如果半年内宠物丢失或死亡，我们将不退还押金。</li>
            <li>对于领养还没有做绝育的小动物，我们将另加收绝育保证金350元/只。小动物年满六个月后，领养之家将给于免费绝育，收回收据后全数退回绝育保证金。</li>
          </ul>
        </div>

        <div class="rule-section">
          <h3><span class="icon">03</span> 安全保障</h3>
          <ul>
            <li>为防止宠物发生坠楼、跑丢等意外情况，在领养前请自觉接受工作人员对领养家庭窗户、阳台或入户花园的安全检查。</li>
            <li>对于有安全隐患的地方需用网格封起来。没有安全隐患后方可领养。</li>
          </ul>
        </div>

        <div class="rule-section">
          <h3><span class="icon">04</span> 回访与沟通</h3>
          <ul>
            <li>领养人应加入我们领养人的微信群，方便相互及时沟通了解宠物近况、身体情况等。</li>
            <li>领养人要不定期的发被领养宠物的照片或视频到群里。如果长时间领养人没有发宠物近况，我们会主动与之联系，有可能需要上门回访。</li>
            <li>如遇领养人搬家（包括离开本市去其他城市、回老家）搬家后的新地址及联系方式需要告诉我们以便及时更新。</li>
          </ul>
        </div>

        <div class="rule-section">
          <h3><span class="icon">05</span> 医疗与健康</h3>
          <ul>
            <li>领养后发现生病需及时带到附近宠物医院或到与我们合作的宠物医院进行及时治疗。</li>
            <li>领养后为了您和家人及宠物的健康，请每年定期将其带到宠物医院或领养之家，我们将免费为其注射疫苗。</li>
            <li>如遇宠物遗失、跑丢等情况，请第一时间与我们联系（以免耽误找寻的最佳时间）。</li>
          </ul>
        </div>

        <div class="actions">
          <el-button type="primary" size="large" @click="handleAgree">我已阅读并同意，去领养</el-button>
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
  max-width: 900px;
  margin: 0 auto;
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.header {
  text-align: center;
  margin-bottom: 40px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

.header h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  color: #666;
  font-size: 16px;
}

.rule-section {
  margin-bottom: 30px;
}

.rule-section h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.icon {
  background: var(--el-color-primary);
  color: #fff;
  font-size: 14px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  font-weight: bold;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

li {
  position: relative;
  padding-left: 20px;
  margin-bottom: 10px;
  color: #555;
  line-height: 1.6;
  font-size: 15px;
}

li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  width: 6px;
  height: 6px;
  background: #ddd;
  border-radius: 50%;
}

.actions {
  text-align: center;
  margin-top: 50px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}
</style>
