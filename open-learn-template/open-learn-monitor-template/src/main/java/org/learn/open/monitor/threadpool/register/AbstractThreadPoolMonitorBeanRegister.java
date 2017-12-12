package org.learn.open.monitor.threadpool.register;

import java.util.Map;

import org.learn.open.monitor.threadpool.ThreadPoolMonitorSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author jack yu
 * @Description : spring bean注册线程池监控
 * 使用方法:继承实现addThreadPool
 */
public abstract class AbstractThreadPoolMonitorBeanRegister implements InitializingBean {

    private static final Logger LOGGER= LoggerFactory.getLogger(AbstractThreadPoolMonitorBeanRegister.class);

    @Override
    public void afterPropertiesSet() throws Exception{
        Map<String,Object> threadPoolMap=this.addThreadPool();

        try{
            LOGGER.info("Begin to register ThreadPool. ");
            for(Map.Entry<String,Object> entry:threadPoolMap.entrySet()){
                LOGGER.info("ThreadPool Name {}", entry.getKey());
                ThreadPoolMonitorSet.INSTANCE.register(entry.getKey(),entry.getValue());
            }
        }catch (Exception e){
            LOGGER.error("call ThreadPoolMonitorSet.INSTANCE.register() error. ",e);
            throw e;
        }

    }


    /**
     * 添加到 thread pool monitor set中 threadPool
     *
     * @return Map<String(thread name),Object(ThreadPool object)>
     */
    protected abstract Map<String,Object> addThreadPool();

}
