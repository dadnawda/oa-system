<template>
  <svg :width="size" :height="size" viewBox="0 0 24 24" fill="none"
       :stroke="color" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
    <path v-for="(d, i) in paths" :key="i" :d="d" />
    <circle v-for="(c, i) in circles" :key="'c' + i" :cx="c[0]" :cy="c[1]" :r="c[2]" />
  </svg>
</template>

<script setup>
/**
 * 鸿蒙风格线性图标组件（HarmonyOS Symbol 同款视觉：2px 圆头描边）
 * 内置零依赖 SVG 兜底；如需官方完整图标库，将 HarmonyIcons 字体引入后可平滑替换。
 */
import { computed } from 'vue'

const props = defineProps({
  name: { type: String, required: true },
  size: { type: [Number, String], default: 16 },
  color: { type: String, default: 'currentColor' }
})

const ICONS = {
  home:     { paths: ['M3 11l9-7 9 7v9a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1v-9z'] },
  grid:     { paths: ['M4 5h16v4H4zM4 15h16v4H4zM4 10h16v4H4z'] },
  people:   { paths: ['M4 20c1.5-3.5 4.5-5 8-5s6.5 1.5 8 5'], circles: [[12, 8, 4]] },
  doc:      { paths: ['M6 4h9l3 3v13H6z', 'M9 9h6M9 13h6'] },
  clock:    { paths: ['M12 8v4l3 2'], circles: [[12, 12, 8]] },
  leave:    { paths: ['M9 5h6M12 5v6l4 5H8l4-5'] },
  swap:     { paths: ['M4 8h13m0 0-3-3m3 3-3 3M20 16H7m0 0 3-3m-3 3 3 3'] },
  user:     { paths: ['M4 20c1.5-3.5 4.5-5 8-5s6.5 1.5 8 5'], circles: [[12, 8, 4]] },
  plus:     { paths: ['M12 5v14M5 12h14'] },
  search:   { paths: ['M20 20l-3.5-3.5'], circles: [[11, 11, 7]] },
  edit:     { paths: ['M4 20h16', 'M6 16l10-10 3 3-10 10H6v-3z'] },
  trash:    { paths: ['M4 7h16', 'M9 7V5h6v2', 'M7 7l1 13h8l1-13', 'M10 11v6M14 11v6'] },
  eye:      { paths: ['M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6-10-6-10-6z'], circles: [[12, 12, 2.5]] },
  check:    { paths: ['M5 13l4 4L19 7'] },
  close:    { paths: ['M6 6l12 12M18 6L6 18'] },
  money:    { paths: ['M12 3v18', 'M8 7h6a3 3 0 0 1 0 6H9a3 3 0 0 0 0 6h7'] },
  send:     { paths: ['M22 2 11 13', 'M22 2 15 22l-4-9-9-4 20-7z'] },
  logout:   { paths: ['M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4', 'M16 17l5-5-5-5', 'M21 12H9'] },
  building: { paths: ['M4 21V5a1 1 0 0 1 1-1h9a1 1 0 0 1 1 1v16', 'M15 9h4a1 1 0 0 1 1 1v11', 'M2 21h20', 'M8 8h3M8 12h3M8 16h3'] },
  bell:     { paths: ['M18 9a6 6 0 1 0-12 0c0 6-2 7-2 7h16s-2-1-2-7', 'M10 20a2 2 0 0 0 4 0'] },
  chat:     { paths: ['M21 12a8 8 0 0 1-8 8H4l2-3a8 8 0 1 1 15-5z', 'M8 12h.01M12 12h.01M16 12h.01'] },
  card:     { paths: ['M2 7a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V7z', 'M2 10h20'] },
  scan:     { paths: ['M3 7V5a2 2 0 0 1 2-2h2', 'M17 3h2a2 2 0 0 1 2 2v2', 'M21 17v2a2 2 0 0 1-2 2h-2', 'M7 21H5a2 2 0 0 1-2-2v-2', 'M3 12h18'] },
  star:     { paths: ['M12 3l2.7 5.6 6.1.8-4.5 4.3 1.1 6-5.4-2.9L6.6 19.7l1.1-6L3.2 9.4l6.1-.8L12 3z'] },
  smile:    { paths: ['M8 14s1.5 2 4 2 4-2 4-2', 'M9 9h.01M15 9h.01'], circles: [[12, 12, 8]] },
  'arrow-left': { paths: ['M19 12H5', 'M12 19l-7-7 7-7'] }
}

const paths = computed(() => ICONS[props.name]?.paths || [])
const circles = computed(() => ICONS[props.name]?.circles || [])
</script>
