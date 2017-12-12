package org.learn.open.monitor.threadpool.model;

import java.util.concurrent.BlockingQueue;

/**
 * @Author: jack-yu
 * @Description:  线程池维度信息
 */
public class ThreadPoolInformation {

    private String name;

    private int threadNum;

    private int coreThreadNum;

    private int maxThreadNum;

    private int queueLength;
    //是否执行关闭
    private boolean isShutdown;
    //活跃线程数
    private int activeCount;
    //已执行数量
    private long taskCount;
    //队列
    private BlockingQueue queues;

    public boolean isShutdown() {
        return isShutdown;
    }

    public void setShutdown(boolean shutdown) {
        isShutdown = shutdown;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }

    public BlockingQueue getQueues() {
        return queues;
    }

    public void setQueues(BlockingQueue queues) {
        this.queues = queues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public int getCoreThreadNum() {
        return coreThreadNum;
    }

    public void setCoreThreadNum(int coreThreadNum) {
        this.coreThreadNum = coreThreadNum;
    }

    public int getMaxThreadNum() {
        return maxThreadNum;
    }

    public void setMaxThreadNum(int maxThreadNum) {
        this.maxThreadNum = maxThreadNum;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(int queueLength) {
        this.queueLength = queueLength;
    }

    @Override
    public String toString() {
        return "ThreadPoolInformation{" +
                "name='" + name + '\'' +
                ", threadNum=" + threadNum +
                ", coreThreadNum=" + coreThreadNum +
                ", maxThreadNum=" + maxThreadNum +
                ", queueLength=" + queueLength +
                ", isShutdown=" + isShutdown +
                ", activeCount=" + activeCount +
                ", taskCount=" + taskCount +
                ", queues=" + queues +
                '}';
    }
}
