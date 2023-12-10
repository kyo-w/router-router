const Debug = false

const GetBaseUrl = () => {
    if (Debug) {
        return "192.168.100.100:9090"
    } else {
        return window.location.host
    }
}
export default {
    GetFrameworkWsLocation: () => {
        return 'ws://' + GetBaseUrl() + "/api/ws/framework"
    },
    GetContextWsLocation: () => {
        return 'ws://' + GetBaseUrl() + "/api/ws/context"
    },
    GetStackWsLocation: () => {
        return 'ws://' + GetBaseUrl() + "/api/ws/stack"
    },
    GetLogWsLocation: () => {
        return 'ws://' + GetBaseUrl() + "/api/ws/log"
    },
    GetBaseUrl
}