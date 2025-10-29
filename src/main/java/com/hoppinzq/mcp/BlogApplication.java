package com.hoppinzq.mcp;

import com.hoppinzq.mcp.blog.BlogService;
import com.hoppinzq.mcp.git.GitService;
import com.hoppinzq.mcp.web.SpiderService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    @Bean
    public List<ToolCallback> toolCallbacks(GitService gitService, BlogService blogService, SpiderService spiderService) {
        return List.of(ToolCallbacks.from(gitService, blogService,spiderService));
    }
}

