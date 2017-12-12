package org.learn.open.monitor.threadpool.model;

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
                '}';
    }
}
