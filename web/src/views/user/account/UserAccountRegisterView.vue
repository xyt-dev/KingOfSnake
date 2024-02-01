<template>
  <ContentField>
      <div class="row justify-content-center">
        <div class="col-3">
          <form @submit.prevent="register"> <!-- 阻止默认的网页刷新行为 -->
            <div class="mb-3">
              <label for="username" class="form-label">用户名</label>
              <input v-model="username" type="text" class="form-control" id="username" placeholder="用户名">
            </div>
            <div class="mb-3">
              <label for="password" class="form-label">密码</label>
              <input v-model="password" type="password" class="form-control" id="password"  placeholder="密码">
            </div>
            <div class="mb-3">
              <label for="confirmPassword" class="form-label">确认密码</label>
              <input v-model="confirmPassword" type="password" class="form-control" id="confirmPassword"  placeholder="确认密码">
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
import {ref} from "vue";
import $ from 'jquery';
import router from "@/router";

let username = ref('');
let password = ref('');
let confirmPassword = ref('');
let errorMessage = ref('');

const register = () => {
	$.ajax({
        url: "http://127.0.0.1:3000/user/account/register/",
        type: "post",
        data: {
            username: username.value,
            password: password.value,
            confirmedPassword: confirmPassword.value
        },
        success(resp) {
					if(resp.message === "success") {
						console.log("register success: ", resp);
						router.push({name: "UserAccountLoginView"})
					} else {
						console.log("register err: ", resp);
						errorMessage.value = resp.message;
					}
        },
        error(err) {
			errorMessage = err.message;
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