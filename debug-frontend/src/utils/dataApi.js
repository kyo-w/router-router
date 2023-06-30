import request from "./request";

export function getTargetVersionAip(target) {
    return request({
        url: "/data/version/" + target,
        method: 'get'
    })
}


export function existDataApi() {
    return request({
        url: "/data/exist",
        method: 'get'
    })
}

export function getListDataApi() {
    return request({
        url: "/data/existtarget",
        method: 'get'
    })
}


export function existTargetApi(target) {
    return request({
        url: "/data/exist/" + target,
        method: 'get'
    })
}

export function getTargetDataApi(target) {
    return request({
        url: "/data/" + target,
        method: 'get'
    })
}

export function exportAllApi() {
    return request({
        url: "/data/export/all",
        method: 'get',
        responseType: 'blob'
    })
}

export function hasModifyApi() {
    return request({
        url: '/data/modify/spring',
        method: 'get'
    })
}

export function getSpringPrefix() {
  return request({
      url: '/data/modify/spring/prefix',
      method: 'get'
  })
}

export function getFilterMap(){
    return request({
        url: '/data/filter',
        method: 'get'
    })
}