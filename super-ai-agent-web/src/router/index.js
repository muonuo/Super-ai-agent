import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoveAppView from '../views/LoveAppView.vue'
import ManusView from '../views/ManusView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView,
    meta: { title: 'Super AI Agent - 智能AI应用平台' }
  },
  {
    path: '/love-app',
    name: 'LoveApp',
    component: LoveAppView,
    meta: { title: 'AI恋爱大师 - 智能恋爱咨询助手' }
  },
  {
    path: '/manus',
    name: 'Manus',
    component: ManusView,
    meta: { title: 'Monuo Manus - AI智能体Agent' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 动态标题
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
})

export default router
