<template>
  <NavBar>
  </NavBar>
  <router-view></router-view>
</template>


<script setup>
import NavBar from "@/components/NavBar.vue";
import "bootstrap/dist/css/bootstrap.css"
import "bootstrap/dist/js/bootstrap.js"

// import $ from 'jquery';
//
//
//
// function apiTest() {
//   $.ajax({
//     url: "http://127.0.0.1:3000/user/account/token/",
//     type: "post",
//     data: {
//       "username": "321",
//       "password": "666"
//     },
//     success(resp) {
//       console.log("/user/account/token/")
//       console.log("success ", resp);
//     },
//     error(err) {
//       console.log("/user/account/token/")
//       console.log("err ", err);
//     }
//   })
//   $.ajax({
//     url: "http://127.0.0.1:3000/user/account/info/",
//     type: "get",
//     headers: {
//       Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4M2IwMmIyNTNlOWM0MjZmYTZkMzFhODUwNWRlMWYyMiIsInN1YiI6IjEyMyIsImlzcyI6IuWTiOWTiOWTiCIsImlhdCI6MTcwNjYwNzEyMCwiZXhwIjoxNzA3ODE2NzIwfQ.hkPxfJYPt7EUOAR6uj5Fy7AoAWzgBLVRFR-u5rC4OS4"
//     },
//     success(resp) {
//       console.log("/user/account/info/")
//       console.log("success ", resp);
//     },
//     error(err) {
//       console.log("/user/account/info/")
//       console.log("err ", err);
//     }
//   })
//   $.ajax({
//     url: "http://127.0.0.1:3000/user/account/register/",
//     type: "post",
//     data: {
//       username: "newUser",
//       password: "newUser",
//       confirmedPassword: "newUser"
//     },
//     success(resp) {
//       console.log("/user/account/register/")
//       console.log("success ", resp);
//     },
//     error(err) {
//       console.log("/user/account/register/")
//       console.log("err ", err);
//     }
//   })
//   $.ajax({
//     url: "http://127.0.0.1:3000/user/all/",
//     type: "get",
//     headers: {
//       Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4M2IwMmIyNTNlOWM0MjZmYTZkMzFhODUwNWRlMWYyMiIsInN1YiI6IjEyMyIsImlzcyI6IuWTiOWTiOWTiCIsImlhdCI6MTcwNjYwNzEyMCwiZXhwIjoxNzA3ODE2NzIwfQ.hkPxfJYPt7EUOAR6uj5Fy7AoAWzgBLVRFR-u5rC4OS4"
//     },
//     success(resp) {
//       console.log("/user/account/register/")
//       console.log("success ", resp);
//     },
//     error(err) {
//       console.log("/user/account/register/")
//       console.log("err ", err);
//     }
//   })
// }
//
// apiTest();

</script>
<style>
body {
  //background-image: url("@/assets/lady.jpg");
  //background-size: cover;
  //background-repeat: no-repeat;
}
</style>
