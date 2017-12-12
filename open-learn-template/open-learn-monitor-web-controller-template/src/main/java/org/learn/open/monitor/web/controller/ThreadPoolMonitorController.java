package org.learn.open.monitor.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.learn.open.api.monitor.ThreadPoolMonitorService;
import org.learn.open.monitor.threadpool.model.ThreadPoolInformation;
import org.learn.open.monitor.web.executor.NormalAsyncContext;
import org.learn.open.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: jack-yu
 * @Description:  线程池监控
 * //TODO 添加统一返回结果  
 */
@Controller
@RequestMapping(value = "/monitor")
public class ThreadPoolMonitorController {

    @Autowired
    protected NormalAsyncContext normalAsyncContext;
    @Autowired
    private ThreadPoolMonitorService threadPoolMonitorService;

    @RequestMapping("/getThreadPoolInformation")
    @ResponseBody
    public ThreadPoolInformation getThreadPoolInformation(@PathParam("threadPoolName") String threadPoolName){
        System.err.println("in getThreadPoolInformation. threadPoolName= "+threadPoolName);
        ThreadPoolInformation information= threadPoolMonitorService.getThreadPoolRunTimeInformation(threadPoolName);
        return information;
    }

    @RequestMapping("/getThreadPoolInformation")
    @ResponseBody
    public List<ThreadPoolInformation> getAllThreadPoolInformation(){
        System.err.println("in getAllThreadPoolInformation.");

        return threadPoolMonitorService.getAllThreadPoolRunTimeInformation();
    }

    @RequestMapping("/getThreadPoolInformation")
    @ResponseBody
    public Boolean releaseThreadPoolInformation(@PathParam("threadPoolName") String threadPoolName){
        System.err.println("in getThreadPoolInformation. threadPoolName= "+threadPoolName);
        return threadPoolMonitorService.releaseThreadPool(threadPoolName);
    }

}
