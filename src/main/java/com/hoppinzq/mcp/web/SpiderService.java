package com.hoppinzq.mcp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.util.Date;

@Service
public class SpiderService {

    @Tool(name = "get_csdn_data", description = "爬取csdn上的博客内容，注意：返回格式如下：\n" +
            "{\"now\": \"当前时间\",\"error\": \"错误信息（如果博客爬取失败的话）\"," +
            "\"blogTitle\": \"博客标题\",\"blogCategory\": \"博客分类\",\"blogText\": \"博客正文\"," +
            "\"blogAuthor\": \"博客作者\",\"image\": \"博客封面图片\",\"blogDate\": \"发布日期\"}\n" +
            "你要注意：1、关注博客标题、分类、发布日期、封面图片、发布日期这几个字段；" +
            "2、为博客正文生成一个简短的描述；3、将博客正文以markdown格式提供给其他工具或者辅助。\n" +
            "后续编写博客的时候，需要用到这些内容")
    public String getWebData(@ToolParam(required = true, description = "网页链接") String link) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("now", new Date().toString());
        WebMessageContext.enter(response);
        try {
            Spider.create(new CSDNProcessor()).addUrl(link).thread(1).run();
        } catch (Exception e) {
            response.put("error", e.getMessage());
        } finally {
            WebMessageContext.exit();
        }
        return response.toString();
    }
}

