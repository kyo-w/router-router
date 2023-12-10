import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router'
import {mgConnectExist} from "@/api/manageApi";

const connectView = () => import('@/views/Connect.vue')
const debugIndexView = () => import('@/views/debug/Index.vue')
const TomcatView = () => import('@/views/debug/Tomcat.vue')
const SettingView = () => import('@/views/debug/Setting.vue')
const LogView = () => import('@/views/debug/Log.vue')
const SpringView = () => import('@/views/debug/Spring.vue')
const JerseyView = () => import('@/views/debug/Jersey.vue')
const StrutsView = () => import('@/views/debug/Struts.vue')

const ServletView = () => import('@/views/debug/Servlet.vue')
const routes: Array<RouteRecordRaw> = [{
    path: '/connect',
    name: 'connect',
    component: connectView
}, {
    path: '/debug',
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
    redirect: '/debug'
}
]

const router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    routes
})
router.beforeEach((to, from, next) => {
    if (to.name == 'connect') {
        next()
        return
    }
    mgConnectExist().then(res => {
        if (!res.isWant()) {
            router.push('/connect')
            return
        } else {
            next()
        }
    })
})
export default router
