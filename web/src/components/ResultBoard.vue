<template>
    <div class="result-board">
        <div class="result-text large-font" v-if="$store.state.pk.result === 'D'">
            平局
        </div>
        <div class="result-text medium-font" v-else-if="isWin()">
            大吉大利，今晚吃蛇
        </div>
        <div class="result-text medium-font" v-else>
            对手今晚吃蛇
        </div>
        <div class="result-btn">
            <button class="clean-btn" @click="restart">
                再来一局
            </button>
        </div>
    </div>
</template>

<script setup>
import {useStore} from "vuex";

const store = useStore()
const isWin = () => {
	const a_id = parseInt(store.state.pk.a_id);
	const user_id = parseInt(store.state.user.id);
    return (a_id === user_id
        ? store.state.pk.result === "B"
        : store.state.pk.result === "A")
}
const restart = () => {
	store.commit("updateResult", "none");
    store.commit("updateStatus", "matching");
	store.commit("updateOpponent", { // 设置匹配默认头像、对手昵称
		username: "对手",
		photo: require("@/assets/images/1.png"),
	});
}

</script>

<style scoped>
.large-font {
    font-size: 50px;
}

.medium-font {
    font-size: 35px;
}

div.result-board {
    width: 30vw;
    height: 30vh;
    background-color: rgba(50, 50, 50, 0.3);
    position: absolute;
    top: 35vh;
    left: 35vw;
    text-align: center;
    color: white;
    font-size: 50px;
    font-weight: bold;
    font-style: italic;
}

.result-text {
    padding-top: 5vh;
}

.result-btn {
    padding-top: 5vh;
    padding-bottom: 5vh;
}

.clean-btn {
    text-decoration: none;
    position: relative;
    border: none;
    font-size: 15px;
    font-family: inherit;
    cursor: pointer;
    color: #fff;
    width: 9em;
    height: 3em;
    line-height: 2em;
    text-align: center;
    background: linear-gradient(90deg, #03a9f4, #f441a5, #ffeb3b, #03a9f4);
    background-size: 300%;
    border-radius: 30px;
    z-index: 1;
}

.clean-btn:hover {
    animation: ani 8s linear infinite;
    border: none;
}

@keyframes ani {
    0% {
        background-position: 0%;
    }

    100% {
        background-position: 400%;
    }
}

.clean-btn:before {
    content: "";
    position: absolute;
    top: -5px;
    left: -5px;
    right: -5px;
    bottom: -5px;
    z-index: -1;
    background: linear-gradient(90deg, #03a9f4, #f441a5, #ffeb3b, #03a9f4);
    background-size: 400%;
    border-radius: 35px;
    transition: 0.3s;
}

.clean-btn:hover::before {
    filter: blur(30px);
}

.clean-btn:active {
    background: linear-gradient(32deg, #03a9f4, #f441a5, #ffeb3b, #03a9f4);
}

</style>