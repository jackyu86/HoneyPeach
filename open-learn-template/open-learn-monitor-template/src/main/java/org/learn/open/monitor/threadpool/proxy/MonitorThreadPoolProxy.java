package org.learn.open.monitor.threadpool.proxy;


import java.lang.reflect.Method;

import org.learn.open.monitor.threadpool.model.ThreadPoolInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @Author: jack-yu
 * @Description:  线程池代理创建获取维度信息
 */
public class MonitorThreadPoolProxy {

    private static final Logger LOGGER= LoggerFactory.getLogger(MonitorThreadPoolProxy.class);

    private Object threadPoolObject;

    public MonitorThreadPoolProxy(Object threadPoolObject){
        this.threadPoolObject=threadPoolObject;
    }

    public int getThreadNum(){
        try{
            Object threadNum=invokeFunc(threadPoolObject,"getPoolSize").invoke(threadPoolObject);
            return (Integer)threadNum;
        }catch (Exception e){
            LOGGER.error("run getPoolSize() error.",e);
            return -1;
        }
    }

    public int getCoreThreadNum(){
        try{
            Object coreThreadNum=invokeFunc(threadPoolObject,"getCorePoolSize").invoke(threadPoolObject);
            return (Integer)coreThreadNum;
        }catch (Exception e){
            LOGGER.error("run getCorePoolSize() error.",e);
            return -1;
        }
    }

    public int getMaxThreadNum(){
        try{
            Object maxThreadNum = invokeFunc(threadPoolObject,"getMaximumPoolSize").invoke(threadPoolObject);
            return (Integer)maxThreadNum;
        }catch (Exception e){
            LOGGER.error("run getMaximumPoolSize() error.",e);
            return -1;
        }
    }

    public int getQueueLength(){
        try{
            Object queue=invokeFunc(threadPoolObject,"getQueue").invoke(threadPoolObject);
            Object queueLength=invokeFunc(queue,"size").invoke(queue);
            return (Integer)queueLength;
        }catch (Exception e){
            LOGGER.error("run getQueue() or queue.size() error.",e);
            return -1;
        }
    }

    public ThreadPoolInformation buildThreadPoolInformation(){
        ThreadPoolInformation threadPoolInformation=new ThreadPoolInformation();
        threadPoolInformation.setCoreThreadNum(getCoreThreadNum());
        threadPoolInformation.setMaxThreadNum(getMaxThreadNum());
        threadPoolInformation.setQueueLength(getQueueLength());
        threadPoolInformation.setThreadNum(getThreadNum());
        return threadPoolInformation;
    }



    private Method invokeFunc(Object obj,String funcName,Class<?>... parameterTypes) throws NoSuchMethodException{
            return obj.getClass().getMethod(funcName,parameterTypes);
    }
}
