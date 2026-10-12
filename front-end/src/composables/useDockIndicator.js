import { onBeforeUnmount, onMounted, ref } from 'vue'

/**
 * 菜单玻璃"液滴"跟随鼠标（酷安 dock 手感：metaball 液体融合）
 *
 * 物理模型：弹簧-阻尼积分（stiffness / damping），而不是简单 lerp
 *   v += (target - pos) * K * dt      // 弹簧拉力
 *   v -= v * D * dt                   // 阻尼
 *   pos += v * dt                     // 位置积分
 * → 带轻微过冲 + 回弹，就是"液态"的弹性质感
 *
 * 液体形变：按当前速度沿运动方向拉伸、垂直方向压缩（近似体积守恒）
 *   scaleY = 1 + min(|v| * stretch, maxStretch)
 *   scaleX = 1 - min(|v| * stretch * 0.7, maxStretch * 0.7)
 * 同时横向轻微跟随指针（磁吸感，幅度很小，避免飘）
 *
 * 交互规则：
 *  - 指针在菜单上移动 → 选择框跟随指针所在项
 *  - 指针离开菜单 → 弹簧回落到当前路由对应的项（activeIndex）
 *  - 路由切换（未悬停时）→ 同样弹簧回落
 *
 * @param {Object} opts
 * @param {import('vue').Ref<number>} opts.activeIndex 当前路由对应索引
 * @param {number} opts.itemHeight  菜单项高度（40）
 * @param {number} opts.step        菜单项步距（40 + gap 6 = 46）
 */
export function useDockIndicator({ activeIndex, itemHeight = 40, step = 46 } = {}) {
  const menuRef = ref(null)

  // 目标（指针所在项）
  let targetY = 0
  let hoverIndex = -1
  let hovering = false

  // 主液滴（跟手）
  let posY = 0
  let velY = 0
  let targetX = 0
  let posX = 0

  // 弹簧参数：K 越大越快（越"硬"），D 越大越不晃；这套值 ≈ 轻微过冲
  const K = 300
  const D = 26
  const MAX_STRETCH = 0.02

  const indicatorStyle = ref({})
  let rafId = 0
  let last = 0

  const reduced = () => window.matchMedia('(prefers-reduced-motion: reduce)').matches

  /** 指针 Y → 菜单项索引（取包含该 Y 的项，越界夹紧） */
  function indexFromY(y) {
    const menu = menuRef.value
    if (!menu) return 0
    const items = menu.querySelectorAll('.menu-item')
    if (!items.length) return 0
    let best = 0
    let bestDist = Infinity
    items.forEach((el, i) => {
      const center = el.offsetTop + itemHeight / 2
      const d = Math.abs(y - center)
      if (d < bestDist) { bestDist = d; best = i }
    })
    return best
  }

  function onMenuMove(e) {
    const menu = menuRef.value
    if (!menu) return
    const rect = menu.getBoundingClientRect()
    const y = e.clientY - rect.top
    const idx = indexFromY(y)
    hoverIndex = idx
    hovering = true
    targetY = idx * step
    // 横向磁吸：指针偏离菜单中心的量，按 8% 轻推（最大 ±5px）
    const cx = rect.left + rect.width / 2
    targetX = Math.max(-5, Math.min(5, (e.clientX - cx) * 0.08))
  }

  function onMenuLeave() {
    hovering = false
    hoverIndex = -1
    targetY = (activeIndex?.value ?? 0) * step
    targetX = 0
  }

  function tick(ts) {
    rafId = requestAnimationFrame(tick)
    const dt = Math.min(0.033, Math.max(0.008, (ts - last) / 1000 || 0.016))
    last = ts

    if (hovering || Math.abs(targetY - posY) > 0.2 || Math.abs(velY) > 0.4) {
      // 弹簧积分
      velY += (targetY - posY) * K * dt
      velY -= velY * D * dt
      posY += velY * dt
      posX += (targetX - posX) * 0.18

      const v = Math.abs(velY) / 100           // 归一化速度
      const stretch = reduced() ? 0 : Math.min(MAX_STRETCH, v * 0.9)
      const sy = 1 + stretch
      const sx = 1 - stretch * 0.7

      // 主液滴：横向磁吸 + 沿运动方向拉伸
      indicatorStyle.value = {
        transform: `translate3d(${posX.toFixed(2)}px, ${posY.toFixed(2)}px, 0) scale(${sx.toFixed(3)}, ${sy.toFixed(3)})`
      }
    }
  }

  onMounted(() => {
    posY = (activeIndex?.value ?? 0) * step
    targetY = posY
    // 首帧先落位，避免静止时液滴停在顶部
    indicatorStyle.value = { transform: `translate3d(0px, ${posY.toFixed(2)}px, 0) scale(1, 1)` }
    rafId = requestAnimationFrame(tick)
  })

  onBeforeUnmount(() => cancelAnimationFrame(rafId))

  // 悬停中路由变化时不打断（由 onMenuLeave 之后回落）；未悬停则跟随路由
  function syncActive() {
    if (!hovering) targetY = (activeIndex?.value ?? 0) * step
  }

  return { menuRef, indicatorStyle, onMenuMove, onMenuLeave, syncActive, hoverIndex: () => hoverIndex }
}
