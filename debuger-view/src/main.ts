import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import './style/theme.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

let light = true;
let themeName = "light"
let themeType = sessionStorage.getItem("router_theme")
if (themeType !== null && themeType === 'dark') {
    light = false
    themeName = "dark"
}

const changeTheme = function () {
    return function () {
        light = !light
        if (!light) {
            document.documentElement.setAttribute('theme', 'light')
            document.querySelector("html")?.classList.remove("dark")
            document.querySelector("html")?.classList.add("light")
            sessionStorage.setItem('router_theme', 'light')
            themeName = 'light'
        } else {
            document.documentElement.setAttribute('theme', 'dark')
            document.querySelector("html")?.classList.remove("light")
            document.querySelector("html")?.classList.add("dark")
            sessionStorage.setItem('router_theme', 'dark')
            themeName = 'dark'
        }
    }
}()
// 初始化主题
changeTheme()
const getTheme = function () {
    return function () {
        return themeName
    }
}()


let app = createApp(App);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.config.globalProperties.changeTheme = changeTheme;
app.config.globalProperties.getTheme = getTheme;

app.use(store).use(ElementPlus).use(router).mount('#app')

const debounce = (fn: any, delay: any) => {
    let timer: any = null;

    return function () {
        // @ts-ignore
        let context = this;

        let args = arguments;

        clearTimeout(timer);

        timer = setTimeout(function () {
            fn.apply(context, args);
        }, delay);
    };
};
const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
    constructor(callback: any) {
        callback = debounce(callback, 16);
        super(callback);
    }
};
