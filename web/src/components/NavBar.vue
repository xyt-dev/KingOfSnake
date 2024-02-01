<template>
  <nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container">

      <router-link class="navbar-brand" :to="{name: 'HomeView'}" style="font-size:23px; margin-right:30px;">KingOfSnake</router-link>

      <!-- 页面变窄时的button -->
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarText">

        <!-- leftSide -->
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link :class="routeName === 'PkIndexView' ? 'nav-link active' : 'nav-link'" :to="{name: 'PkIndexView'}">对战</router-link>
          </li>
          <li class="nav-item">
            <router-link :class="routeName === 'RecordIndexView' ? 'nav-link active' : 'nav-link'" :to="{name: 'RecordIndexView'}">对局列表</router-link>
          </li>
          <li class="nav-item">
            <router-link :class="routeName === 'RankListView' ? 'nav-link active' : 'nav-link'" :to="{name: 'RankListView'}">排行榜</router-link>
          </li>
        </ul>

        <!-- rightSide -->
        <ul class="navbar-nav">
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false" v-if="$store.state.user.is_login">
                {{ $store.state.user.username }}
              </a>
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false" v-else>
                登录/注册
              </a>
              <ul class="dropdown-menu">
                <li v-if="!$store.state.user.is_login"><router-link class="dropdown-item" :to="{name: 'UserAccountLoginView'}">登录</router-link></li>
                <li  v-if="$store.state.user.is_login"><router-link :class="routeName === 'UserBotIndexView' ? 'dropdown-item active' : 'dropdown-item'" :to="{name: 'UserBotIndexView'}">Bot</router-link></li>
                <li><hr class="dropdown-divider"></li>
                <li v-if="!$store.state.user.is_login"><router-link class="dropdown-item" :to="{name: 'UserAccountRegisterView'}">注册账号</router-link></li>
                <li v-if="$store.state.user.is_login"><a class="dropdown-item" @click="logout" role="button">退出登录</a></li>
              </ul>
            </li>
        </ul>

      </div>
    </div>
  </nav>
</template>

<script setup>
import {useRoute, useRouter} from "vue-router";
import {useStore} from "vuex";
import {computed} from "vue";
const route = useRoute();
const router = useRouter();
const store = useStore();

let routeName = computed(() => route.name);

const logout = () => {
  store.dispatch("logout");
  router.push("/user/account/login/");
}

</script>

<style scoped>

</style>