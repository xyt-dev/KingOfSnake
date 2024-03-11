import { createApp } from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'
import ElementPlus from 'element-plus';

createApp(App).use(router).use(store).use(ElementPlus).mount('#app')

localStorage.setItem("IpAddr", "127.0.0.1") // server ip

// 解决奇怪的 ResizeObserver loop completed with undelivered notifications.
const debounce = (fn, delay) => {
	let timer = null;
	return function () {
		let context = this;
		let args = arguments;
		clearTimeout(timer);
		timer = setTimeout(function () {
			fn.apply(context, args);
		}, delay);
	}
}

const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver{
	constructor(callback) {
		callback = debounce(callback, 16);
		super(callback);
	}
}