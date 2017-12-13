package org.learn.open.rest.utils;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;

/**  
 * @ClassName: RateLimterUtils  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author jack-yu
 * @date 2016年3月22日 上午11:32:29  
 *    
 */
public class RateLimterUtils {
    //集中令牌桶存储
    private static final ConcurrentMap<String, RateLimiter>  resourceLimiterMap = Maps.newConcurrentMap();
    //并发每应用100/s
    private static final  double PERMITSPERSECOND = 100;
    
    
    public static class RateLimitException extends Exception {

        private static final long serialVersionUID = 1L;

        private String appkey;

        /**
		 * @return the appkey
		 */
		public String getAppkey() {
			return appkey;
		}

		/**
		 * @param appkey the appkey to set
		 */
		public void setAppkey(String appkey) {
			this.appkey = appkey;
		}

		public RateLimitException(String appkey) {
            super(appkey + " should not be visited so frequently");
            this.appkey = appkey;
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
    
	   public static void updateAppQps(String appkey, double qps) {
	        RateLimiter limiter = resourceLimiterMap.get(appkey);
	        if (limiter == null) {
	            limiter = RateLimiter.create(qps);
	            RateLimiter putByOtherThread
	                    = resourceLimiterMap.putIfAbsent(appkey, limiter);
	            if (putByOtherThread != null) {
	                limiter = putByOtherThread;
	            }
	        }
	        limiter.setRate(qps);
	    }
	   

	   public static  RateLimiter insertAppQps(String appkey) {
	            RateLimiter    limiter = RateLimiter.create(PERMITSPERSECOND);
	            RateLimiter putByOtherThread
	                    = resourceLimiterMap.putIfAbsent(appkey, limiter);
	            if (putByOtherThread != null) {
	                limiter = putByOtherThread;
	            }
	            return limiter;
	    }
	   
	    public static void removeApp(String appkey) {
	        resourceLimiterMap.remove(appkey);
	    }
	    
	    
	    public  static  void enter(String appkey) throws  RateLimitException {
	    	 RateLimiter limiter = resourceLimiterMap.get(appkey);
	        if (limiter == null) {
	        	insertAppQps(appkey);
	            return;
	        	}
	        if (!limiter.tryAcquire()) {
	        	throw new RateLimitException(appkey);
	        }
	    }

	    public static void exit(String resource) {
	        //do nothing when use RateLimiter
	    }
	    public static void main(String[] args) {
	    	for(int j=0;j<100;j++){
	    		new Thread(new Runnable() {
					@Override
					public void run() {
				    	try {
				    		for(int i=0;i<1000;i++){
								enter("abc");
				    		}
						} catch (RateLimitException e) {
						}
						
					}
				}).start();
		
	    	}
	    	
		}
}
