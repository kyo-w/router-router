import request from "./request";

export function setArg(args){
  return request({
    url: "/args/set",
    method: 'post',
    data: args
  })
}

export function getArgApi(){
  return request({
    url: "/args/get",
    method: 'get'
  })
}

