<template>
    <el-upload
            class="avatar-uploader"
            :action="upLoadAddr"
            :headers="{ 'Authorization': 'Bearer ' + $store.state.user.token}"
            :data="{token: $store.state.user.token}"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
    >
        <img v-if="$store.state.user.photo" :src="$store.state.user.photo" class="avatar" />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
    </el-upload>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {useStore} from "vuex";

const upLoadAddr = `http://${localStorage.getItem('IpAddr')}:3000/user/account/uploadAvatar/`;
const store = useStore();

const handleAvatarSuccess = () => {
	store.dispatch("getInfo", {
		success(resp) {
            console.log("getInfo success: ", resp);
			ElMessage.success("更新用户头像");
		},
		error(err) {
			console.log("getInfo err: ", err);
		},
	})
}

const beforeAvatarUpload= (rawFile) => {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/jpg' && rawFile.type !== 'image/png') {
        ElMessage.error('Avatar picture must be JPG/PNG format!')
        return false
    } else if (rawFile.size / 1024 / 1024 > 1) {
        ElMessage.error('Avatar picture size can not exceed 1MB!')
        return false
    }
    return true
}
</script>
<style scoped>
.avatar-uploader .avatar {
    width: 100%;
    height: 100%;
    display: block;
}
</style>
<style>
.avatar-uploader {
    display: block;
    height: 100%;
}
.avatar-uploader .el-upload {
    width: 100%;
    height: 100%;
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: auto;
    height: auto;
    text-align: center;
}
</style>
