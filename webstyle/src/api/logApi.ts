import {routerRequest} from "@/utils/request";

export function getLogData() {
    return routerRequest({
        url: '/log/get',
        method: 'get'
    })
}


export function cleanLogData() {
    return routerRequest({
        url: '/log/clean',
        method: 'get'
    })
}