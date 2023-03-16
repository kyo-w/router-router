package com.kyodream.end.utils;


/**
 * 4xx 为非预期
 * 3xx 为信息
 * 2xx 为功能正常
 */
public class Constant {
    public static ApiResponse emptyAttr = new ApiResponse(404, "未设定连接参数");
    public static ApiResponse hasExist = new ApiResponse(400, "已经存在一个连接");
    public static ApiResponse connectError = new ApiResponse(404, "连接异常");
    public static ApiResponse unknownParam = new ApiResponse(404, "未知参数");

    public static ApiResponse paramNoSet = new ApiResponse(400, "未连接目标debug端口");


    public static ApiResponse emptyData = new ApiResponse(300, "空数据");
    public static ApiResponse connectRepeat = new ApiResponse(300, "重复连接");

    public static ApiResponse anAlyComplete = new ApiResponse(200, true);
    public static ApiResponse anAlyNoComplete = new ApiResponse(200, false);
    public static ApiResponse settingSuccess = new ApiResponse(200, "设置成功");
    public static ApiResponse connectSuccess = new ApiResponse(200, "连接成功");
    public static ApiResponse connectExist = new ApiResponse(200, "连接存在");
    public static ApiResponse connectNoExist = new ApiResponse(201, "连接不存在");
    public static ApiResponse stopSuccess = new ApiResponse(200, "停止成功");
    public static ApiResponse handleSuccess = new ApiResponse(200, "处理完成");

    public static ApiResponse existData = new ApiResponse(200, "存在数据");

}
