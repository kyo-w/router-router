package router.server.controller;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private Integer code;
    private Object msg;

    public static ApiResponse status200(Object msg) {
        return new ApiResponse(200, msg);
    }

    public static ApiResponse status400(Object msg) {
        return new ApiResponse(400, msg);
    }

    public static ApiResponse Ok() {
        return status200("Complete");
    }

    public static ApiResponse Fail() {
        return status400("Fail");
    }
}
