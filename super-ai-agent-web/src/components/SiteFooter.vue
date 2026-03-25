<template>
  <footer class="site-footer">
    <div class="footer-divider"></div>
    <div class="footer-content">
      <div class="footer-brand">Super AI Agent</div>
      <div class="footer-links">
        <a
          href="https://github.com/muonuo"
          target="_blank"
          rel="noopener"
          class="footer-link"
          title="GitHub"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path
              d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"
            />
          </svg>
          <span class="link-text">GitHub</span>
        </a>
        <a
          href="https://gitee.com/monks-offering"
          target="_blank"
          rel="noopener"
          class="footer-link"
          title="Gitee"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path
              d="M10.324 19.541h5.587v-7.036h-3.255l.536-3.607-4.424.001 1.003 6.33-2.538 2.131a2.328 2.328 0 0 1-.26-.119 2.328 2.328 0 0 1-.26.119l-2.537-2.131 1.002-6.33h-4.424l.536 3.607h-3.255v7.036h5.587l-.428 2.139h1.867l-.428-2.139zm-1.866-9.284h2.336l-.001 5.936 2.338-2.338h-2.338l-2.335 2.338V10.257zM2.217 4.166l5.342-3.333 5.342 3.333-5.342 3.333-5.342-3.333zM1.459 4.821l6.1 3.801-2.995 1.567-3.105-5.368zm7.544 5.368l-3.105 5.368-2.995-1.567 6.1-3.801h-.001zm5.774-3.567l-5.342-3.333-5.342 3.333 5.342 3.333 5.342-3.333z"
            />
          </svg>
          <span class="link-text">Gitee</span>
        </a>
      </div>

      <div class="footer-copyright">
        <span class="copyright-text">Made with</span>
        <span class="heart">♥</span>
        <span class="copyright-text">by</span>
        <a href="https://github.com/muonuo" target="_blank" class="brand-name"
          >Super AI Agent</a
        >
        <span class="divider">·</span>
        <span class="version">v1.0.0</span>
        <span class="divider">·</span>
        <span class="copyright-text">© {{ currentYear }}</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from "vue";

const currentYear = computed(() => new Date().getFullYear());
</script>

<style scoped>
.site-footer {
  padding: 16px 24px 0px;
  background: transparent;
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  position: relative;
}

.footer-divider {
  position: absolute;
  top: 25px;
  left: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(167, 139, 250, 0.6) 20%,
    rgba(192, 132, 252, 0.8) 50%,
    rgba(167, 139, 250, 0.6) 80%,
    transparent 100%
  );
  box-shadow: 0 0 10px rgba(167, 139, 250, 0.3);
}

.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
  position: relative;
  padding-top: 70px;
}

.footer-links {
  display: flex;
  gap: 20px;
}

.footer-brand {
  font-size: 28px;
  font-weight: 700;
  color: #818cf8;
  letter-spacing: 1px;
}

.footer-link {
  color: #c9d1d9;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  text-decoration: none;
}

.link-text {
  font-size: 16px;
  font-weight: 500;
}

.footer-link:hover {
  color: #919bf3;
}

.footer-copyright {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  flex-wrap: wrap;
}

.copyright-text {
  color: #a1a1aa;
  font-weight: 400;
}

.version {
  color: #a78bfa;
  font-weight: 500;
}

.heart {
  display: inline-block;
  color: #f43f5e;
  animation: heartbeat 1.5s ease-in-out infinite;
  font-size: 14px;
}

@keyframes heartbeat {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

.brand-name {
  color: #c084fc;
  font-weight: 600;
  text-decoration: none;
  background: linear-gradient(135deg, #c084fc 0%, #f472b6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: all 0.3s ease;
}

.brand-name:hover {
  opacity: 0.8;
  text-shadow: 0 0 10px rgba(192, 132, 252, 0.5);
}

.divider {
  color: #52525b;
  margin: 0 2px;
}

/* 响应式 */
@media (max-width: 768px) {
  .site-footer {
    padding: 12px 16px 16px;
  }

  .footer-copyright {
    font-size: 12px;
  }

  .tagline-text {
    font-size: 10px;
  }

  .footer-link {
    padding: 6px;
  }

  .footer-link svg {
    width: 16px;
    height: 16px;
  }
}

/* 减少动画 */
@media (prefers-reduced-motion: reduce) {
  .heart {
    animation: none;
  }

  .footer-link:hover {
    transform: none;
  }
}
</style>
