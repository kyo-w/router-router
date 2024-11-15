const Debug = false
export function GetBaseUrl() {
    if (Debug) {
        return "127.0.0.1:9090"
    } else {
        return window.location.host
    }
}
