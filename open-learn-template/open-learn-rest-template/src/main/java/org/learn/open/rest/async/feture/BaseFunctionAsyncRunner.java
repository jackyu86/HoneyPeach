package org.learn.open.rest.async.feture;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

/**  
 * @ClassName: BaseAsyncRunner  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author jack-yu
 * @date 2016年3月11日 下午6:07:35  
 *    
 */
public  abstract class BaseFunctionAsyncRunner<T,V> implements Callable<V>{

	T t =null;
	
	public BaseFunctionAsyncRunner(T t) {
		this.t =t;
	}
	
    public abstract V  call() throws Exception;
	
	 private void awaitCompletion(List<Future<?>> futures,ExecutorService executor) {
	        futures.forEach((future) -> {
	            try {
	                future.get();
	            } catch (InterruptedException | ExecutionException e) {
	                e.printStackTrace();
	            }
	        });
	        executor.shutdown();
	    }
	 
	 

}
