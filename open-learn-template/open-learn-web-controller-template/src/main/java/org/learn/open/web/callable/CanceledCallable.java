package org.learn.open.web.callable;

import javax.servlet.AsyncContext;
import java.util.concurrent.Callable;

public abstract class CanceledCallable implements Callable {
    public AsyncContext asyncContext;

    public CanceledCallable(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }
}