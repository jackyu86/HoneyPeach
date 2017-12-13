package org.learn.open.monitor.web.controller.monitor;

import java.util.List;
import javax.websocket.server.PathParam;

import org.learn.open.api.monitor.ThreadPoolMonitorService;
import org.learn.open.entity.Result;
import org.learn.open.monitor.web.executor.NormalAsyncContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: jack-yu
 * @Description: 线程池监控
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
    public Result getThreadPoolInformation(@PathParam("threadPoolName") String threadPoolName) {
        Result result = new Result(true);
        result.setObj(threadPoolMonitorService.getThreadPoolRunTimeInformation(threadPoolName));
        return result;
    }

    @RequestMapping("/getThreadPoolInformation")
    @ResponseBody
    public Result getAllThreadPoolInformation() {
        Result result = new Result(true);
        result.setObjs(threadPoolMonitorService.getAllThreadPoolRunTimeInformation());
        return result;
    }

    @RequestMapping("/releaseThreadPoolInformation")
    @ResponseBody
    public Result releaseThreadPoolInformation(@PathParam("threadPoolName") String threadPoolName) {
          Result result = new Result(true);
          result.setObj(threadPoolMonitorService.releaseThreadPool(threadPoolName));
        return result;
    }

}
