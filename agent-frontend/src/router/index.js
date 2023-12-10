import {createRouter, createWebHashHistory, createWebHistory} from 'vue-router'

const IndexView = () => import("@/views/Index.vue")
const TomcatView = () => import('@/components/show/Tomcat.vue')
const JettyView = () => import('@/components/show/Jetty.vue')

const StrutsView = () => import('@/components/show/Struts.vue')
const SpringView = () => import('@/components/show/Spring.vue')
const StackView = () => import('@/components/show/Stack.vue')

const JerseyView = () => import('@/components/show/Jersey.vue')

const LogView = () => import('@/components/show/Log.vue')

const routes = [
    {
        path: '/',
        redirect: '/info',
    }, {
        path: '/info',
        component: IndexView,
        children: [
            {
                path: 'tomcat',
                component: TomcatView
            }, {
                path: 'jetty',
                component: JettyView
            }, {
                path: 'struts',
                component: StrutsView
            },
            {
                path: 'stack',
                component: StackView
            }, {
                path: 'spring',
                component: SpringView
            }, {
                path: 'jersey',
                component: JerseyView
            }, {
                path: 'log',
                component: LogView
            }
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    routes,

})

export default router
