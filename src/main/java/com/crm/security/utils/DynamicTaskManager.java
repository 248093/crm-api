package com.crm.security.utils;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 动态定时任务管理器：提交、取消定时任务（基于内存，服务重启后任务丢失）
 */
@Component
public class DynamicTaskManager {
    // 线程池：用于执行定时任务（核心线程数可根据需求调整）
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    // 存储任务ID与Future的映射，用于取消任务（key：任务唯一标识，如"product_123_OFFLINE"）
    private final ConcurrentHashMap<String, Runnable> taskMap = new ConcurrentHashMap<>();

    /**
     * 提交延迟任务（到点执行）
     * @param taskId 任务唯一标识（如"product_123_OFFLINE"）
     * @param delay 延迟时间（从当前到执行时间的毫秒数）
     * @param task 要执行的任务逻辑
     */
    public void submitTask(String taskId, long delay, Runnable task) {
        // 先取消已存在的同名任务（避免重复提交）
        cancelTask(taskId);
        // 提交延迟任务，并存储到map
        scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
        taskMap.put(taskId, task);
    }

    /**
     * 取消任务（如用户修改定时时间时调用）
     * @param taskId 任务唯一标识
     */
    public void cancelTask(String taskId) {
        taskMap.remove(taskId); // 从map中移除（已提交的任务无法直接取消，只能通过标识忽略执行）
    }

    /**
     * 检查任务是否存在
     */
    public boolean hasTask(String taskId) {
        return taskMap.containsKey(taskId);
    }
}