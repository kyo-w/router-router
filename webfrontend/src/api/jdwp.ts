import {routerRequest} from "@/utils/requests"
import {ElMessage} from "element-plus";

export function apiGetJDWPStatus(): Promise<Boolean> {
    return routerRequest({
        method: 'GET',
        url: '/jdwp/status'
    }).then(routerResponse => {
        return new Promise<Boolean>(resolve => resolve(routerResponse.code === 200))
    })
}

export function apiConnectJDWP(): Promise<Boolean> {
    return routerRequest({
        method: 'POST',
        url: '/jdwp/connect'
    }).then(routerResponse => {
        if (routerResponse.code === 400) {
            ElMessage({
                type: 'error',
                message: routerResponse.msg,
            })
        }
        return new Promise<Boolean>(resolve => resolve(routerResponse.code === 200))
    })
}

export function apiCloseJDWP() {
    return routerRequest({
        method: "DELETE",
        url: '/jdwp/close'
    })
}
