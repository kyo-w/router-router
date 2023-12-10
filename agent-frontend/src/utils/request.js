import axios from "axios";
import config from './config'

const service = axios.create({
    baseURL: 'http://' + config.GetBaseUrl(),
    withCredentials: false,
    timeout: 5000
})

export default service
