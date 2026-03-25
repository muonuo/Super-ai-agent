#!/bin/bash

# 从 Git 中移除根目录下的 Markdown 文件（除了 README.md）
git rm --cached *.md
git add README.md

echo "已从 Git 中移除文档文件，但保留了本地文件"
echo "请执行以下命令提交更改："
echo "git commit -m '移除文档文件，不提交到远程仓库'"
