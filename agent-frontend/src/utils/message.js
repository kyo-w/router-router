import {ElNotification} from 'element-plus'

export function Message(title, message, type) {
    ElNotification({
        title: title,
        message: message,
        position: 'bottom-left',
        type: type
    })
}