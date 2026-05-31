/**
 * 前端埋点工具
 * 记录页面浏览、事件点击等行为
 */

const TRACKER_ENDPOINT = '/api/tracker/collect'

/**
 * 获取访客 ID（基于 localStorage 持久化）
 */
function getVisitorId() {
  let id = localStorage.getItem('blog_visitor_id')
  if (!id) {
    id = 'visitor_' + Date.now().toString(36) + '_' + Math.random().toString(36).slice(2, 8)
    localStorage.setItem('blog_visitor_id', id)
  }
  return id
}

/**
 * 发送埋点事件
 */
function track(event, data = {}) {
  const payload = {
    visitorId: getVisitorId(),
    event,
    data,
    url: window.location.href,
    referrer: document.referrer || '',
    timestamp: Date.now(),
    userAgent: navigator.userAgent,
  }

  // 使用 sendBeacon 确保页面关闭时也能发送
  if (navigator.sendBeacon) {
    navigator.sendBeacon(TRACKER_ENDPOINT, JSON.stringify(payload))
  } else {
    fetch(TRACKER_ENDPOINT, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      keepalive: true,
    }).catch(() => { /* 静默失败 */ })
  }
}

/**
 * 页面浏览埋点
 */
function trackPageView(pageName) {
  track('page_view', { page: pageName })
}

/**
 * 文章阅读埋点
 */
function trackArticleView(slug, title) {
  track('article_view', { slug, title })
}

/**
 * 点击事件埋点
 */
function trackClick(element, label) {
  track('click', { element, label })
}

export default {
  track,
  trackPageView,
  trackArticleView,
  trackClick,
  getVisitorId,
}
