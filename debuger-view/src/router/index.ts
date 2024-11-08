import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router'
import {mgConnectExist} from "@/api/manageApi";

const dashBoard = () => import('@/views/Dashboard.vue')
const debugIndexView = () => import('@/views/router/Index.vue')
const TomcatView = () => import('@/views/router/Tomcat.vue')
const SettingView = () => import('@/views/router/Setting.vue')
const LogView = () => import('@/views/router/Log.vue')
const SpringView = () => import('@/views/router/Spring.vue')
const JerseyView = () => import('@/views/router/Jersey.vue')
const StrutsView = () => import('@/views/router/Struts.vue')

const ServletView = () => import('@/views/router/Servlet.vue')
const routes: Array<RouteRecordRaw> = [{
    path: '/dashboard',
    name: 'dashboard',
    component: dashBoard
}, {
    path: '/router',
    name: 'debug',
    component: debugIndexView,
    children: [{
        path: 'tomcat',
        name: 'tomcat',
        component: TomcatView
    }, {
        path: 'setting',
        name: 'setting',
        component: SettingView
    }, {
        path: 'log',
        name: 'log',
        component: LogView
    }, {
        path: 'spring',
        name: 'spring',
        component: SpringView
    }, {
        path: 'jersey',
        name: 'jersey',
        component: JerseyView
    }, {
        path: 'struts',
        name: 'struts',
        component: StrutsView
    }, {
        path: 'servlet',
        name: 'servlet',
        component: ServletView
    }],
}, {
    path: '/',
    redirect: '/dashboard'
}
]

const router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    routes
})
// router.beforeEach((to, from, next) => {
//     if (to.name == 'connect') {
//         next()
//         return
//     }
//     mgConnectExist().then(res => {
//         if (!res.isWant()) {
//             router.push('/connect')
//             return
//         } else {
//             next()
//         }
//     })
// })
export default router
