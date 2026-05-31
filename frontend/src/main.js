import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import 'bootstrap-icons/font/bootstrap-icons.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './styles/global.css'

// 必须：Edge/Chromium 焦点 bug 修复
;(function patchHistoryFocusBug() {
  const origReplaceState = window.history.replaceState.bind(window.history)
  const origPushState = window.history.pushState.bind(window.history)
  window.history.replaceState = function () {
    if (document.hidden) return
    return origReplaceState.apply(this, arguments)
  }
  window.history.pushState = function () {
    if (document.hidden) return
    return origPushState.apply(this, arguments)
  }
})()

const app = createApp(App)

// 必须：全量注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
