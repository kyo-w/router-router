import {routerRequest, RouterResponse} from "@/utils/requests"
import {ElMessage} from "element-plus";

export class Project {
    id: string
    name: string
    hostname: string
    port: number
    createTime: Date
    private empty: boolean
    public static default: Project = new Project(null)

    constructor(response: any) {
        this.id = response !== null ? response.id : ""
        this.hostname = response !== null ? response.hostname : ""
        this.port = response !== null ? response.port : -1
        this.name = response !== null ? response.alias : ""
        this.createTime = response !== null ? response.createTime : new Date()
        this.empty = response === null
    }

    public isEmpty() {
        return this.empty
    }
}

export function apiGetAllProject(): Promise<Project[]> {
    return routerRequest({
        url: '/project/get/all',
        method: 'get'
    }).then(routerResponse => {
        let result: Project[] = []
        for (let project of routerResponse.msg) {
            result.push(new Project(project))
        }
        return new Promise<Project[]>(resolve => resolve(result))
    })
}


export function apiCreateProject(project: Project | any): Promise<Boolean> {
    return routerRequest({
        url: '/project/creat',
        method: 'post',
        data: {
            alias: project.name,
            hostname: project.hostname,
            port: project.port,
            createTime: project.createTime
        },
    }).then(routerResponse => {
        return new Promise<Boolean>(resolve => {
            ElMessage({
                type: routerResponse.code === 200 ? "success" : "error",
                message: routerResponse.msg,
            })
            resolve(routerResponse.code === 200)
        })
    })
}

export function apiSelectProject(id: string) {
    return routerRequest({
        url: '/project/select/' + id,
        method: 'post',
    })
}

export function apiLogoutProject() {
    return routerRequest({
        url: '/project/logout',
        method: 'post',
    })
}

export function apiGetCurrentProject(): Promise<Project> {
    return routerRequest({
        url: '/project/current',
        method: 'GET',
    }).then(routerResponse => {
        return new Promise<Project>(resolve => resolve(new Project(routerResponse.msg)))
    })
}

export function apiDeleteProject(id: string) {
    return routerRequest({
        url: '/project/delete/' + id,
        method: 'DELETE'
    })
}
