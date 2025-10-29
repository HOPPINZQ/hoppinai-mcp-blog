package com.hoppinzq.mcp.blog;

import com.hoppinzq.mcp.util.api.ApiUtil;
import com.hoppinzq.mcp.util.cmd.CmdResponse;
import com.hoppinzq.mcp.util.cmd.CmdUtil;
import com.hoppinzq.mcp.util.file.FileUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class BlogService {

    @Tool(name = "start_blog_project", description = "在项目目录下启动博客项目，如果没有启动成功，尝试使用init_blog初始化项目，如果版本太低，告知用户升级版本")
    public CmdResponse startBlogProject(@ToolParam(description = "项目的目录") String localDir) {
        return CmdUtil.cmdIndependence(localDir, "pnpm run dev");
    }

    @Tool(name = "init_blog_project", description = "在项目目录下初始化博客项目，如果没有初始化成功，尝试使用install_environment安装环境，然后再执行init_blog")
    public CmdResponse initBlogProject(@ToolParam(description = "项目的目录") String localDir) {
        return CmdUtil.cmd(localDir, "pnpm", "pnpm","install");
    }

    @Tool(name = "install_environment", description = "安装必要环境，在安装前，告知用户要安装的环境。如果用户没有npm环境，提示用户安装node")
    public CmdResponse installEnvironment() {
        return CmdUtil.cmd(null, "npm", "npm", "i", "-g", "pnpm");
    }

    @Tool(name = "create_blog", description = "创建一个博客，需要传入博客名称，" +
            "注意：1、博客名称不能是中文；2、会在src/content/posts/下创建该博客的markdown文件；3、博客正文为markdown格式；" +
            "4、使用start_blog_project启动项目并预览博客，如果你已经启动过了，无需再次启动")
    public CmdResponse createBlog(@ToolParam(description = "项目的目录") String localDir,
                                  @ToolParam(description = "博客名称，不能是中文") String blogName) {
        return CmdUtil.cmd(localDir, "pnpm", "pnpm" ,"new-post" , blogName);
    }

    @Tool(name = "generate_image", description = "根据博客的内容生成图片")
    public String generateImage(@ToolParam(description = "博客的的绝对路径") String blogPath) throws Exception {
        String prompt = ApiUtil.getImagePrompt(Files.readString(Path.of(blogPath)));
        String imageUrl = ApiUtil.getImageUrl(prompt);
        String fileName = UUID.randomUUID() + ".png";
        Path blogDir = Paths.get(blogPath).getParent();
        try (var in = new URL(imageUrl).openStream()) {
            Files.copy(in, blogDir.resolve(fileName));
        }
        return "生成的图片保存到：" + blogDir + FileSystems.getDefault().getSeparator() + fileName + "，请在博客的image字段中引用图片的相对路径";
    }

    @Tool(name = "package_blog_project", description = "打包博客项目")
    public CmdResponse packageBlogProject(@ToolParam(description = "项目的目录") String localDir) {
        return CmdUtil.cmd(localDir, "npm", "npm", "run" ,"build");
    }

    @Tool(name = "deploy_blog_project", description = "部署博客项目，如果部署前没有打包，清先使用package_blog_project工具打包，然后传入打包文件夹的路径，打包文件夹一般是dist文件夹")
    public String deployBlogProject(@ToolParam(description = "打包文件夹的路径") String distDir) {
        String blogDir = System.getenv("BLOG_DIR");
        if(blogDir == null){
            blogDir = "hoppinai";
        }
        FileUtil.fileUpload(distDir,"/home/file/file/"+blogDir);
        return "博客项目部署成功，请访问：https://hoppinzq.com/%s/index.html".formatted(blogDir);
    }
}
