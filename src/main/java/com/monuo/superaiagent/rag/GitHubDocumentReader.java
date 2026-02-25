package com.monuo.superaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Component
@Slf4j
public class GitHubDocumentReader implements DocumentReader {

    private final String repoOwner;
    private final String repoName;
    private final String branch;
    private final String githubToken;

    public GitHubDocumentReader(
            @Value("${spring.ai.rag.github.owner:alibaba}") String repoOwner,
            @Value("${spring.ai.rag.github.repo:spring-ai-alibaba}") String repoName,
            @Value("${spring.ai.rag.github.branch:main}") String branch,
            @Value("${spring.ai.rag.github.token}") String githubToken) {
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.branch = branch;
        this.githubToken = githubToken;
    }

    @Override
    public List<Document> get() {
        List<Document> documents = new ArrayList<>();

        try {
            documents.add(fetchReadme());
            documents.add(fetchRepoInfo());
            documents.add(fetchFileTree());
        } catch (Exception e) {
            log.error("获取GitHub仓库信息失败", e);
        }

        return documents;
    }

    private Document fetchReadme() throws Exception {
        String readmeUrl = String.format(
                "https://api.github.com/repos/%s/%s/readme",
                repoOwner, repoName);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(readmeUrl, Map.class);

        String content = "";
        if (response != null && response.get("content") != null) {
            content = new String(
                    java.util.Base64.getDecoder().decode(
                            ((String) response.get("content")).replace("\n", "")),
                    StandardCharsets.UTF_8);
        }

        return new Document(
                "github_readme_" + repoName,
                content,
                Map.of(
                        "source", "github",
                        "repo_owner", repoOwner,
                        "repo_name", repoName,
                        "type", "readme",
                        "url", String.format("https://github.com/%s/%s", repoOwner, repoName)
                )
        );
    }

    private Document fetchRepoInfo() throws Exception {
        String repoUrl = String.format(
                "https://api.github.com/repos/%s/%s",
                repoOwner, repoName);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(repoUrl, Map.class);

        StringBuilder info = new StringBuilder();
        if (response != null) {
            info.append("# Repository Information\n\n");
            info.append("- Full Name: ").append(response.get("full_name")).append("\n");
            info.append("- Description: ").append(response.get("description")).append("\n");
            info.append("- Stars: ").append(response.get("stargazers_count")).append("\n");
            info.append("- Forks: ").append(response.get("forks_count")).append("\n");
            info.append("- Language: ").append(response.get("language")).append("\n");
            info.append("- Topics: ").append(response.get("topics")).append("\n");
            info.append("- Created At: ").append(response.get("created_at")).append("\n");
            info.append("- Last Updated: ").append(response.get("updated_at")).append("\n");
            info.append("- License: ").append(response.get("license")).append("\n");
        }

        return new Document(
                "github_info_" + repoName,
                info.toString(),
                Map.of(
                        "source", "github",
                        "repo_owner", repoOwner,
                        "repo_name", repoName,
                        "type", "repo_info"
                )
        );
    }

    private Document fetchFileTree() throws Exception {
        String treeUrl = String.format(
                "https://api.github.com/repos/%s/%s/git/trees/%s?recursive=1",
                repoOwner, repoName, branch);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(treeUrl, Map.class);

        StringBuilder fileTree = new StringBuilder();
        fileTree.append("# Repository File Structure\n\n");

        if (response != null && response.get("tree") != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> tree = (List<Map<String, Object>>) response.get("tree");

            for (Map<String, Object> item : tree) {
                String path = (String) item.get("path");
                String type = (String) item.get("type");

                if ("blob".equals(type)) {
                    String[] parts = path.split("/");
                    String indent = "  ".repeat(Math.max(0, parts.length - 1));
                    fileTree.append(indent).append("- ").append(parts[parts.length - 1]).append("\n");
                } else if ("tree".equals(type)) {
                    fileTree.append("- ").append(path).append("/\n");
                }
            }
        }

        return new Document(
                "github_tree_" + repoName,
                fileTree.toString(),
                Map.of(
                        "source", "github",
                        "repo_owner", repoOwner,
                        "repo_name", repoName,
                        "type", "file_tree"
                )
        );
    }
}
