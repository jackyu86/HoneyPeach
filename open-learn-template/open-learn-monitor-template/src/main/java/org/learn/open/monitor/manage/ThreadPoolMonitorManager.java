package org.learn.open.monitor.manage;

import java.util.List;

import org.learn.open.monitor.model.ThreadPoolInformation;


/**
 * @Author: jack-yu
 * @Description:  线程池管理器
 */
public interface ThreadPoolMonitorManager {

    ThreadPoolInformation getTPInformationByName(String name);

    List<ThreadPoolInformation> getAllTPInformation();

    ThreadPoolInformation registerThreadPoolMonitor(String threadPoolName, String beanId);
}
