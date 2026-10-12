import { onBeforeUnmount, onMounted, ref } from 'vue'

/**
 * 液态玻璃的鼠标响应（面板位置固定，不做拖拽）
 *
 * 提供两件事：
 *  1) 镜面高光跟随鼠标：坐标经 lerp 缓动写入 CSS 变量 --mx / --my → 高光有"拖尾"感
 *  2) 折射强度随指针移动速度脉动：快速划过时玻璃"晃"得更厉害，静止后回落
 *
 * 性能约束（来自开源实现的经验，见 LiquidGlassFilter.vue 注释）：
 *  - 逐帧只写 CSS 变量、不做 layout 读写 → 不触发重排
 *  - SVG 滤镜强度更新节流到 ≤20 次/秒（feDisplacementMap 每改一次都要重算滤镜）
 *  - prefers-reduced-motion 下不做折射脉动
 *
 * @param {Object} opts
 * @param {number} opts.baseScale 折射基础强度（与 SVG 滤镜的 scaleR 对齐）
 */
export function useLiquidGlass({ baseScale = 18 } = {}) {
  const panelRef = ref(null)
  const hovered = ref(false)

  // 鼠标跟随高光：目标值 + 缓动值（差值即拖尾长度）
  let targetX = 100
  let targetY = 160
  let glowX = targetX
  let glowY = targetY
  // 折射强度：基础值 → 移动时上浮 → 静止回落
  let scaleNow = baseScale
  let scaleTarget = baseScale
  let rafId = 0
  let lastMoveTs = 0
  let lastScaleTs = 0
  let idleTimer = 0

  const reduced = () => window.matchMedia('(prefers-reduced-motion: reduce)').matches

  function applyVars() {
    const el = panelRef.value
    if (!el) return
    el.style.setProperty('--mx', glowX.toFixed(1) + 'px')
    el.style.setProperty('--my', glowY.toFixed(1) + 'px')
    el.style.setProperty('--lg-glow', hovered.value ? '1' : '0')
  }

  function tick() {
    rafId = requestAnimationFrame(tick)
    // 高光缓动跟随（0.30 = 跟手较紧；调小 → 拖尾更长、更像光晕）
    glowX += (targetX - glowX) * 0.30
    glowY += (targetY - glowY) * 0.30
    // 折射强度缓动
    scaleNow += (scaleTarget - scaleNow) * 0.12
    applyVars()
    // 节流写入 SVG 滤镜强度（≤20 次/秒）
    const now = performance.now()
    if (now - lastScaleTs > 50) {
      lastScaleTs = now
      const f = document.getElementById('lg-refraction')
      const disp = f && f.querySelectorAll('feDisplacementMap')
      if (disp && disp.length === 3) {
        disp[0].setAttribute('scale', (scaleNow * 1.0).toFixed(1))
        disp[1].setAttribute('scale', (scaleNow * 0.78).toFixed(1))
        disp[2].setAttribute('scale', (scaleNow * 0.67).toFixed(1))
      }
    }
  }

  function onPointerMove(e) {
    const el = panelRef.value
    if (!el) return
    const rect = el.getBoundingClientRect()
    targetX = e.clientX - rect.left
    targetY = e.clientY - rect.top
    const now = performance.now()
    const dt = Math.max(16, now - lastMoveTs)
    lastMoveTs = now
    // 移动速度 → 折射强度（带上限，避免过曝）
    if (!reduced()) scaleTarget = Math.min(baseScale * 2.2, baseScale + 900 / dt)
    clearTimeout(idleTimer)
    idleTimer = window.setTimeout(() => { scaleTarget = baseScale }, 260)
  }

  function onPointerEnter() { hovered.value = true }
  function onPointerLeave() {
    hovered.value = false
    scaleTarget = baseScale
  }

  onMounted(() => {
    tick()
    const el = panelRef.value
    if (!el) return
    el.addEventListener('pointerenter', onPointerEnter)
    el.addEventListener('pointerleave', onPointerLeave)
    el.addEventListener('pointermove', onPointerMove)
    applyVars()
  })

  onBeforeUnmount(() => {
    cancelAnimationFrame(rafId)
    clearTimeout(idleTimer)
    const el = panelRef.value
    if (el) {
      el.removeEventListener('pointerenter', onPointerEnter)
      el.removeEventListener('pointerleave', onPointerLeave)
      el.removeEventListener('pointermove', onPointerMove)
    }
  })

  return { panelRef, hovered }
}
