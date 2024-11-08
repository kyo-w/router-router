import {GetBaseUrl} from "@/config";

export const createContextWs = (service: (this: WebSocket, ev: MessageEvent) => any) => {
    let ws = new WebSocket('ws://' + GetBaseUrl() + '/ws/data/context')
    ws.onmessage = service
}
export const createFrameworkWs = (service: (this: WebSocket, ev: MessageEvent) => any) => {
    let ws = new WebSocket('ws://' + GetBaseUrl() + '/ws/data/framework')
    ws.onmessage = service
}
export const createDiscoveryWs = (service: (this: WebSocket, ev: MessageEvent) => any) => {
    let ws = new WebSocket('ws://' + GetBaseUrl() + '/ws/discovery')
    ws.onmessage = service
}
export const createDebugInfoWs = (service: (this: WebSocket, ev: MessageEvent) => any) => {
    let ws = new WebSocket('ws://' + GetBaseUrl() + '/ws/debug/info')
    ws.onmessage = service
}

export const createFailServletWs = (service: (this: WebSocket, ev: MessageEvent) => any) => {
    let ws = new WebSocket('ws://' + GetBaseUrl() + '/ws/debug/servletfail')
    ws.onmessage = service
}
