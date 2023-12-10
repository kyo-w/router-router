package com.kyodream.debugger.utils;

public class ApiResponse {
    private Integer code;
    private Object msg;

    public ApiResponse(Integer code, Object msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ApiResponse ok(Object msg) {
        return new ApiResponse(200, msg);
    }

    public static ApiResponse fail(Object msg) {
        return new ApiResponse(400, msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
