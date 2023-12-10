class RouterSocket {
    constructor(url, type, store) {
        this.ws = new WebSocket(url)
        this.store = store
        this.type = type
        this.ws.onopen = () => {
            console.log(url + "连接成功")
        }
        this.ws.onmessage = (event) => {
            this.store.commit('FLUSH_FLAG', this.type)
            if (this.func) {
                this.func(event)
            }
        }
    }

    onmessage(func) {
        this.func = func
    }
}

export default RouterSocket