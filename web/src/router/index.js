import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '@/views/pk/PkIndexView.vue'
import RankListIndexView from "@/views/ranklist/RankListIndexView.vue";
import RecordIndexView from "@/views/record/RecordIndexView.vue";
import NotFound from "@/views/404/NotFound.vue";
import UserBotIndexView from "@/views/user/bot/UserBotIndexView.vue";
import HomeView from "@/views/HomeView.vue";

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: HomeView,
  },
  {
    path: '/pk/',
    name: 'PkIndexView',
    component: PkIndexView,
  },
  {
    path: '/ranklist/',
    name: 'RankListView',
    component: RankListIndexView,
  },
  {
    path: '/record/',
    name: 'RecordIndexView',
    component: RecordIndexView,
  },
  {
    path: '/user/bot/',
    name: 'UserBotIndexView',
    component: UserBotIndexView,
  },
  {
    path: '/:pathMatch(.*)*', // OR "/:catchAll(.*)*"
    name: 'NotFound',
    component: NotFound,
  },

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
