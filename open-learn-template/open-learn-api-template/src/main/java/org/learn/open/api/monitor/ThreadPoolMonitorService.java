package org.learn.open.api.monitor;

import java.util.List;
import java.util.Queue;

import org.learn.open.monitor.threadpool.model.ThreadPoolInformation;

/**
 * @Author: jack-yu
 * @Description: 线程池状态服务
 */
public interface ThreadPoolMonitorService {
    /**
     * 根据名字获取运行线程池状态
     */
    ThreadPoolInformation getThreadPoolRunTimeInformation(String threadPoolName);

    /**
     * 获取所有线程池状态
     * */
    List<ThreadPoolInformation> getAllThreadPoolRunTimeInformation();

    /**
     * 注册监控线程池
     * */
    ThreadPoolInformation registerThreadPoolMonitor(String threadPoolName, String beanName);

    /**
     * 释放线程池(谨慎操作)
     * */
    Boolean releaseThreadPool(String threadPoolName);

    /**
     * //TODO
     * 未实现
     * 释放线程池并备份队列(谨慎操作)此处所指队列为java内置队列实现。外部队列可使用发布订阅等容错和消费方式
     * */
    Boolean releaseThreadPoolAndBackupQueue(String threadPoolName, Queue queue);
    /*    //重置线程池大小扩容不推荐(重置对象句柄)
        ThreadPoolInformation resizeRuntimeThreadPool(String threadPoolName);*/
}
