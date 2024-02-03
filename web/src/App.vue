<template>
  <NavBar>
  </NavBar>
  <router-view></router-view>
</template>


<script setup>
import NavBar from "@/components/NavBar.vue";
import "bootstrap/dist/css/bootstrap.css"
import "bootstrap/dist/js/bootstrap.js"
import 'element-plus/dist/index.css'

</script>
<style>
body {
  //background-image: url("@/assets/lady.jpg");
  //background-size: cover;
  //background-repeat: no-repeat;
}
</style>
