package org.learn.open.monitor.threadpool.manage;

import java.util.List;

import org.learn.open.monitor.threadpool.model.ThreadPoolInformation;


/**
 * @Author: jack-yu
 * @Description:  线程池管理器
 */
public interface ThreadPoolMonitorManager {
    /**
     * 根据名字获取线程池信息
     * */
    ThreadPoolInformation getTPInformationByName(String name);
    /**
     * 获取所有线程池信息
     * */
    List<ThreadPoolInformation> getAllTPInformation();
    /**
     * 获取spring bean 注册监控线程池
     * 返回null注册失败
     * */
    ThreadPoolInformation registerThreadPoolMonitor(String threadPoolName, String beanId);
}
