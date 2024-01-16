<template>
  <div ref="parent" class="gamemap">
    <canvas ref="canvas" tabindex="0">

    </canvas>
  </div>

</template>

<script setup>
import {GameMap} from "@/assets/scripts/GameMap.js";
import {ref, onMounted, onBeforeUnmount} from "vue";

let parent = ref(null);
let canvas = ref(null);
// eslint-disable-next-line no-unused-vars
let gameMap = null;

onMounted(() => {
  gameMap = new GameMap(canvas.value.getContext('2d'), parent.value);
})

onBeforeUnmount(() => {
  gameMap.deleteTimer();
})

</script>

<style scoped>
.gamemap {
  width: 100%;
  height: 100%;
  margin: auto;
  display: flex;
  justify-content: center; /* 水平方向居中对齐 */
  align-items: center; /* 竖直方向居中对齐 */
}
</style>