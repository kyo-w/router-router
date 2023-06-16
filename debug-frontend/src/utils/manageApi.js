import request from "./request";

export function connectDebugApi(){
  return request({
    url: "/mg/connect/run",
    method: 'get'
  })
}
export function analyStatusApi(){
  return request({
    url: '/mg/analyst/status',
    method: 'get'
  })
}

export function cleanDataApi(){
  return request({
    url: '/mg/clean',
    method: 'get'
  })
}

export function stopConnectApi(){
  return request({
    url: '/mg/stop',
    method: 'get'
  })
}

export function connectStatusApi(){
  return request({
    url: "/mg/exist",
    method: 'get'
  })
}

export function analystsStop(){
  return request({
    url: "/mg/analyst/stop",
    method: 'get'
  })
}

export function analystsStart(){
  return request({
    url: "/mg/analyst/start",
    method: 'get'
  })
}

