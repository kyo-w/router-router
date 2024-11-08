import axios, {AxiosRequestConfig, AxiosResponse} from "axios";
import {GetBaseUrl} from "@/config";

const nativeRequest = axios.create({
    baseURL: 'http://' + GetBaseUrl(),
    withCredentials: true,
    timeout: 5000
})


export class RouterResponse {
    context: any;
    code: number;
    want: boolean

    constructor(axiosResponse: AxiosResponse) {
        this.context = axiosResponse.data.msg
        this.code = axiosResponse.data.code
        this.want = false;
        if (this.code >= 200 && this.code < 300) {
            this.want = true;
        }
    }

    getContext(): any {
        return this.context;
    }

    isWant(): boolean {
        return this.want;
    }
}

export const routerRequest = (config: AxiosRequestConfig): Promise<RouterResponse> => {
    return nativeRequest(config).then(res => {
        return new Promise(resolve => {
            resolve(new RouterResponse(res))
        })
    })
}