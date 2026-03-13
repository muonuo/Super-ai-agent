package com.monuo.superaiagent.agent.monitor;

import lombok.Data;

/**
 * 执行监控器
 * 负责监控智能体的执行时间和状态
 */
@Data
public class ExecutionMonitor {

    // 执行开始时间
    private long executionStartTime;

    // 最大执行时间（毫秒）
    private long maxExecutionTimeMs = 300000; // 默认5分钟

    // 是否超时
    private boolean executionTimeout = false;

    /**
     * 开始监控
     */
    public void start() {
        this.executionStartTime = System.currentTimeMillis();
        this.executionTimeout = false;
    }

    /**
     * 检查是否超时
     */
    public boolean isTimeout() {
        if (executionTimeout) {
            return true;
        }
        long elapsed = getElapsedTime();
        if (elapsed > maxExecutionTimeMs) {
            executionTimeout = true;
            return true;
        }
        return false;
    }

    /**
     * 获取已用时间
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - executionStartTime;
    }

    /**
     * 重置监控器
     */
    public void reset() {
        this.executionStartTime = System.currentTimeMillis();
        this.executionTimeout = false;
    }

    /**
     * 强制设置超时
     */
    public void forceTimeout() {
        this.executionTimeout = true;
    }
}
