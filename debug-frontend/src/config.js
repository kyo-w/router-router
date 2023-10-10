const Debug = false
export default {
    Debug,
    GetDebugWsLocation : ()=>{
        if(Debug){
            return "ws://127.0.0.1:9090/ws/debug/info"
        }else {
            return 'ws://' + window.location.host + "/ws/debug/info"
        }
    },
    GetEventWsLocation: ()=>{
        if(Debug){
            return "ws://127.0.0.1:9090/ws/middle/discovery"
        }else {
            return 'ws://' + window.location.host + "/ws/middle/discovery"
        }
    },
    GetStackWsLocation: ()=>{
        if(Debug){
            return "ws://127.0.0.1:9090/ws/stack"
        }else{
            return 'ws://' + window.location.host + "/ws/stack"
        }
    }
}