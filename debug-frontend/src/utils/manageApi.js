import request from "./request";

export function connectDebugApi(){
  return request({
    url: "/mg/connect",
    method: 'get'
  })
}

export function cleanDataApi(){
  return request({
    url: '/mg/clean',
    method: 'get'
  })
}


export function connectStatusApi(){
  return request({
    url: "/mg/exist",
    method: 'get'
  })
}

export function analystApi(){
  return request({
    url: "/mg/run",
    method: 'get'
  })
}

export function closeConnectApi(){
  return request({
    url : "/mg/close/connect",
    method: 'get'
  })
}
