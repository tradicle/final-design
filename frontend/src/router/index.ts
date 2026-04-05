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
import ClueReport from '../views/ClueReport.vue'
import Donate from '../views/Donate.vue'
import Community from '../views/Community.vue'
import Knowledge from '../views/Knowledge.vue'
import KnowledgeDetail from '../views/KnowledgeDetail.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '/news', component: News },
    { path: '/animals', component: AnimalList },
    { path: '/pet/:id', component: PetDetail },
    { path: '/adoption/rules', component: AdoptionRules },
    { path: '/adoption/apply', component: AdoptionApply },
    { path: '/about', component: About },
    { path: '/clue', component: ClueReport },
    { path: '/donate', component: Donate },
    { path: '/community', component: Community },
    { path: '/knowledge', component: Knowledge },
    { path: '/knowledge/:id', component: KnowledgeDetail },
    { path: '/admin', component: AdminPanel },
  ],
})
