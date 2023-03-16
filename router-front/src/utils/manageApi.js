import request from "./request";

export function connectDebugApi(){
  return request({
    url: "/mg/connect",
    method: 'get'
  })
}

export function runAnalyApi(){
  return request({
    url: "/mg/run",
    method: 'get'
  })
}
export function analyStatusApi(){
  return request({
    url: '/mg/analystatus',
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

