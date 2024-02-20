<template>
    <div class="card" style="margin:20px 298px">
        <div class="card-body">
            <el-table :data="records" style="width: 100%; height: 100%;">
                <el-table-column prop="record.createTime" label="对战时间" />
                <el-table-column prop="a_username" label="玩家A" #default="scope">
                    <div style="justify-content: center">
                        <el-avatar :src="getPhotoUrl(scope.row.a_photo)"/>
                        <span class="user-name">{{scope.row.a_username}}{{ratingVariation(scope.row, 'A')}}</span>
                    </div>
                </el-table-column>
                <el-table-column prop="b_username" label="玩家B" #default="scope">
                    <div style="justify-content: center" >
                        <el-avatar :src="getPhotoUrl(scope.row.b_photo)"/>
                        <span class="user-name">{{scope.row.b_username}}{{ratingVariation(scope.row, 'B')}}</span>
                    </div>
                </el-table-column>
                <el-table-column prop="record.result" label="对局结果" :formatter="resultFormatter"/>
            </el-table>
        </div>
        <el-pagination background layout="prev, pager, next" :page-size="15" :total="recordsCount" class="pagination" @current-change="handleCurrentChange" />
    </div>

</template>

<script setup>
import {useStore} from 'vuex';
import {ref} from "vue";
import $ from 'jquery'

const store = useStore();
let currentPage = 1;
let records = ref([]);
let recordsCount = ref();

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

const resultFormatter = (row, column, cellValue) => {
	if (cellValue === "B") return row.a_username.toString() + " 捕获了 " + row.b_username.toString() + " 的蛇";
	else if (cellValue === "A") return row.b_username.toString() + " 捕获了 " + row.a_username.toString() + " 的蛇";
	else if (cellValue === "D") return "平局";
}

const ratingVariation = (row, role) => {
	let diff = row.record.diff;
	let ratingA = row.record.arating;
	let ratingB = row.record.brating;
	let str = " : "
	if (row.record.result === "A" && role === "A") {
		return str + ratingA.toString() + "[" + "-" + diff + "]";
    } else if (row.record.result === "B" && role === "B") {
		return str + ratingB.toString() + "[" + "-" + diff + "]";
    } else if (row.record.result === "D") {
		return str + ratingA.toString() + "[0]";
    } else if (role === "A") {
		return str + ratingA.toString() + "[" + "+" + diff + "]";
    } else {
		return str + ratingB.toString() + "[" + "+" + diff + "]";
	}

}

const pullPage = page => {
	currentPage = page;
	$.ajax({
        url: `http://${localStorage.getItem("IpAddr")}:3000/record/getlist/`,
        data: {
			page,
        },
        type: "get",
        headers: {
			Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
			records.value = resp.records;
			recordsCount.value = resp.records_count;
			console.log(recordsCount.value, records.value);
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