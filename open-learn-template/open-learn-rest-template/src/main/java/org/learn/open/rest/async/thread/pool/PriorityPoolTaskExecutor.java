package org.learn.open.rest.async.thread.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;

/**  
 * @ClassName: PriorityPoolTaskExecutor  
 * @Description: 优先级队列线程池扩展
 * @author jack-yu
 * @date 2016年3月11日 下午12:51:07  
 */
public class PriorityPoolTaskExecutor extends ExecutorConfigurationSupport implements TaskExecutor
{
    private int corePoolSize = 1;
     
    private int maxPoolSize = Integer.MAX_VALUE;
     
    private int keepAliveSeconds = 60;
     
    private int queueCapacity = Integer.MAX_VALUE;
     
    private boolean allowCoreThreadTimeOut = false;
     
    private ThreadPoolExecutor threadPoolExecutor;
     
    private byte[] lock = new byte[0];
     
    private static final long serialVersionUID = -3315691715909831821L;
     
    @Override
    public void execute(Runnable task)
    {
        Executor executor = getThreadPoolExecutor();
        try
        {
            executor.execute(task);
        }
        catch (RejectedExecutionException ex)
        {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
         
    }
     
    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory,
        RejectedExecutionHandler rejectedExecutionHandler)
    {
        BlockingQueue<Runnable> queue = createQueue(this.queueCapacity);
        ThreadPoolExecutor executor =
            new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS, queue,
                threadFactory, rejectedExecutionHandler);
        if (this.allowCoreThreadTimeOut)
        {
            executor.allowCoreThreadTimeOut(true);
        }
         
        this.threadPoolExecutor = executor;
        return executor;
    }
     
    protected BlockingQueue<Runnable> createQueue(int queueCapacity)
    {
        if (queueCapacity > 0)
        {
            return new PriorityBlockingQueue<Runnable>(queueCapacity);
        }
        else
        {
            return new SynchronousQueue<Runnable>();
        }
    }
     
    /**
     * @return 返回 corePoolSize
     */
    public int getCorePoolSize()
    {
        synchronized (lock)
        {
            return corePoolSize;
        }
    }
     
    /**
     * @return 返回 maxPoolSize
     */
    public int getMaxPoolSize()
    {
        synchronized (lock)
        {
             
            return maxPoolSize;
        }
    }
     
    /**
     * @return 返回 keepAliveSeconds
     */
    public int getKeepAliveSeconds()
    {
        synchronized (lock)
        {
             
            return keepAliveSeconds;
        }
    }
     
    /**
     * @return 返回 queueCapacity
     */
    public int getQueueCapacity()
    {
        synchronized (lock)
        {
             
            return queueCapacity;
        }
    }
     
    /**
     * @return 返回 allowCoreThreadTimeOut
     */
    public boolean isAllowCoreThreadTimeOut()
    {
        synchronized (lock)
        {
             
            return allowCoreThreadTimeOut;
        }
    }
     
    /**
     * @return 返回 threadPoolExecutor
     */
    public ThreadPoolExecutor getThreadPoolExecutor()
    {
        synchronized (lock)
        {
             
            return threadPoolExecutor;
        }
    }
     
    /**
     * @param 对corePoolSize进行赋值
     */
    public void setCorePoolSize(int corePoolSize)
    {
        synchronized (lock)
        {
             
            this.corePoolSize = corePoolSize;
            if (null != this.threadPoolExecutor)
            {
                this.threadPoolExecutor.setCorePoolSize(corePoolSize);
            }
        }
    }
     
    /**
     * @param 对maxPoolSize进行赋值
     */
    public void setMaxPoolSize(int maxPoolSize)
    {
        synchronized (lock)
        {
             
            this.maxPoolSize = maxPoolSize;
            if (null != this.threadPoolExecutor)
            {
                this.threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
            }
        }
    }
     
    /**
     * @param 对keepAliveSeconds进行赋值
     */
    public void setKeepAliveSeconds(int keepAliveSeconds)
    {
        synchronized (lock)
        {
             
            this.keepAliveSeconds = keepAliveSeconds;
            if (this.threadPoolExecutor != null)
            {
                this.threadPoolExecutor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
            }
        }
    }
     
    /**
     * @param 对queueCapacity进行赋值
     */
    public void setQueueCapacity(int queueCapacity)
    {
        this.queueCapacity = queueCapacity;
    }
     
    /**
     * @param 对allowCoreThreadTimeOut进行赋值
     */
    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut)
    {
        synchronized (lock)
        {
             
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
            if (this.threadPoolExecutor != null)
            {
                this.threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
            }
        }
    }
     
    /**
     * @param 对threadPoolExecutor进行赋值
     */
    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor)
    {
        this.threadPoolExecutor = threadPoolExecutor;
    }
     
}
