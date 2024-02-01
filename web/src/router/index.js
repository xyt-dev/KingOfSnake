import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '@/views/pk/PkIndexView.vue'
import RankListIndexView from "@/views/ranklist/RankListIndexView.vue";
import RecordIndexView from "@/views/record/RecordIndexView.vue";
import NotFound from "@/views/404/NotFound.vue";
import UserBotIndexView from "@/views/user/bot/UserBotIndexView.vue";
import HomeView from "@/views/HomeView.vue";
import UserAccountLoginView from "@/views/user/account/UserAccountLoginView.vue";
import UserAccountRegisterView from "@/views/user/account/UserAccountRegisterView.vue";
import store from "@/store";

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: HomeView,
    meta: { // 额外的信息，名字随意
       requestAuth: true,
    }
  },
  {
    path: '/pk/',
    name: 'PkIndexView',
    component: PkIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/ranklist/',
    name: 'RankListView',
    component: RankListIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/record/',
    name: 'RecordIndexView',
    component: RecordIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/user/bot/',
    name: 'UserBotIndexView',
    component: UserBotIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: '/user/account/login/',
    name: 'UserAccountLoginView',
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: '/user/account/register/',
    name: 'UserAccountRegisterView',
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: '/:pathMatch(.*)*', // OR "/:catchAll(.*)*"
    name: 'NotFound',
    component: NotFound,
    meta: {
      requestAuth: false,
    }
  },

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 通过 router 进入页面时，会先执行这个函数
router.beforeEach((to, from, next) => {
    if (to.meta.requestAuth && store.state.user.is_login === false) {
      // 路由跳转是异步操作，与页面渲染不是同步的，所以放在组件中会导致先渲染在跳转的情况
      // 正确的逻辑是先判断是否存在 jwt_token，在决定跳转到哪个页面
      const jwt_token = localStorage.getItem("jwt_token");
      if (jwt_token) {
        store.commit("updateToken", jwt_token);
        console.log(jwt_token);
        store.dispatch("getInfo", {
          success(resp) {
            console.log("getInfo success: ", resp);
            router.push("/")
          },
          error(err) {
            console.log("getInfo err: ", err);
            next({name: "UserAccountLoginView"}); // 若 jwt_token 失效，则跳转到登录页面
          }
        })
      } else {
        next({name: "UserAccountLoginView"}) // next: 跳转到指定页面
      }
    } else {
      next(); // 跳转到默认页面
    }
})

export default router
