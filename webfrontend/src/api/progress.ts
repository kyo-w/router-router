import {routerRequest} from "@/utils/requests"
import {GetBaseUrl} from "@/config"

export class Progress {
    id: number
    name: string
    percentage: number
    message: string
    error: string
    fail: boolean

    constructor(response: any) {
        this.id = response !== null ? response.id : -1
        let namespaceList = response.taskName.split(".")
        this.name = response !== null ? namespaceList[namespaceList.length - 1] : ""
        this.percentage = response !== null ? Math.floor((response.current / response.count) * 100) : 0
        if (this.percentage === 100) {
            this.message = '完成分析'
        } else {
            this.message = response !== null ? response.message : ""
        }
        this.fail = response !== null ? response.fail : false
        this.error = response !== null ? response.error : ""
    }
}

export function apiGetProgress(): Promise<Progress[]> {
    return routerRequest({
        url: '/progress/getall',
        method: 'GET'
    }).then(routerResponse => {
        let result: Progress[] = []
        for (let rawProgress of routerResponse.msg) {
            result.push(new Progress(rawProgress))
        }
        return new Promise<Progress[]>(resolve => resolve(result))
    })
}

export function GetCurrentProject(): Promise<Boolean> {
    return routerRequest({
        url: '/project/current',
        method: 'GET'
    }).then(routerResponse => {
        return new Promise<Boolean>(resolve => resolve(routerResponse.code === 200))
    })
}
