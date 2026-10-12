<template>
  <span class="hm-svg-icon" :style="{ width: size + 'px', height: size + 'px' }" v-html="svg" />
</template>

<script setup>
/**
 * HM 图标组件：渲染从 Pixso「7.IconRes」/设计画板导出的官方 Symbol SVG
 * 图标文件位于 src/assets/icons/*.svg，描边/填充自动跟随文字颜色（currentColor）
 */
import { computed } from 'vue'

const modules = import.meta.glob('../assets/icons/*.svg', { query: '?raw', import: 'default', eager: true })

const props = defineProps({
  name: { type: String, required: true },
  size: { type: [Number, String], default: 16 }
})

const svg = computed(() => {
  const raw = modules[`../assets/icons/${props.name}.svg`] || ''
  return raw
    .replace(/fill="rgb\(0,0,0\)"/g, 'fill="currentColor"')
    .replace(/stroke="rgb\(0,0,0\)"/g, 'stroke="currentColor"')
})
</script>

<style scoped>
.hm-svg-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
  flex-shrink: 0;
  line-height: 0;
}
.hm-svg-icon :deep(svg) { width: 100%; height: 100%; }
</style>
