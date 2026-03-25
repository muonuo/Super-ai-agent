<template>
  <router-view v-slot="{ Component }">
    <transition :name="transitionName" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const transitionName = ref('fade-star')

// 监听路由变化，可以根据需要动态切换动画效果
watch(() => route.path, (newPath) => {
  if (newPath.startsWith('/love')) {
    transitionName.value = 'fade-star'
  } else {
    transitionName.value = 'fade-star'
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  min-height: 100vh;
}

/* 滑动渐变转场动画 - 自然平滑 */
.fade-star-enter-active,
.fade-star-leave-active {
  transition: all 0.45s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.fade-star-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-star-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* 转场过程中的背景 */
.fade-star-enter-active::before,
.fade-star-leave-active::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a0a1f 0%, #2d1b3d 50%, #1a1025 100%);
  z-index: 9999;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.fade-star-enter-active::before,
.fade-star-leave-active::before {
  opacity: 1;
}

.fade-star-leave-active::before {
  animation: fadeBgOut 0.45s cubic-bezier(0.25, 0.1, 0.25, 1) forwards;
}

.fade-star-enter-active::before {
  animation: fadeBgIn 0.45s cubic-bezier(0.25, 0.1, 0.25, 1) forwards;
}

@keyframes fadeBgIn {
  0% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}

@keyframes fadeBgOut {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

/* 星星效果 */
.stars-transition {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9998;
  overflow: hidden;
}

.star-particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: white;
  border-radius: 50%;
  animation: starTwinkle 1s ease-in-out infinite;
}

@keyframes starTwinkle {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.5); }
}

/* 减少动画 - 无障碍支持 */
@media (prefers-reduced-motion: reduce) {
  .fade-star-enter-active,
  .fade-star-leave-active {
    transition: opacity 0.2s ease;
  }

  .fade-star-enter-from,
  .fade-star-leave-to {
    transform: none;
  }

  .fade-star-enter-active::before,
  .fade-star-leave-active::before {
    animation: none;
    opacity: 0;
  }
}
</style>
