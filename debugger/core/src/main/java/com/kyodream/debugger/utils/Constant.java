package com.kyodream.debugger.utils;


/**
 * 4xx 异常
 * 3xx 存在非预期结果
 * 2xx 存在预期结果
 */
public class Constant {
    public static ApiResponse unknownParam = new ApiResponse(404, "未知参数");
    public static ApiResponse connectRepeat = new ApiResponse(300, "重复连接");
    public static ApiResponse connectExist = new ApiResponse(200, "连接存在");
    public static ApiResponse connectNoExist = new ApiResponse(300, "连接不存在");

}
