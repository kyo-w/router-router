import {createRouter, createWebHashHistory} from 'vue-router'
const BugSetting = () => import("../view/BugSetting.vue")
const Main = () => import("../view/MainInfo.vue")
const Tomcat = ()=>import("@/view/info/Tomcat")
const Spring = ()=>import("@/view/info/Spring")
const Struts = ()=>import("@/view/info/Struts")
const Jersey = ()=>import("@/view/info/Jersey")
const Jetty = ()=>import("@/view/info/Jetty")
const Filter = ()=>import("@/view/info/Filter")

const routes = [
    {
        path: '/',
        redirect: '/index'
    },
    {
        path: '/index',
        component: BugSetting
    },
    {
        path: '/main',
        component: Main,
        children: [
            {
                path: "tomcat",
                component: Tomcat
            },
            {
                path: "spring",
                component: Spring
            },
            {
                path: "jetty",
                component: Jetty
            }, {
                path: "jersey",
                component: Jersey
            },
            {
                path: "struts",
                component: Struts
            }, {
                path: 'cleanback',
                redirect: '/main'
            },{
                path: 'filter',
                component: Filter
            }
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    routes
})

export default router
