package com.monuo.superaiagent.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 用户交互工具 - 当LLM遇到问题时向用户请求帮助
 *
 * 使用场景：
 * 1. 信息不足时询问用户
 * 2. 需要用户确认某些信息
 * 3. 遇到困难请求用户提供思路
 * 4. 需要用户补充必要参数或数据
 */
@Slf4j
@Component
public class UserAssistanceTool {

    /**
     * 向用户请求更多信息或帮助
     *
     * @param question     需要询问用户的问题
     * @param context      当前的问题上下文（帮助用户理解）
     * @param options      可选的选项列表（如果有）
     * @param urgency      问题的紧急程度：low（普通）, medium（重要）, high（紧急）
     */
    @Tool(name = "askUser", description = """
            Ask the user for help when you encounter problems or need more information.

            Use this tool when:
            1. You don't have enough information to complete the task
            2. You're unsure about something and need clarification
            3. You need the user to provide necessary data or parameters
            4. You want to verify something with the user before proceeding
            5. You need the user's input to solve a problem

            IMPORTANT: Don't try to guess or assume information. Ask the user instead!

            Examples of when to use:
            - "What file should I read?"
            - "Which email address should I use?"
            - "Should I continue with option A or B?"
            - "I need your password to proceed, can you provide it?"
            """)
    public String askUser(
            @ToolParam(description = """
                    The specific question you want to ask the user.
                    Be clear and specific about what information you need.

                    Good examples:
                    - "请提供要读取的文件名"
                    - "请确认是要发送到哪个邮箱？"
                    - "您希望我用中文还是英文回复？"

                    Bad examples:
                    - "怎么办？"
                    - "我不知道"
                    """) String question,

            @ToolParam(description = """
                    Context about the current problem or situation.
                    This helps the user understand why you need this information.

                    Include:
                    - What you're trying to do
                    - What information is missing
                    - Why you can't proceed without this info
                    """, required = false) String context,

            @ToolParam(description = """
                    Optional: A list of choices for the user to select from.
                    Format as comma-separated options.

                    Example: "选项A, 选项B, 选项C"
                    """, required = false) String options,

            @ToolParam(description = """
                    How urgent is this question?
                    - low: Can wait, informational question
                    - medium: Important, need answer to proceed
                    - high: Blocking issue, need immediate help

                    Default is medium.
                    """, required = false) String urgency) {

        StringBuilder response = new StringBuilder();

        response.append("📌 需要您的帮助\n\n");

        response.append("❓ 问题：").append(question).append("\n\n");

        if (context != null && !context.isEmpty()) {
            response.append("📋 上下文：").append(context).append("\n\n");
        }

        if (options != null && !options.isEmpty()) {
            response.append("🔢 请选择：\n");
            String[] optionList = options.split(",");
            for (int i = 0; i < optionList.length; i++) {
                response.append("   ").append(i + 1).append(". ").append(optionList[i].trim()).append("\n");
            }
            response.append("\n");
        }

        String urgencyText = (urgency != null && !urgency.isEmpty()) ? urgency.toLowerCase() : "medium";
        switch (urgencyText) {
            case "high" -> response.append("⚠️ 紧急程度：高 - 请尽快回复\n");
            case "low" -> response.append("ℹ️ 紧急程度：低 - 您有空时回复即可\n");
            default -> response.append("⚡ 紧急程度：中等 - 请尽快回复\n");
        }

        log.info("向用户请求帮助: {}", question);

        return response.toString();
    }

    /**
     * 确认用户意图
     * 当有多个可能的理解时，让用户确认
     *
     * @param description 对当前情况的描述
     * @param optionA    选项A描述
     * @param optionB    选项B描述
     */
    @Tool(name = "confirmWithUser", description = """
            Confirm with the user when there are multiple possible interpretations.
            Use this to avoid misunderstandings or taking the wrong action.

            Examples:
            - "您说的是发邮件还是发短信？"
            - "是要读取这个文件还是那个文件？"
            - "确认一下：您要删除这个文件吗？"
            """)
    public String confirmWithUser(
            @ToolParam(description = "What you understand from the user's request") String description,
            @ToolParam(description = "First option") String optionA,
            @ToolParam(description = "Second option") String optionB) {

        return "🤔 请确认您的意图：\n\n" +
                "我的理解：" + description + "\n\n" +
                "请确认：\n" +
                "1. " + optionA + "\n" +
                "2. " + optionB + "\n";
    }

    /**
     * 寻求建议
     * 当LLM有多种解决方案时，询问用户偏好
     *
     * @param problemDescription 当前面临的问题
     * @param solutionA         方案A描述
     * @param solutionB         方案B描述
     * @param prosAndCons      可选的方案优缺点对比
     */
    @Tool(name = "askForAdvice", description = """
            Ask the user for advice when you have multiple solutions but unsure which to choose.

            Use this when:
            - You have multiple ways to solve a problem
            - You need user preference to decide
            - The user might have a preference you should respect
            """)
    public String askForAdvice(
            @ToolParam(description = "Description of the problem or task") String problemDescription,
            @ToolParam(description = "First solution option") String solutionA,
            @ToolParam(description = "Second solution option") String solutionB,
            @ToolParam(description = "Optional comparison of pros and cons", required = false) String prosAndCons) {

        StringBuilder response = new StringBuilder();
        response.append("💡 需要您的建议\n\n");
        response.append("任务：").append(problemDescription).append("\n\n");
        response.append("方案A：").append(solutionA).append("\n");
        response.append("方案B：").append(solutionB).append("\n");

        if (prosAndCons != null && !prosAndCons.isEmpty()) {
            response.append("\n对比分析：\n").append(prosAndCons).append("\n");
        }

        response.append("\n请告诉我您倾向哪个方案，或者有其他建议？");

        return response.toString();
    }
}
