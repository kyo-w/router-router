import {routerRequest} from "@/utils/requests"

export class Middleware {
    id: number
    type: string
    virtualPath: string
    physicalPath: string
    version: string

    constructor(response: any) {
        this.id = response !== null ? response.id : -1
        this.type = response !== null ? response.type : ""
        this.virtualPath = response !== null ? response.virtualPath : ""
        this.physicalPath = response !== null ? response.physicalPath : ""
        this.version = response !== null ? response.version : ""
    }
}

export class Framework {
    id: number
    type: string
    contextPath: string
    version: string

    constructor(response: any) {
        this.id = response !== null ? response.id : -1
        this.type = response !== null ? response.type : ""
        this.contextPath = response !== null ? response.contextPath : ""
        this.version = response !== null ? response.version : ""
    }
}

export class Servlet {
    id: number
    middleId: number
    classname: string
    urls: string[]
    mark: boolean

    constructor(response: any) {
        this.id = response !== null ? response.id : -1
        this.middleId = response !== null ? response.middleId : -1
        this.classname = response !== null ? response.classname : ""
        this.urls = response !== null ? response.urls : []
        this.mark = response !== null ? response.mark : false
    }
}

export class Filter {
    id: number
    priority: number
    middleId: number
    classname: string
    url: string

    constructor(response: any) {
        this.id = response !== null ? response.id : -1
        this.middleId = response !== null ? response.middleId : -1
        this.priority = response !== null ? response.priority : -1
        this.classname = response !== null ? response.classname : ""
        this.url = response !== null ? response.url : ""
    }

}

export class Handler {
    id: number
    frameworkId: number
    classname: string
    urls: string[]
    mark: boolean

    constructor(response: any) {
        this.id = response !== null ? response.id : -1
        this.frameworkId = response !== null ? response.frameworkId : -1
        this.classname = response !== null ? response.classname : ""
        this.urls = response !== null ? response.urls : []
        this.mark = response !== null ? response.mark : false
    }
}

export function apiGetMiddlewareAllData() {
    return routerRequest({
        method: 'GET',
        url: '/data/middleware/getall'
    }).then(routerResponse => {
        return new Promise<string[]>(resolve => resolve(routerResponse.msg))
    })
}

export function apiGetFrameworkAllData() {
    return routerRequest({
        method: 'GET',
        url: '/data/framework/getall'
    }).then(routerResponse => {
        return new Promise<string[]>(resolve => resolve(routerResponse.msg))
    })
}

export function apiGetSpecifiedMiddleware(target: string): Promise<Middleware[]> {
    return routerRequest({
        method: 'GET',
        url: '/data/middleware/' + target + '/panel'
    }).then(routerResponse => {
        return new Promise<Middleware[]>(resolve => {
            let res: Middleware[] = []
            for (let elem of routerResponse.msg) {
                res.push(new Middleware(elem))
            }
            resolve(res)
        })
    })
}

export function apiGetSpecifiedMiddlewareUrlMap(id: number, pageNumber: number): Promise<Servlet[]> {
    return routerRequest({
        method: 'GET',
        url: '/data/middleware/' + id.toString() + '/urlmap',
        params: {
            limit: 10,
            page: pageNumber
        }
    }).then(routerResponse => {
        let res: Servlet[] = []
        for (let elem of routerResponse.msg) {
            res.push(new Servlet(elem))
        }
        return new Promise<Servlet[]>(resolve => resolve(res))
    })
}

export function apiGetSpecifiedMiddlewareFilter(id: number, pageNumber: number): Promise<Filter[]> {
    return routerRequest({
        method: 'GET',
        url: '/data/middleware/' + id.toString() + '/filter',
        params: {
            limit: 10,
            page: pageNumber
        }
    }).then(routerResponse => {
        let res: Filter[] = []
        for (let elem of routerResponse.msg) {
            res.push(new Filter(elem))
        }
        return new Promise<Filter[]>(resolve => resolve(res))
    })
}

export function apiGetSpecifiedMiddlewareUrlMapCount(middleid: number): Promise<number> {
    return routerRequest({
        method: 'GET',
        url: '/data/middleware/' + middleid.toString() + '/urlmap/count'
    }).then(routerResponse => {
        return new Promise<number>(resolve => resolve(routerResponse.msg))
    })
}

export function apiGetSpecifiedMiddlewareFilterCount(middleid: number): Promise<number> {
    return routerRequest({
        method: 'GET',
        url: '/data/middleware/' + middleid.toString() + '/filter/count'
    }).then(routerResponse => {
        return new Promise<number>(resolve => resolve(routerResponse.msg))
    })
}


export function apiGetSpecifiedFrameworkPanel(target: string): Promise<Framework[]> {
    return routerRequest({
        method: 'GET',
        url: '/data/framework/' + target + '/panel'
    }).then(routerResponse => {
        let result: Framework[] = []
        for (let elem of routerResponse.msg) {
            result.push(new Framework(elem))
        }
        return new Promise<Framework[]>(resolve => resolve(result))
    })
}

export function apiGetSpecifiedFrameworkUrlMapCount(frameworkId: number): Promise<number> {
    return routerRequest({
        method: 'GET',
        url: '/data/framework/' + frameworkId.toString() + '/handlerMap/count'
    }).then(routerResponse => {
        return new Promise<number>(resolve => resolve(routerResponse.msg))
    })
}

export function apiGetSpecifiedFrameworkUrlMap(frameworkId: number, pageNumber: number): Promise<Handler[]> {
    return routerRequest({
        method: 'GET',
        url: '/data/framework/' + frameworkId.toString() + '/handlerMap',
        params: {
            limit: 10,
            page: pageNumber
        }
    }).then(routerResponse => {
        let result: Handler[] = []
        for (let elem of routerResponse.msg) {
            result.push(new Handler(elem))
        }
        return new Promise<Handler[]>(resolve => resolve(result))
    })
}


export function apiHandlerMark(HandlerId: number, markFlag: boolean) {
    return routerRequest({
        method: 'POST',
        url: '/data/framework/mark',
        params: {
            id: HandlerId,
            flag: markFlag
        }
    })
}

export function apiServletMark(servletId:number, markFlag: boolean){
    return routerRequest({
        method: 'POST',
        url: '/data/middleware/mark',
        params: {
            id: servletId,
            flag: markFlag
        }
    })
}
