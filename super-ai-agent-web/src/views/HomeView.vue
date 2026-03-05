<template>
  <div class="home-container">
    <!-- 动态背景光晕 -->
    <div class="gradient-orb orb-1"></div>
    <div class="gradient-orb orb-2"></div>
    <div class="gradient-orb orb-3"></div>

    <div class="content-wrapper">
      <div class="hero-section">
        <h1 class="title">
          <span class="title-gradient">Super AI Agent</span>
        </h1>
        <p class="subtitle">选择您想要的 AI 助手</p>
      </div>

      <div class="cards-container">
        <!-- AI恋爱大师 -->
        <router-link to="/love-app" class="card card-love">
          <div class="card-glow"></div>
          <div class="card-icon">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12Z"
              />
            </svg>
          </div>
          <h2 class="card-title">AI 恋爱大师</h2>
          <p class="card-desc">智能情感顾问，为您提供恋爱建议和关系咨询</p>
          <div class="card-arrow">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="2"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M13.5 4.5 21 12m0 0-7.5 7.5M21 12H3"
              />
            </svg>
          </div>
        </router-link>

        <!-- AI超级智能体 -->
        <router-link to="/manus" class="card card-manus">
          <div class="card-glow"></div>
          <div class="card-icon">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M9.75 3.104v5.714a2.25 2.25 0 0 1-.659 1.591L5 14.5M9.75 3.104c-.251.023-.501.05-.75.082m.75-.082a24.301 24.301 0 0 1 4.5 0m0 0v5.714c0 .597.237 1.17.659 1.591L19.8 15.3M14.25 3.104c.251.023.501.05.75.082M19.8 15.3l-1.57.393A9.065 9.065 0 0 1 12 15a9.065 9.065 0 0 0-6.23.693L5 14.5m14.8.8 1.402 1.402c1.232 1.232.65 3.318-1.067 3.611A48.309 48.309 0 0 1 12 21c-2.773 0-5.491-.235-8.135-.687-1.718-.293-2.3-2.379-1.067-3.61L5 14.5"
              />
            </svg>
          </div>
          <h2 class="card-title">AI 超级智能体</h2>
          <p class="card-desc">全能 AI 助手，帮你完成各种复杂任务</p>
          <div class="card-arrow">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="2"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M13.5 4.5 21 12m0 0-7.5 7.5M21 12H3"
              />
            </svg>
          </div>
        </router-link>
      </div>

      <!-- 项目介绍区域 -->
      <div class="features-section">
        <h3 class="features-title">核心特性</h3>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M3.75 6A2.25 2.25 0 0 1 6 3.75h2.25A2.25 2.25 0 0 1 10.5 6v2.25a2.25 2.25 0 0 1-2.25 2.25H6a2.25 2.25 0 0 1-2.25-2.25V6ZM3.75 15.75A2.25 2.25 0 0 1 6 13.5h2.25a2.25 2.25 0 0 1 2.25 2.25V18a2.25 2.25 0 0 1-2.25 2.25H6A2.25 2.25 0 0 1 3.75 18v-2.25ZM13.5 6a2.25 2.25 0 0 1 2.25-2.25H18A2.25 2.25 0 0 1 20.25 6v2.25A2.25 2.25 0 0 1 18 10.5h-2.25a2.25 2.25 0 0 1-2.25-2.25V6ZM13.5 15.75a2.25 2.25 0 0 1 2.25-2.25H18a2.25 2.25 0 0 1 2.25 2.25V18A2.25 2.25 0 0 1 18 20.25h-2.25A2.25 2.25 0 0 1 13.5 18v-2.25"
                />
              </svg>
            </div>
            <h4 class="feature-title">三层 Agent 架构</h4>
            <p class="feature-desc">
              借鉴 OpenManus，分离思考与行动，实现自我推理的智能体
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z"
                />
              </svg>
            </div>
            <h4 class="feature-title">RAG 向量检索</h4>
            <p class="feature-desc">支持知识库增强回答，提供精准的上下文信息</p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M14.25 6.087c0-.355.186-.676.401-.959.221-.29.349-.634.349-1.003 0-1.036-1.007-1.875-2.25-1.875s-2.25.84-2.25 1.875c0 .369.128.713.349 1.003.215.283.401.604.401.959v0a.64.64 0 0 1-.657.643 48.39 48.39 0 0 1-4.163-.3c.186 1.613.293 3.25.315 4.907a.656.656 0 0 1-.658.663v0a48.282 48.282 0 0 1-.315-4.873c-.037-1.081-.155-2.634-.397-4.872-.33.104-.68.167-1.072.21-.372.043-.75-.062-.97-.25-.22-.188-.352-.46-.352-.772v0c0-.355.186-.676.401-.959.221-.29.349-.634.349-1.003 0-1.036-1.007-1.875-2.25-1.875s-2.25.84-2.25 1.875c0 .369.128.713.349 1.003.215.283.401.604.401.959v0c0 .303.152.588.395.775a.622.622 0 0 0 .644.059c1.35-.342 2.443-.585 3.275-.702-.046-.642-.11-1.33-.11-2.083 0-2.95.94-5.577 2.625-7.69.345-.432 1.004-.6 1.631-.472m-5.456 6.225a42.112 42.112 0 0 1 2.108-.403c.615 0 1.222.082 1.813.245a42.18 42.18 0 0 1 1.675.297v.484a21.372 21.372 0 0 0-.54-.263 23.132 23.132 0 0 0-1.374-.212m5.328 3.323a48.422 48.422 0 0 0-2.704-.306 48.43 48.43 0 0 0-1.56.16v.398c0 .678-.135 1.327-.392 1.923a23.88 23.88 0 0 1-1.118 1.604 23.62 23.62 0 0 1-1.482 1.058m-6.89-3.457a30.325 30.325 0 0 1 1.79-.567c.628.164 1.2.417 1.695.748a30.26 30.26 0 0 1 1.26 1.004v.276a29.72 29.72 0 0 0-.928-.267 30.168 30.168 0 0 0-1.856-.413c-.627.138-1.217.33-1.76.577Z"
                />
              </svg>
            </div>
            <h4 class="feature-title">MCP 工具集成</h4>
            <p class="feature-desc">
              支持多种工具调用，包括搜索、文件处理、邮件发送等
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke-width="1.5"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M9.813 15.904 9 18.75l-.813-2.846a4.5 4.5 0 0 0-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 0 0 3.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 0 0 3.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 0 0-3.09 3.09ZM18.259 8.715 18 9.75l-.259-1.035a3.375 3.375 0 0 0-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 0 0 2.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 0 0 2.456 2.456L21.75 6l-1.035.259a3.375 3.375 0 0 0-2.456 2.456ZM16.894 20.567 16.5 21.75l-.394-1.183a2.25 2.25 0 0 0-1.423-1.423L13.5 18.75l1.183-.394a2.25 2.25 0 0 0 1.423-1.423l.394-1.183.394 1.183a2.25 2.25 0 0 0 1.423 1.423l1.183.394-1.183.394a2.25 2.25 0 0 0-1.423 1.423Z"
                />
              </svg>
            </div>
            <h4 class="feature-title">多模态交互</h4>
            <p class="feature-desc">支持图片理解、语音交互等多种输入输出形式</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部版权 -->
    <SiteFooter />
  </div>
</template>

<script setup>
import SiteFooter from "../components/SiteFooter.vue";
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap");

.home-container {
  min-height: 100vh;
  padding: 60px 24px 20px;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 50%, #16213e 100%);
  position: relative;
  overflow: hidden;
  font-family:
    "Inter",
    -apple-system,
    BlinkMacSystemFont,
    "Segoe UI",
    sans-serif;
}

/* 动态背景光晕 */
.gradient-orb {
  position: fixed;
  z-index: 0;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 20s ease-in-out infinite;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #ec4899 0%, #f43f5e 100%);
  top: 50%;
  right: -150px;
  transform: translateY(-50%);
  animation-delay: -7s;
}

.orb-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%);
  bottom: -50px;
  left: 30%;
  animation-delay: -14s;
}

@keyframes float {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(30px, -30px) scale(1.05);
  }
  50% {
    transform: translate(-20px, 20px) scale(0.95);
  }
  75% {
    transform: translate(20px, 30px) scale(1.02);
  }
}

.content-wrapper {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
}

.hero-section {
  text-align: center;
  margin-bottom: 60px;
}

.title {
  font-size: 56px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 16px;
  letter-spacing: -1px;
}

.title-gradient {
  background: linear-gradient(135deg, #6366f1 0%, #ec4899 50%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 16px;
  color: #a1a1aa;
  letter-spacing: 3px;
  text-transform: uppercase;
  font-weight: 500;
}

.cards-container {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 80px;
  flex-wrap: wrap;
}

.card {
  flex: 1;
  min-width: 320px;
  max-width: 420px;
  padding: 48px 36px;
  border-radius: 24px;
  text-decoration: none;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  cursor: pointer;
}

.card-love {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(251, 113, 133, 0.2);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
}

.card-manus {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(167, 139, 250, 0.2);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 24px;
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
}

.card-love .card-glow {
  background: radial-gradient(
    circle at 50% 0%,
    rgba(251, 113, 133, 0.3) 0%,
    transparent 70%
  );
}

.card-manus .card-glow {
  background: radial-gradient(
    circle at 50% 0%,
    rgba(167, 139, 250, 0.3) 0%,
    transparent 70%
  );
}

.card:hover {
  transform: translateY(-12px);
}

.card-love:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(251, 113, 133, 0.5);
  box-shadow: 0 20px 60px rgba(251, 113, 133, 0.25);
}

.card-manus:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(167, 139, 250, 0.5);
  box-shadow: 0 20px 60px rgba(167, 139, 250, 0.25);
}

.card:hover .card-glow {
  opacity: 1;
}

.card-icon {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  position: relative;
  transition: all 0.4s ease;
}

.card-love .card-icon {
  background: linear-gradient(
    135deg,
    rgba(251, 113, 133, 0.2) 0%,
    rgba(225, 29, 72, 0.3) 100%
  );
  border: 1px solid rgba(251, 113, 133, 0.3);
}

.card-manus .card-icon {
  background: linear-gradient(
    135deg,
    rgba(167, 139, 250, 0.2) 0%,
    rgba(124, 58, 237, 0.3) 100%
  );
  border: 1px solid rgba(167, 139, 250, 0.3);
}

.card-icon svg {
  width: 36px;
  height: 36px;
}

.card-love .card-icon svg {
  color: #fb7185;
}
.card-manus .card-icon svg {
  color: #a78bfa;
}

.card:hover .card-icon {
  transform: scale(1.1) rotate(5deg);
}

.card-love:hover .card-icon {
  box-shadow: 0 0 30px rgba(251, 113, 133, 0.4);
}
.card-manus:hover .card-icon {
  box-shadow: 0 0 30px rgba(167, 139, 250, 0.4);
}

.card-title {
  font-size: 28px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
}

.card-desc {
  font-size: 16px;
  color: #a1a1aa;
  line-height: 1.6;
  margin-bottom: 24px;
}

.card-arrow {
  position: absolute;
  bottom: 24px;
  right: 24px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.4s ease;
}

.card-love .card-arrow {
  background: rgba(251, 113, 133, 0.1);
  color: #fb7185;
  border: 1px solid rgba(251, 113, 133, 0.2);
}

.card-manus .card-arrow {
  background: rgba(167, 139, 250, 0.1);
  color: #a78bfa;
  border: 1px solid rgba(167, 139, 250, 0.2);
}

.card:hover .card-arrow {
  transform: translate(6px, -6px) rotate(-10deg);
  background: rgba(255, 255, 255, 0.15);
}

.card-love:hover .card-arrow {
  box-shadow: 0 0 20px rgba(251, 113, 133, 0.4);
}
.card-manus:hover .card-arrow {
  box-shadow: 0 0 20px rgba(167, 139, 250, 0.4);
}

.card-arrow svg {
  width: 20px;
  height: 20px;
}

/* 功能介绍区域 */
.features-section {
  text-align: center;
  margin-bottom: 150px;
}

.features-title {
  font-size: 32px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 48px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 32px 24px;
  text-align: left;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-8px);
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(99, 102, 241, 0.3);
  box-shadow: 0 16px 48px rgba(99, 102, 241, 0.15);
}

.feature-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  background: linear-gradient(
    135deg,
    rgba(99, 102, 241, 0.2) 0%,
    rgba(139, 92, 246, 0.2) 100%
  );
  border: 1px solid rgba(99, 102, 241, 0.3);
  transition: all 0.4s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 0 24px rgba(99, 102, 241, 0.4);
}

.feature-icon svg {
  width: 28px;
  height: 28px;
  color: #818cf8;
}

.feature-title {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 12px;
}

.feature-desc {
  font-size: 14px;
  color: #a1a1aa;
  line-height: 1.6;
}

/* 响应式 */
@media (max-width: 768px) {
  .title {
    font-size: 36px;
  }
  .subtitle {
    font-size: 14px;
    letter-spacing: 2px;
  }
  .cards-container {
    flex-direction: column;
    align-items: center;
  }
  .card {
    width: 100%;
    padding: 36px 28px;
  }
  .features-title {
    font-size: 26px;
  }
  .features-grid {
    grid-template-columns: 1fr;
  }
  .orb-1,
  .orb-2,
  .orb-3 {
    width: 250px;
    height: 250px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .gradient-orb,
  .card,
  .feature-card {
    animation: none;
    transition: none;
  }
}
</style>
