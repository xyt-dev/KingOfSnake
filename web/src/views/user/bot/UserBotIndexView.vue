<template>
	<div class="container">
		<div class="row">
			<div class="col-3">
				<div class="card" style="margin-top:20px">
					<div class="card-body">
						<img :src="$store.state.user.photo" style="width:100%" alt="Avatar">
					</div>
				</div>
			</div>
			<div class="col-9">
				<div class="card" style="margin-top:20px">
					<div class="card-header">
						<span style="font-size:150%">Bot List</span>
						<button class="button1 float-end" @click="addBotTableVisible = true; addBotForm.error_message=''; ">创建新Bot</button>
					</div>
					<div class="card-body">
						<el-table :data="tableData" style="width: 100%; height: 100%;">
							<el-table-column prop="title" label="标题" />
							<el-table-column prop="createTime" label="创建时间" />
							<el-table-column prop="modifyTime" label="上一次修改" />
							<el-table-column label="&nbsp&nbsp&nbsp&nbsp&nbsp编辑" >
								<template #default="scope">
										<button class="button1 button1-blue" @click="updateBotTableVisible = true; updateBotForm.error_message=''; setUpdateBotForm(scope.row)">&nbsp;编辑&nbsp;</button>
								</template>
							</el-table-column>
							<el-table-column label="&nbsp&nbsp&nbsp&nbsp&nbsp删除" >
								<template #default="scope">
									<button class="button1 button1-red" @click="removeBot(scope.row)">&nbsp;删除&nbsp;</button>
								</template>
							</el-table-column>
						</el-table>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="add-bot-dialog"> <!-- 使用 scoped 后 el-dialog 没法直接识别到 在外面套个div 再结合:deep() -->
		<el-dialog
				v-model="addBotTableVisible"
				width="70%"
				title="创建新Bot"
				destroy-on-close
				center
				class="custom-dialog"
		>
			<el-form :model="addBotForm" label-width="120px">
				<el-form-item label="标题">
					<el-input v-model="addBotForm.title" />
				</el-form-item>
				<el-form-item label="描述">
					<el-input v-model="addBotForm.description" type="textarea" />
				</el-form-item>
				<el-form-item label="代码">

					<VAceEditor
							v-model:value="addBotForm.content"
							@init="editorInit"
							lang="c_cpp"
							theme="chrome"
							style="height: 300px; width: 100%" />

				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<div class="error-message">{{ addBotForm.error_message }}</div>
					<el-button @click="addBotTableVisible = false;">取消</el-button>
					<el-button type="primary" @click="addBot">提交</el-button>
				</span>
			</template>
		</el-dialog>
	</div>


	<div class="update-bot-dialog"> <!-- 使用 scoped 后 el-dialog 没法直接识别到 在外面套个div 再结合:deep() -->
		<el-dialog
				v-model="updateBotTableVisible"
				width="70%"
				:title=updateBotForm.title
				destroy-on-close
				center
				class="custom-dialog"
		>
			<el-form label-width="120px">
				<el-form-item label="标题">
					<el-input v-model="updateBotForm.title" />
				</el-form-item>
				<el-form-item label="描述">
					<el-input v-model="updateBotForm.description" type="textarea" />
				</el-form-item>
				<el-form-item label="代码">

					<VAceEditor
							v-model:value="updateBotForm.content"
							@init="editorInit"
							lang="c_cpp"
							theme="chrome"
							style="height: 300px; width: 100%" />

				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<div class="error-message">{{ updateBotForm.error_message }}</div>
					<el-button @click="updateBotTableVisible = false;">取消</el-button>
					<el-button type="primary" @click="updateBot">提交</el-button>
				</span>
			</template>
		</el-dialog>
	</div>

</template>

<script setup>
import { useStore } from 'vuex';
import {onMounted, ref} from 'vue';
import $ from 'jquery';
import { VAceEditor } from "vue3-ace-editor";
import ace from "ace-builds";

import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';
import 'ace-builds/src-noconflict/mode-c_cpp';


ace.config.set(
		"basePath",
		"https://cdn.jsdelivr.net/npm/ace-builds@" +
		require("ace-builds").version +
		"/src-noconflict/")


import { reactive } from 'vue'
const store = useStore();
let bots = ref([]);


const tableData = ref([]);
let addBotTableVisible = ref(false);
let updateBotTableVisible = ref(false);

const addBotForm = reactive({
	title: '',
	description: '',
	content: '',
	error_message: '',
})

const updateBotForm = reactive({
	bot_id: '',
	title: '',
	description: '',
	content: '',
	error_message: '',
})

const setUpdateBotForm = (row) => {
	updateBotForm.bot_id = row.bot_id;
	updateBotForm.title = row.title;
	updateBotForm.description = row.description;
	updateBotForm.content = row.content;
}

const clearForm = (form) => {
	form.title = '';
	form.description = '';
	form.content = '';
	form.error_message = '';
	if("bot_id" in form) {
		form.bot_id = '';
	}
}

const refreshList = () => {
	tableData.value = [];
	$.ajax({
		url: `http://${localStorage.getItem('IpAddr')}:3000/user/bot/getlist/`,
		type: "get",
		headers: {
			Authorization: "Bearer " + store.state.user.token,
		},
		success(resp) {
			bots.value = resp;
			for(let i = 0; i < resp.length; i++) {
				tableData.value.push({
					bot_id: resp[i].id,
					title: resp[i].title,
					createTime: resp[i].createTime,
					modifyTime: resp[i].modifyTime,
					content: resp[i].content,
					description: resp[i].description
				})
			}
			console.log("getBotList success: ", resp);
		},
		error(err) {
			console.log("getBotList err: ", err);
		}
	})
}

const addBot = () => {
	addBotForm.error_message = '';
	$.ajax({
		url: `http://${localStorage.getItem('IpAddr')}:3000/user/bot/add/`,
		type: "post",
		data: {
			title: addBotForm.title,
			description: addBotForm.description,
			content: addBotForm.content,
		},
		headers: {
			Authorization: "Bearer " + store.state.user.token,
		},
		success(resp) {
			if(resp.message === "success") {
				clearForm(addBotForm);
				refreshList();
				addBotTableVisible.value = false;
				console.log("addBot success: ", resp);
			} else {
				addBotForm.error_message = resp.message;
			}
		},
		error(err) {
			console.log("addBot err: ", err);
		}
	})
}

const removeBot = (row) => {
	$.ajax({
		url: `http://${localStorage.getItem('IpAddr')}:3000/user/bot/remove/`,
		type: "post",
		data: {
			bot_id: row.bot_id,
		},
		headers: {
			Authorization: "Bearer " + store.state.user.token,
		},
		success(resp) {
			refreshList();
			console.log("removeBot success: ", resp);
		},
		error(err) {
			console.log(row);
			console.log("removeBot err: ", err);
		}
	})
}

const updateBot = () => {
		$.ajax({
			url: `http://${localStorage.getItem('IpAddr')}:3000/user/bot/update/`,
			type: "post",
			data: {
				bot_id: updateBotForm.bot_id,
				title: updateBotForm.title,
				description: updateBotForm.description,
				content: updateBotForm.content,
			},
			headers: {
				Authorization: "Bearer " + store.state.user.token,
			},
			success(resp) {
				if(resp.message === "success") {
					clearForm(updateBotForm);
					refreshList();
					updateBotTableVisible.value = false;
					console.log("updateBot success: ", resp);
				} else {
					updateBotForm.error_message = resp.message;
				}
				console.log("updateBot success: ", resp);
			},
			error(err) {
				console.log("updateBot err: ", err);
			}
	});
}

onMounted(() => {
	refreshList();
});


</script>

<style scoped>

.button1 {
	height: 35px;
	padding: 5px 10px;
	border: unset;
	border-radius: 15px;
	color: #212121;
	z-index: 1;
	background: #e8e8e8;
	position: relative;
	font-weight: 300;
	font-size: 17px;
	box-shadow: 5px 8px 12px -3px rgba(0,0,0,0.2);
	transition: all 200ms;
	//overflow: hidden;
}

.button1::before {
	content: "";
	position: absolute;
	top: 0;
	left: 0;
	height: 100%;
	width: 0;
	border-radius: 15px;
	background-color: #212121;
	z-index: -1;
	box-shadow: 5px 8px 19px -3px rgba(0,0,0,0.2);
	transition: all 200ms
}

.button1-blue::before {
	background-color: #4d79ff;
}

.button1-red::before {
	background-color: #ff4d4d;
}

.button1:hover {
	color: #e8e8e8;
}

.button1:hover::before {
	width: 100%;
}

.el-table :deep(.cell)  {  /* 直接浏览器检查找到的 .cell 标签 */
	padding: 0 0 10px;
}

.add-bot-dialog :deep(.el-dialog) {
	border-radius: 15px;
	padding-right: 70px;
}

.add-bot-dialog :deep(.el-dialog__title) {
	padding-left: 70px;
}

.add-bot-dialog :deep(.el-dialog__footer) {
	padding-left: 50px;
}

.update-bot-dialog :deep(.el-dialog) {
	border-radius: 15px;
	padding-right: 70px;
}

.update-bot-dialog :deep(.el-dialog__title) {
	padding-left: 70px;
}

.update-bot-dialog :deep(.el-dialog__footer) {
	padding-left: 50px;
}

.error-message {
	color: red;
	margin-bottom: 10px;
}

</style>