package com.hoppinzq.mcp.constants;

/**
 * 博客系统常量类
 * 集中管理系统中使用的各种常量，包括服务器配置、API配置等
 */
public class BlogConstants {
    // SFTP服务器配置
    public static final String SFTP_HOST = "ssh";
    public static final int SFTP_PORT = 22;
    public static final String SFTP_USERNAME = "ssh用户名";
    public static final String SFTP_PASSWORD = "ssh密码";
    
    // API配置
    public static final String API_BASE_URL = "openAI代理地址";
    public static final String CHAT_COMPLETIONS_URL = API_BASE_URL + "/chat/completions";
    public static final String IMAGES_GENERATIONS_URL = API_BASE_URL + "/images/generations";
    public static final String API_KEY = "sk-APIKEY";
    public static final String CONTENT_TYPE_JSON = "application/json";
    
    // AI模型配置
    public static final String MODEL_GPT_4O_MINI = "gpt-4o-mini";
    public static final String MODEL_DALL_E_3 = "dall-e-3";
    public static final String IMAGE_SIZE = "1024x1024";
}
