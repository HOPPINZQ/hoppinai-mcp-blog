package com.hoppinzq.mcp.util.api;

public record ApiResponse(Integer code, String msg, Object data) {

    public static ApiResponse fail(String message) {
        return new ApiResponse(500, message, null);
    }

    public static ApiResponse success(Object data) {
        return new ApiResponse(200, "调用成功！", data);
    }
}
