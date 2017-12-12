package org.learn.open.service;

import java.util.List;
import java.util.Queue;

import org.learn.open.api.monitor.ThreadPoolMonitorService;
import org.learn.open.monitor.threadpool.manage.ThreadPoolMonitorManager;
import org.learn.open.monitor.threadpool.model.ThreadPoolInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: jack-yu
 * @Description:
 */
@Service(value = "threadPoolMonitorService")
public class ThreadPoolMonitorServiceImpl implements ThreadPoolMonitorService {

    @Autowired
    private ThreadPoolMonitorManager threadPoolMonitorManager;

    @Override
    public ThreadPoolInformation getThreadPoolRunTimeInformation(String threadPoolName) {
        return threadPoolMonitorManager.getTPInformationByName(threadPoolName);
    }

    @Override
    public List<ThreadPoolInformation> getAllThreadPoolRunTimeInformation() {
        return threadPoolMonitorManager.getAllTPInformation();
    }

    @Override
    public ThreadPoolInformation registerThreadPoolMonitor(String threadPoolName, String beanName) {
        return threadPoolMonitorManager.registerThreadPoolMonitor(threadPoolName,beanName);
    }

    @Override
    public Boolean releaseThreadPool(String threadPoolName) {
        return null;
    }

    @Override
    public Boolean releaseThreadPoolAndBackupQueue(String threadPoolName, Queue queue) {
        return null;
    }
}
