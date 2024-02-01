<template>
  <ContentField>
    <div class="row justify-content-center">
      <div class="col-3">
        <form @submit.prevent="login"> <!-- 阻止默认的网页刷新行为 -->
          <div class="mb-3">
            <label for="exampleInputEmail1" class="form-label">用户名</label>
            <input v-model="username" type="text" class="form-control" id="username" placeholder="用户名">
          </div>
          <div class="mb-3">
            <label for="exampleInputPassword1" class="form-label">密码</label>
            <input v-model="password" type="password" class="form-control" id="password"  placeholder="密码">
          </div>
          <div class="error-message"> {{errorMessage}} </div>
          <button type="submit" class="btn btn-primary">Submit</button>
        </form>
      </div>
    </div>
  </ContentField>

</template>

<script setup>
import ContentField from "@/components/ContentField.vue";
import { useStore } from 'vuex';
import { ref} from 'vue';
import router from "@/router";

const store = useStore();
let username = ref('');
let password = ref('');
let errorMessage = ref('');



const login = () => {
	errorMessage.value = "";
  store.dispatch("login", {
    username: username.value,
    password: password.value,
    success(resp) {
			if (resp.message === "success") {
				console.log("login success: ", resp);
				store.dispatch("getInfo", {
					success(resp) {
						console.log("getInfo success: ", resp);
						router.push("/") // 跳转到 HomePage
					},
					error(err) {
						errorMessage.value = "获取用户信息失败";
						console.log("getInfo err: ", err);
					}
				})
			} else {
				console.log("login err: ", resp);
				errorMessage.value = "登录异常";
			}
    },
    error(err) {
      errorMessage.value = "用户名或密码不正确";
      console.log("login err:", err);
    }
  })

}

</script>

<style scoped>
button {
  width: 100%;
}
.error-message {
  color: red;
}
</style>