package com.hoppinzq.mcp.git;

import com.hoppinzq.mcp.util.cmd.CmdResponse;
import com.hoppinzq.mcp.util.cmd.CmdUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class GitService {

    private List<GitProject> hoppinGitProjects;

    @PostConstruct
    public void init() {
        // jdk 9
        hoppinGitProjects = List.of(new GitProject("hoppinai的博客项目", "这是hoppin的博客项目",
                "https://gitee.com/hoppin/fuwari.git", "main"));
    }

    @Tool(name = "get_hoppin_git_project", description = "获取hoppin所有的git项目")
    public List<GitProject> getHoppinGitProject() {
        return hoppinGitProjects;
    }

    @Tool(name = "clone_blog_from_git", description = "克隆git的项目到本地，克隆成功后，请使用get_git_readme工具阅读项目的README.md。" +
            "如果已经克隆过了，则无需克隆，直接提示用户。")
    public CmdResponse cloneBlog(
            @ToolParam(description = "远端仓库地址") String remoteUrl,
            @ToolParam(description = "拉取到本地的目录") String localDir
    ) {
        return CmdUtil.cmd(null, "git", "git", "clone",remoteUrl, localDir);
    }

    @Tool(name = "get_git_readme", description = "获取项目的README.md文件内容")
    public String getGitReadme(@ToolParam(description = "项目文件夹") String localDir) {
        try {
            // JDK 11
            return Files.readString(Path.of(localDir + FileSystems.getDefault().getSeparator() + "README.md"));
        } catch (IOException e) {
            return "读取文件失败: " + e.getMessage();
        }
    }

    @Tool(name = "commit_git", description = "提交代码工具，只有用户要求提交代码时，才能使用该工具。" +
            "注意：你需要在提交前，在'\\src\\content\\spec\\changelog.md'文件内维护更新日志(如果文件存在的话)，然后才能提交代码。")
    public CmdResponse commitGit(@ToolParam(description = "项目文件夹") String localDir,
                                 @ToolParam(description = "提交信息，结合修改的信息生成") String commitMsg
    ) {
        CmdResponse gitAdd = CmdUtil.cmd(localDir, "git", "git", "add", "-A");
        if(!gitAdd.success()) {
            return gitAdd;
        }else{
            return CmdUtil.cmd(localDir, "git", "git", "commit", "-m", commitMsg);
        }
    }
}

record GitProject(String name, String description, String remoteUrl, String branch) {
}
