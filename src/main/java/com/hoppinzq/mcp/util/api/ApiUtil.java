package com.hoppinzq.mcp.util.api;

import com.hoppinzq.mcp.constants.BlogConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;

import java.time.Duration;

public class ApiUtil {

    public static String getImagePrompt(String imageMessage) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode params = mapper.createObjectNode();
        params.put("model", BlogConstants.MODEL_GPT_4O_MINI);
        ObjectNode systemPrompt = mapper.createObjectNode();
        systemPrompt.put("role", "system");
        systemPrompt.put("content", "你是一个图片提示词生成专家，每次我都会给你一篇文章的内容，请你根据这篇文章内容生成一个对应的图片提示词。");
        ObjectNode userPrompt = mapper.createObjectNode();
        userPrompt.put("role", "user");
        userPrompt.put("content", imageMessage);
        ArrayNode messages = mapper.createArrayNode();
        messages.add(systemPrompt);
        messages.add(userPrompt);
        params.set("messages", messages);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, params.toString());
        Request request = new Request.Builder()
                .url(BlogConstants.CHAT_COMPLETIONS_URL)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + BlogConstants.API_KEY)
                .addHeader("Content-Type", BlogConstants.CONTENT_TYPE_JSON)
                .build();
        Response response = client.newCall(request).execute();
        JsonNode node = mapper.readTree(response.body().string());
        return node.get("choices").get(0).get("message").get("content").asText();
    }

    public static String getImageUrl(String prompt) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode params = mapper.createObjectNode();
        params.put("model", BlogConstants.MODEL_DALL_E_3);
        params.put("prompt", prompt);
        params.put("n", 1);
        params.put("size", BlogConstants.IMAGE_SIZE);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(Duration.ofSeconds(60))
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, params.toString());
        Request request = new Request.Builder()
                .url(BlogConstants.IMAGES_GENERATIONS_URL)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + BlogConstants.API_KEY)
                .addHeader("Content-Type", BlogConstants.CONTENT_TYPE_JSON)
                .build();
        Response response = client.newCall(request).execute();
        JsonNode node = mapper.readTree(response.body().string());
        return node.get("data").get(0).get("url").asText();
    }
}
