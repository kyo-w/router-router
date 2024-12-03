import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import '@/theme/dark.css'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.mount('#app')
