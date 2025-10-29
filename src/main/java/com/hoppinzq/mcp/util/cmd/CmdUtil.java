package com.hoppinzq.mcp.util.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CmdUtil {

    public static CmdResponse cmd(String projectPath, String mark, String... args) {
        StringBuffer buffer = new StringBuffer();
        try {
            List<String> command = new ArrayList<>();
            command.add("cmd");
            command.add("/c");
            command.add("chcp");
            command.add("65001");
            command.add("&&");
            // 添加实际命令
            for (String arg : args) {
                command.add(arg);
            }
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            if (projectPath != null) {
                File projectDir = new File(projectPath);
                if (!projectDir.exists() || !projectDir.isDirectory()) {
                    return CmdResponse.fail("项目路径不正确:" + projectPath);
                }
                processBuilder.directory(projectDir);
            }
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (mark == null) {
                            buffer.append(line);
                        } else {
                            buffer.append("[" + mark + "] " + line);
                        }
                        buffer.append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputThread.start();
            if (process.isAlive()) {
                process.waitFor();
            }
            outputThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return CmdResponse.fail("执行命令失败:" + e.getMessage());
        }
        return CmdResponse.success(buffer.toString().trim());
    }

    public static CmdResponse cmdIndependence(String projectPath, String args) {
        try {
            String fullCommand = String.format(
                    "cmd /c start \"独立进程\" cmd /k \"chcp 65001 && cd /d %s && %s\"",
                    projectPath,
                    args
            );
            Runtime.getRuntime().exec(fullCommand);
            return CmdResponse.success("进程已在独立窗口中运行");
        } catch (IOException e) {
            e.printStackTrace();
            return CmdResponse.fail("启动失败: " + e.getMessage());
        }
    }
}