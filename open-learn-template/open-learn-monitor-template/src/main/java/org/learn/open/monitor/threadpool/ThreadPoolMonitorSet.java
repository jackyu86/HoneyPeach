package org.learn.open.monitor.threadpool;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: jack-yu
 * @Description:  全局线程池监控集合
 */
public enum ThreadPoolMonitorSet {

    INSTANCE{
        private final Logger LOGGER= LoggerFactory.getLogger(ThreadPoolMonitorSet.class);
        //
        private Map<String,Object> threadPoolMap=new ConcurrentHashMap<String, Object>();

        @Override
        public boolean register(String threadPoolName, Object ThreadPoolObject) {

            if(null!=threadPoolMap.get(threadPoolName)){
                LOGGER.error("ThreadPool Name [{}] has exist. ",threadPoolName);
                throw new RuntimeException("Thread Pool Name has exist. threadName="+threadPoolName);
            }

            threadPoolMap.put(threadPoolName,ThreadPoolObject);
            return true;
        }

        @Override
        public Object getThreadPoolObject(String threadPoolName) {

            return threadPoolMap.get(threadPoolName);
        }

        @Override
        public Map<String, Object> getAllThreadPools() {

            return  Collections.unmodifiableMap(threadPoolMap);
        }
    };

    public abstract boolean register(String ThreadPoolName,Object ThreadPoolObject);

    public abstract Object getThreadPoolObject(String ThreadPoolName);

    public abstract Map<String,Object> getAllThreadPools();

}
