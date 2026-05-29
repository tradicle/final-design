import { createRouter, createWebHistory } from 'vue-router'

import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import AnimalList from '../views/AnimalList.vue'
import PetDetail from '../views/PetDetail.vue'
import AdoptionRules from '../views/AdoptionRules.vue'
import AdoptionApply from '../views/AdoptionApply.vue'
import AdminPanel from '../views/AdminPanel.vue'
import News from '../views/News.vue'
import About from '../views/About.vue'
import Donate from '../views/Donate.vue'
import Community from '../views/Community.vue'
import Knowledge from '../views/Knowledge.vue'
import KnowledgeDetail from '../views/KnowledgeDetail.vue'
import AccountCenter from '../views/AccountCenter.vue'
import Transparency from '../views/Transparency.vue'
import NewsDetail from '../views/NewsDetail.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import AdminAnimals from '../views/AdminAnimals.vue'
import AdminNews from '../views/AdminNews.vue'
import AdminCommunity from '../views/AdminCommunity.vue'
import AdminAdoption from '../views/AdminAdoption.vue'
import AdminDonationClaim from '../views/AdminDonationClaim.vue'
import AdminContent from '../views/AdminContent.vue'
import Activities from '../views/Activities.vue'
import ActivityDetail from '../views/ActivityDetail.vue'
import AdminActivities from '../views/AdminActivities.vue'

export const router = createRouter({
  history: createWebHistory(),
  scrollBehavior() {
    return { top: 0 }
  },
  routes: [
    { path: '/', component: Home },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '/news', component: News },
    { path: '/news/:id', component: NewsDetail },
    { path: '/activities', component: Activities },
    { path: '/activities/:id', component: ActivityDetail },
    { path: '/animals', component: AnimalList },
    { path: '/pet/:id', component: PetDetail },
    { path: '/adoption/rules', component: AdoptionRules },
    { path: '/adoption/apply', component: AdoptionApply, meta: { requiresAuth: true } },
    { path: '/about', component: About },
    { path: '/donate', component: Donate },
    { path: '/community', component: Community },
    {
      path: '/knowledge',
      component: Knowledge,
      children: [
        { path: ':id', component: KnowledgeDetail },
      ],
    },
    { path: '/account', component: AccountCenter, meta: { requiresAuth: true } },
    { path: '/transparency', component: Transparency },
    {
      path: '/admin',
      component: AdminPanel,
      redirect: '/admin/dashboard',
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: 'dashboard', component: AdminDashboard },
        { path: 'animals', component: AdminAnimals },
        { path: 'news', component: AdminNews },
        { path: 'activities', component: AdminActivities },
        { path: 'community', component: AdminCommunity },
        { path: 'adoption', component: AdminAdoption },
        { path: 'donation-claim', component: AdminDonationClaim },
        { path: 'content', component: AdminContent },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null

  if (to.meta.requiresAuth && !user) {
    next('/login?redirect=' + encodeURIComponent(to.fullPath))
    return
  }
  if (to.meta.requiresAdmin && user?.role !== 'ADMIN') {
    next('/')
    return
  }
  next()
})
