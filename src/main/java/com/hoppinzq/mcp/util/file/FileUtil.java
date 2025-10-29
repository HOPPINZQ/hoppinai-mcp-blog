package com.hoppinzq.mcp.util.file;

import com.hoppinzq.mcp.constants.BlogConstants;
import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static List<String> stringList;
    private Session session;
    private ChannelSftp channelSftp;

    public static String fileUpload(String localPath,String remotePath) {
        FileUtil uploader = new FileUtil();
        try {
            init(uploader);;
            uploader.uploadDirectory(localPath, remotePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            uploader.disconnect();
        }
        return stringList.toString();
    }

    private static void init(FileUtil uploader) throws JSchException {
        uploader.connect(BlogConstants.SFTP_HOST, BlogConstants.SFTP_PORT, BlogConstants.SFTP_USERNAME, BlogConstants.SFTP_PASSWORD);
        stringList = new ArrayList<>();
    }

    /**
     * 连接SFTP服务器
     */
    public void connect(String host, int port, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        channelSftp = (ChannelSftp) channel;
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * 上传整个目录（包含所有文件和子目录）
     */
    public void uploadDirectory(String localPath, String remotePath) throws SftpException, IOException {
        File localDir = new File(localPath);

        if (!localDir.exists() || !localDir.isDirectory()) {
            throw new IOException("本地目录不存在或不是目录: " + localPath);
        }

        // 创建远程目录
        try {
            channelSftp.mkdir(remotePath);
        } catch (SftpException e) {
            // 目录可能已存在，忽略错误
            if (e.id != ChannelSftp.SSH_FX_FAILURE) {
                throw e;
            }
        }

        // 切换到远程目录
        channelSftp.cd(remotePath);

        // 递归上传所有文件和子目录
        uploadDirectoryRecursive(localDir, "");
    }

    /**
     * 递归上传目录
     */
    private void uploadDirectoryRecursive(File localDir, String relativePath) throws SftpException, IOException {
        File[] files = localDir.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            String remoteFilePath = relativePath + file.getName();

            if (file.isDirectory()) {
                // 创建远程子目录
                try {
                    channelSftp.mkdir(remoteFilePath);
                } catch (SftpException e) {
                    // 目录可能已存在，忽略错误
                    if (e.id != ChannelSftp.SSH_FX_FAILURE) {
                        throw e;
                    }
                }

                // 递归上传子目录
                String currentDir = channelSftp.pwd();
                channelSftp.cd(remoteFilePath);
                uploadDirectoryRecursive(file, "");
                channelSftp.cd(currentDir); // 返回上级目录

            } else {
                // 上传文件
                uploadFile(file, remoteFilePath);
            }
        }
    }

    /**
     * 上传单个文件
     */
    public void uploadFile(File localFile, String remoteFilePath) throws SftpException, IOException {
        try (FileInputStream inputStream = new FileInputStream(localFile)) {
            channelSftp.put(inputStream, remoteFilePath);
            stringList.add("上传成功: " + localFile.getAbsolutePath() + " -> " + remoteFilePath);
        }
    }

    /**
     * 检查远程目录是否存在
     */
    private boolean remoteDirectoryExists(String path) {
        try {
            channelSftp.stat(path);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }
}