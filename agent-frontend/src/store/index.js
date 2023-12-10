import {createStore} from "vuex";

const store = createStore({
    state: {
        stackFlag: false,
        contextFlag: false,
        frameworkFlag: false
    },
    mutations: {
        FLUSH_FLAG(state, type) {
            if (type === 'context') {
                state.contextFlag = true
            }
            if (type === 'framework') {
                state.frameworkFlag = true
            }
            if (type === 'stack') {
                state.stackFlag = true
            }
        },
        CLEAN_FLAG(state, type){
            if (type === 'context') {
                state.contextFlag = false
            }
            if (type === 'framework') {
                state.frameworkFlag = false
            }
            if (type === 'stack') {
                state.stackFlag = false
            }
        }
    }
})
export default store