import {routerRequest} from "@/utils/requests"
import {ElMessage} from "element-plus";

export function apiStartAnalysts(): Promise<Boolean> {
    return routerRequest({
        method: 'POST',
        url: '/task/start'
    }).then(routerResponse => {
        return new Promise<Boolean>(resolve => {
            if (routerResponse.code === 400) {
                ElMessage({
                    type: 'error',
                    message: routerResponse.msg,
                })
            }
            resolve(routerResponse.code === 200)
        })
    })
}

export function apiAnalystStatus(): Promise<Boolean> {
    return routerRequest({
        method: 'GET',
        url: '/task/status'
    }).then(routerResponse => {
        return new Promise<Boolean>(resolve => resolve(routerResponse.code === 200))
    })
}

export function apiStopAnalysts() {
    return routerRequest({
        method: 'DELETE',
        url: '/task/stop'
    })
}
