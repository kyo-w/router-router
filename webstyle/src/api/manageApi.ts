import {routerRequest, RouterResponse} from "@/utils/request";

export function getDefaultArgsApi(): Promise<RouterResponse> {
    return routerRequest({
        url: '/mg/args/get',
        method: "GET"
    })
}

export function connectTargetVmApi(args: any): Promise<RouterResponse> {
    return routerRequest({
        url: '/mg/connect/open',
        method: 'POST',
        data: args
    })
}

export function mgConnectExist(): Promise<RouterResponse> {
    return routerRequest({
        url: '/mg/connect/exist',
        method: 'GET'
    })
}

export function mgConnectOpen(): Promise<RouterResponse> {
    return routerRequest({
        url: '/mg/connect/open',
        method: 'GET'
    })
}

export function mgExistTask(): Promise<RouterResponse> {
    return routerRequest({
        url: '/mg/task/exist',
        method: 'GET'
    })
}

export function mgStartTask(): Promise<RouterResponse> {
    return routerRequest({
        url: '/mg/task/run',
        method: 'GET'
    })
}

export function mgStopTask(): Promise<RouterResponse> {
    return routerRequest({
        url: '/mg/task/stop',
        method: 'GET'
    })
}