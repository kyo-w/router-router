import {createRouter, createWebHashHistory, createWebHistory, RouteRecordRaw} from 'vue-router'
import dashBoard from '@/views/DashBoard.vue'
import mgIndex from '@/views/manger/Index.vue'
import progress from '@/views/manger/Progress.vue'
import mgProfile from '@/views/manger/Profile.vue'
import mgData from '@/views/manger/Data.vue'
import {GetCurrentProject} from "@/api/progress";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: '/',
        component: dashBoard,
        meta: {
            title: '面板'
        }
    }, {
        path: '/mg',
        name: 'manger',
        component: mgIndex,
        children: [
            {
                path: 'progress',
                name: 'progress',
                component: progress,
            }, {
                path: 'profile',
                name: 'profile',
                component: mgProfile
            }, {
                path: 'data',
                name: 'data',
                component: mgData
            }
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.name === '/') {
        next()
        return
    }
    if (!router.getRoutes().some(route => route.name === to.name)) {
        router.push('/mg/progress')
        return
    }
    GetCurrentProject().then(res => {
        if (res) {
            next()
        } else {
            router.push('/')
        }
    })
});

export default router
