import axios, {AxiosInstance, AxiosRequestConfig, AxiosResponse} from "axios"
import {GetBaseUrl} from "@/config"

const nativeRequest: AxiosInstance = axios.create({
    baseURL: 'http://' + GetBaseUrl(),
    withCredentials: true,
    timeout: 5000
})

export class RouterResponse {
    msg: any
    code: number

    constructor(axiosResponse: AxiosResponse) {
        this.msg = axiosResponse.data.msg
        this.code = axiosResponse.data.code
    }
}

export const routerRequest = (config: AxiosRequestConfig): Promise<RouterResponse> => {
    return nativeRequest(config).then(res => {
        return new Promise(resolve => {
            resolve(new RouterResponse(res))
        })
    })
}
