package org.learn.open.monitor.web.executor;

import java.util.concurrent.Callable;
import javax.servlet.AsyncContext;

public abstract class CanceledCallable implements Callable {
    public AsyncContext asyncContext;

    public CanceledCallable(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }
}