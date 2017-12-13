package org.learn.open.rest.client;

import java.net.URLEncoder;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;


/**
 * This class is used for ...   
 * @author jack  
 * @version   
 *       1.0, 2015年5月12日 下午2:12:26
 */
public class Jaxrs2Client {
    protected final static Logger LOGGER = Logger.getLogger(Jaxrs2Client.class);
    protected final static String BASE_URI = "http://localhost:8094/cso/";
    protected final static String SANDBOX_DATA_URI = "http://222.73.176.24:8899";
    protected final static String DATA_URI = "http://192.168.10.42:8899";
    protected final static String DEMO_DATA_URI = "http://www.baidu.com";
	protected final static Clock CLOCK = Clock.systemDefaultZone();
    protected Client client;

    protected void checkConfig() {
        final Configuration newConfiguration = client.getConfiguration();
        final Map<String, Object> properties = newConfiguration.getProperties();
        final Iterator<Entry<String, Object>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, Object> next = iterator.next();
            Jaxrs2Client.LOGGER.debug(next.getKey() + ":" + next.getValue());
        }
    }

/*    public void test() {
        final WebTarget webTarget = client.target(Jaxrs2Client.BASE_URI);
        final WebTarget pathTarget = webTarget.path("books");
        final WebTarget pathTarget2 = pathTarget.path("book");
        final WebTarget queryTarget = pathTarget2.queryParam("bookId", "1");
        Jaxrs2Client.LOGGER.debug(queryTarget.getUri());
        final Invocation.Builder invocationBuilder = queryTarget.request(MediaType.APPLICATION_XML);

    }*/
    //上端控制非空
    public Response dataRequest(String requestType,boolean isSandBox,Map<String,String[]> map){
    	LocalDateTime begingDateTime =	LocalDateTime.now(CLOCK);
    	LOGGER.info("jimu  data request beging :"+begingDateTime);
    	  final WebTarget webTarget;
    	if(isSandBox){
    			webTarget = client.target(Jaxrs2Client.SANDBOX_DATA_URI);
    	}else{
    		webTarget = client.target(Jaxrs2Client.DATA_URI);
    	}
         WebTarget pathTarget = webTarget.path("api");
         pathTarget = pathTarget.queryParam("server", requestType);
        
        for(Entry<String, String[]> entry : map.entrySet()) {
            for (String val : entry.getValue()) {
            		pathTarget = pathTarget.queryParam(URLEncoder.encode(entry.getKey()), URLEncoder.encode(val));
            }
          }
        Invocation.Builder invocationBuilder = pathTarget.request(MediaType.APPLICATION_JSON);
        Response response =  invocationBuilder.get();
        LocalDateTime endDateTime =	LocalDateTime.now(CLOCK);
        LOGGER.info("jimu  data request ending :"+endDateTime);
        LOGGER.info("result between millis :"+ ChronoUnit.MILLIS.between(begingDateTime, endDateTime));
    	return response;
    }
    
    
    public Response dataRequest_abc(){
    	  final WebTarget webTarget;
    		webTarget = client.target(Jaxrs2Client.DEMO_DATA_URI);
        Response response =  webTarget.request().get();
    	return response;
    }
    
}
