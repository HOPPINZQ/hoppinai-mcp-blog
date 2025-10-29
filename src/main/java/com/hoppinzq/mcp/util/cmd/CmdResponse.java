package com.hoppinzq.mcp.util.cmd;

import java.util.Date;

public record CmdResponse(Boolean success, Integer code, String message, Object data, String now) {

    public static CmdResponse fail(String message) {
        return new CmdResponse(false, -1, message, null,new Date().toString());
    }

    public static CmdResponse success(Object data) {
        return new CmdResponse(true, 0, "执行指令成功！", data,new Date().toString());
    }
}

