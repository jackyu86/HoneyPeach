package org.learn.open.monitor.web.executor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.learn.open.monitor.threadpool.annotation.ThreadPoolMonitorAnnotation;
import org.learn.open.monitor.web.callable.CanceledCallable;
import org.learn.open.utils.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 队列初始化
 * 线程池的初始化

 */
@Service
public class NormalAsyncContext {
    protected Logger log = LoggerFactory.getLogger(NormalAsyncContext.class);
    int queueCapacity=100;
    String poolSize="15-25";
    @ThreadPoolMonitorAnnotation(threadPoolName="normal")
    public static ThreadPoolExecutor executorService;
    public static BlockingQueue<Runnable> queue = null;
    private long keepAliveTimeInSeconds=1000;
    private AsyncListener asyncListener;
    private int asyncTimeoutInSeconds=2;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        String[] poolSizes = poolSize.split("-");
        int corePoolSize = Integer.parseInt(poolSizes[0]);
        int maximumPoolSize = Integer.valueOf(poolSizes[1]);
        queue = new LinkedBlockingDeque<Runnable>(queueCapacity);

        executorService = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTimeInSeconds, TimeUnit.SECONDS,queue);
        executorService.allowCoreThreadTimeOut(true);
        executorService.setRejectedExecutionHandler(
                (r,executor)->{
                    if(r instanceof CanceledCallable){
                        CanceledCallable cc = (CanceledCallable) r;
                        AsyncContext asyncContext = cc.asyncContext;
                        if( asyncContext != null )
                        {
                            try{
                                ServletRequest req = asyncContext.getRequest();
                                String uri = (String) req.getAttribute("uri");
                                Map params = (Map) req.getAttribute("params");
                                log.error("async request rejected, uri : {},params:{}",uri, JsonTools.getJsonFromObject(params));
                            }catch(Exception e){}
                            try{
                                HttpServletResponse rsp = (HttpServletResponse) asyncContext.getResponse();
                                rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            }finally {
                                asyncContext.complete();
                            }
                        }

                    }
                }
        );
        if(asyncListener == null){
            asyncListener = new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent asyncEvent) throws IOException {

                }

                @Override
                public void onTimeout(AsyncEvent asyncEvent) throws IOException {

                }

                @Override
                public void onError(AsyncEvent asyncEvent) throws IOException {

                }

                @Override
                public void onStartAsync(AsyncEvent asyncEvent) throws IOException {

                }
            };
        }
    }

    public void submitFuture(final HttpServletRequest request, final Callable<Object> task) {
        final String uri = request.getRequestURI();
        final Map<String,String[]> params = request.getParameterMap();
        final AsyncContext asyncContext = request.startAsync();
        asyncContext.getRequest().setAttribute("uri",uri);
        asyncContext.getRequest().setAttribute("params",params);
        asyncContext.setTimeout(asyncTimeoutInSeconds * 1000);
        if(asyncContext != null)
        {
            asyncContext.addListener(asyncListener);
        }
        executorService.submit(new CanceledCallable(asyncContext) {
            @Override
            public Object call() throws Exception {
                Object o = task.call();
                if( o == null ){
                    callback(asyncContext, o , uri, params);
                }else if( o instanceof CompletableFuture ){
                    CompletableFuture<Object> future = (CompletableFuture<Object>) o;
                    future.thenAccept(resultObject -> callback(asyncContext,resultObject,uri,params))
                            .exceptionally( e -> {
                                callback(asyncContext,"",uri,params);
                                return null;
                            });
                }else if (o instanceof String){
                    callback(asyncContext,o,uri,params);
                }
                return null;
            }

            private void callback(AsyncContext asyncContext, Object result, String uri, Map<String, String[]> params) {
                HttpServletResponse rsp = (HttpServletResponse) asyncContext.getResponse();
                try{
                    if( result instanceof String ){
                        write(rsp,result);
                    }else{
                        write(rsp,JsonTools.toJson(result));
                    }
                }catch (Throwable e){
                    rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    try{
                        log.error("get info error, uri : {} , params : {}", uri, JsonTools.toJson(params), e);
                    }catch (Exception ex){}
                }finally {
                    asyncContext.complete();
                }
            }

            private void write(HttpServletResponse rsp, Object result) {
            }
        });
    }
}