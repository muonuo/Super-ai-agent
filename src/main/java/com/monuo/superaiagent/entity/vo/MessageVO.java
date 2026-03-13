package com.monuo.superaiagent.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageVO {

    /**
     * 消息角色：user/assistant/system/tool
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 思考步骤（仅智能体应用）
     */
    private List<String> thinkingSteps;

    /**
     * 思考用时（秒，仅智能体应用）
     */
    private Integer thinkingTime;
}
