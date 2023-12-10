import {routerRequest, RouterResponse} from "@/utils/request";

export function getContextByName(name: string, index: number) {
    return routerRequest({
        url: 'data/' + name + '/' + index,
        method: 'get'
    })
}

export function getContextSizeByName(name: string) {
    return routerRequest({
        url: '/data/count/' + name,
        method: 'get'
    })
}

export function getFrameworkType() {
    return routerRequest({
        url: 'data/framework/exist',
        method: 'get'
    })
}

export function getContextType() {
    return routerRequest({
        url: 'data/middle/exist',
        method: 'get'
    })
}

export function existFailServlet(): Promise<RouterResponse> {
    return routerRequest({
        url: '/data/exist/failservlet',
        method: 'GET'
    })
}

export function getFailServlet(): Promise<RouterResponse> {
    return routerRequest({
        url: '/data/failservlet',
        method: 'GET'
    })
}