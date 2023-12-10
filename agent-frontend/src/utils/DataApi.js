import request from "./request";

export function getStack() {
    return request({
        url: '/api/data/request',
        method: 'get'
    })
}

export function cleanStackApi() {
    return request({
        url: '/api/data/clean/request',
        method: 'get'
    })
}

export function getContextNamespace() {
    return request({
        url: '/api/data/context/namespace',
        method: 'get'
    })
}

export function getFrameworkNamespace() {
    return request({
        url: '/api/data/framework/namespace',
        method: 'get'
    })
}

export function getContextByName(name) {
    return request({
        url: '/api/data/context/' + name,
        method: 'get'
    })
}

export function getFrameworkByName(name) {
    return request({
        url: '/api/data/framework/' + name,
        method: 'get'
    })
}

export function getLogData() {
    return request({
        url: '/api/log/data',
        method: 'get'
    })
}

export function clearLogData() {
    return request({
        url: '/api/log/clean',
        method: 'get'
    })
}