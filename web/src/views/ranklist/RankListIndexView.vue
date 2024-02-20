<template>
    <div class="card" style="margin:20px 298px">
        <div class="card-body">
            <el-table :data="users" style="width: 100%; height: 100%;">
                <el-table-column prop="username" label="玩家" #default="scope">
                    <div style="justify-content: center">
                        <el-avatar :src="getPhotoUrl(scope.row.photo)"/>
                        <span class="user-name">{{scope.row.username}}</span>
                    </div>
                </el-table-column>
                <el-table-column prop="rating" label="排位分">
                </el-table-column>
            </el-table>
        </div>
        <el-pagination background layout="prev, pager, next" :page-size="15" :total="usersCount" class="pagination" @current-change="handleCurrentChange" />
    </div>
</template>

<script setup>

import {useStore} from 'vuex';
import {ref} from "vue";
import $ from 'jquery'

const store = useStore();
let currentPage = 1;
let users = ref([]);
let usersCount = ref();


const getPhotoUrl = (photoUrl) => {
	if (photoUrl.startsWith("http")) {
		return photoUrl;
	} else {
		return `http://${localStorage.getItem('IpAddr')}:3000${photoUrl}`;
	}
}

const handleCurrentChange = page => {
	pullPage(page);
}

const pullPage = page => {
	currentPage = page;
	$.ajax({
		url: `http://${localStorage.getItem("IpAddr")}:3000/ranklist/getlist/`,
		data: {
			page,
		},
		type: "get",
		headers: {
			Authorization: "Bearer " + store.state.user.token,
		},
		success(resp) {
			users.value = resp.users;
			usersCount.value = resp.users_count;
			console.log(usersCount.value, users.value);
		},
		error(err) {
			console.log(err);
		},
	});
}

pullPage(currentPage);

</script>

<style scoped>

.card >>> .cell {
    text-align: left;
}

.user-name {
    display: inline-block;
    margin-top: 10px;
    margin-left: 10px;
    position: absolute;
}

.pagination {
    margin: 20px auto;
}
</style>