import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import { router } from './router'
import { pinia } from './store'

const app = createApp(App)
app.use(ElementPlus)
app.use(pinia)
app.use(router)

router.isReady().then(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    import('./api/user').then(({ getProfile }) => {
      getProfile().then((res) => {
        if (res.code !== 0) {
          localStorage.removeItem('user')
        }
      }).catch(() => {
        localStorage.removeItem('user')
      })
    })
  }
})

app.mount('#app')
