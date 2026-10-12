<template>
  <!--
    精致液态玻璃折射滤镜（参考 GitHub 开源实现，MIT）
    - dpawlikowski/liquid-glass：纯 CSS+SVG 路线（feTurbulence + feDisplacementMap）
    - kucukkanat/liquid-glass：分通道 3 次 feDisplacementMap + screen 混合 → 边缘彩色色差
    - xcyberpunkx0/liquid-glass：位移图 R 通道控 X、G 通道控 Y；仅 Chromium 生效 + CSS 降级
    用法：backdrop-filter: url(#lg-refraction) blur(...) saturate(...)
  -->
  <svg class="lg-filter-defs" width="0" height="0" aria-hidden="true" focusable="false">
    <defs>
      <filter id="lg-refraction" x="-25%" y="-25%" width="150%" height="150%"
              color-interpolation-filters="sRGB">
        <!-- 低频噪声：决定折射的"液体走向"（baseFrequency 越小，波纹越大越平滑） -->
        <feTurbulence type="fractalNoise" :baseFrequency="baseFrequency" :numOctaves="numOctaves"
                      :seed="seed" result="noise" />
        <feGaussianBlur in="noise" :stdDeviation="soften" result="softNoise" />

        <!-- 三次位移：彩色镶边来自 R/G/B 三档强度差 -->
        <feDisplacementMap in="SourceGraphic" in2="softNoise" :scale="scaleR"
                           xChannelSelector="R" yChannelSelector="G" result="dispR" />
        <feDisplacementMap in="SourceGraphic" in2="softNoise" :scale="scaleG"
                           xChannelSelector="R" yChannelSelector="G" result="dispG" />
        <feDisplacementMap in="SourceGraphic" in2="softNoise" :scale="scaleB"
                           xChannelSelector="R" yChannelSelector="G" result="dispB" />

        <!-- 抽出各次位移的单一色彩通道（R / G / B） -->
        <feColorMatrix in="dispR" type="matrix"
                       values="1 0 0 0 0  0 0 0 0 0  0 0 0 0 0  0 0 0 1 0" result="chR" />
        <feColorMatrix in="dispG" type="matrix"
                       values="0 0 0 0 0  0 1 0 0 0  0 0 0 0 0  0 0 0 1 0" result="chG" />
        <feColorMatrix in="dispB" type="matrix"
                       values="0 0 0 0 0  0 0 0 0 0  0 0 1 0 0  0 0 0 1 0" result="chB" />

        <!-- screen 混合回 RGB → 边缘出现彩虹描边（色差 fringe） -->
        <feBlend in="chR" in2="chG" mode="screen" result="rg" />
        <feBlend in="rg" in2="chB" mode="screen" result="rgb" />

        <!-- 轻微降噪，避免条纹破坏精致感 -->
        <feGaussianBlur in="rgb" :stdDeviation="polish" />
      </filter>

      <!--
        液滴融合（metaball / gooey）：酷安 dock 那种"玻璃球鼓出来并连成液体颈"的关键
        原理：feGaussianBlur 把邻近形状糊在一起 → feColorMatrix 抬高 alpha 对比度重新切边
             → 两个形状靠近时自动形成液体连接（分开时自动断成两滴）
        用法：加在**父容器**上（filter: url(#lg-goo)），容器内放若干跟随/拖尾的形状；
             文字与图标必须放在滤镜之外的另一层，否则会被糊掉。
      -->
      <filter id="lg-goo" x="-25%" y="-25%" width="150%" height="150%"
              color-interpolation-filters="sRGB">
        <feGaussianBlur in="SourceGraphic" :stdDeviation="gooBlur" result="gooBlur" />
        <feColorMatrix in="gooBlur" mode="matrix"
                       :values="`1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 ${gooAlpha} ${-gooAlpha / 2}`"
                       result="gooShape" />
        <!-- 清晰形状叠在融合结果之上：既有液体连接，又保持玻璃的锐利边缘 -->
        <feBlend in="SourceGraphic" in2="gooShape" mode="normal" />
      </filter>
    </defs>
  </svg>
</template>

<script setup>
/**
 * 调参速查（在 design.css 的 :root 里改 --lg-* 令牌即可全局生效）：
 *  scaleR/G/B：三档位移强度，差值 = 彩色镶边强度。三者相等 → 无色差；
 *              推荐 R 比 B 大 4~6（本次 R18/G14/B12）→ 边缘冷暖分离，最像真机
 *  baseFrequency：0.005~0.010 最自然（越小越像水面波纹，越大越碎）
 *  soften：噪声平滑度 1.0~2.0（太小会拉丝）
 *  polish：收尾模糊 0~0.6（0.3 左右可抹掉噪点条纹）
 */
defineProps({
  scaleR: { type: [Number, String], default: 18 },
  scaleG: { type: [Number, String], default: 14 },
  scaleB: { type: [Number, String], default: 12 },
  baseFrequency: { type: [Number, String], default: '0.007' },
  numOctaves: { type: [Number, String], default: 2 },
  seed: { type: [Number, String], default: 7 },
  soften: { type: [Number, String], default: 1.4 },
  polish: { type: [Number, String], default: 0.3 },
  /* gooey（液滴融合）参数：blur 决定"多近就连上"，alpha 决定边缘硬度 */
  gooBlur: { type: [Number, String], default: 6 },
  gooAlpha: { type: [Number, String], default: 24 }
})
</script>

<style scoped>
.lg-filter-defs { position: absolute; width: 0; height: 0; overflow: hidden; }
</style>
