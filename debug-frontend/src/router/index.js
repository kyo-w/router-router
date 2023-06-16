import {createRouter, createWebHashHistory} from 'vue-router'
const BugSetting = () => import("../components/BugSetting")
const Main = () => import("../components/MainInfo")
const Tomcat = ()=>import("../components/info/Tomcat")
const Spring = ()=>import("../components/info/Spring")
const Struts = ()=>import("../components/info/Struts")
const Jersey = ()=>import("../components/info/Jersey")
const Jetty = ()=>import("../components/info/Jetty")

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
            }
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    routes
})

export default router
