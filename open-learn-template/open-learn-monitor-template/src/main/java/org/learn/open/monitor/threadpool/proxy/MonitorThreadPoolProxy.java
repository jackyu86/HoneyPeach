package org.learn.open.monitor.threadpool.proxy;


import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    public BlockingQueue getQueues(){
        try{
            Object queue=invokeFunc(threadPoolObject,"getQueue").invoke(threadPoolObject);
            return (BlockingQueue)queue;
        }catch (Exception e){
            LOGGER.error("run getQueue()  error.",e);
            return null;
        }
    }


    public boolean isShutdown(){
        try{
            Object isShutdown=invokeFunc(threadPoolObject,"isShutdown").invoke(threadPoolObject);
            return (Boolean)isShutdown;
        }catch (Exception e){
            LOGGER.error("run isShutdown()  error.",e);
            return false;
        }
    }
    //有序关闭
    public boolean shutdown(){
        try{
            invokeFunc(threadPoolObject,"shutdown").invoke(threadPoolObject);
            return true;
        }catch (Exception e){
            LOGGER.error("run shutdown()  error.",e);
            return false;
        }
    }
    //强制关闭
    public List<Runnable> shutdownNow(){
        try{
            Object tasks = invokeFunc(threadPoolObject,"shutdownNow").invoke(threadPoolObject);

            return (List<Runnable>) tasks;
        }catch (Exception e){
            LOGGER.error("run shutdownNow()  error.",e);
            return null;
        }
    }

    public void removeTask(){
        BlockingQueue bq = getQueues();
        bq.stream().forEach(t->{
            try {
            invokeFunc(threadPoolObject,"remove",t.getClass()).invoke(threadPoolObject);
        } catch (Exception e) {
            LOGGER.error("run remove()  error.",e);
        }});
        try {
            invokeFunc(threadPoolObject,"purge").invoke(threadPoolObject);
        } catch (Exception e) {
            LOGGER.error("run purge()  error.",e);
        }
    }

    //获取活跃线程数
    public int getActiveCount(){
        try{
            Object activeCount=invokeFunc(threadPoolObject,"getActiveCount").invoke(threadPoolObject);
            return (Integer)activeCount;
        }catch (Exception e){
            LOGGER.error("run getActiveCount()  error.",e);
            return -1;
        }
    }
    //已执行任务的总数
    public long getTaskCount(){
        try{
            Object taskCount=invokeFunc(threadPoolObject,"getTaskCount").invoke(threadPoolObject);
            return (Long)taskCount;
        }catch (Exception e){
            LOGGER.error("run getTaskCount()  error.",e);
            return -1l;
        }
    }

    public ThreadPoolInformation buildThreadPoolInformation(){
        ThreadPoolInformation threadPoolInformation=new ThreadPoolInformation();
        threadPoolInformation.setCoreThreadNum(getCoreThreadNum());
        threadPoolInformation.setMaxThreadNum(getMaxThreadNum());
        threadPoolInformation.setQueueLength(getQueueLength());
        threadPoolInformation.setThreadNum(getThreadNum());
        threadPoolInformation.setActiveCount(getActiveCount());
        threadPoolInformation.setShutdown(isShutdown());
        threadPoolInformation.setTaskCount(getTaskCount());
        threadPoolInformation.setQueues(getQueues());
        return threadPoolInformation;
    }



    private Method invokeFunc(Object obj,String funcName,Class<?>... parameterTypes) throws NoSuchMethodException{
            return obj.getClass().getMethod(funcName,parameterTypes);
    }
}
