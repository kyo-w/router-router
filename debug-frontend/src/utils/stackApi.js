import request from "./request";
export function startListenApi(){
    return request({
        url: "/stack/start/listen",
        method: 'get',
    })
}

export function endListenApi(){
    return request({
        url: "/stack/end/listen",
        method: 'get',
    })
}

export function getCacheDataApi(){
    return request({
        url: '/stack/get/cache',
        method: 'get'
    })
}
export function cleanCacheApi(){
    return request({
        url: '/stack/clean/cache',
        method: 'get'
    })
}