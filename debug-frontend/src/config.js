const Debug = false
export default {
    Debug,
    GetWsLocation : ()=>{
        if(Debug){
            return "ws://127.0.0.1:9090/ws/debug/info"
        }else {
            return 'ws://' + window.location.host + "/ws/debug/info"
        }
    }
}