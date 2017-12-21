package com.open.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

public class HttpUtils {
    private final static Log logger = LogFactory.getLog(HttpUtils.class);

    private PoolingClientConnectionManager cm;

    private String url;

    private String refer;

    private String cookie;

    public void init() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        cm = new PoolingClientConnectionManager(schemeRegistry);
        cm.setMaxTotal(500);
        cm.setDefaultMaxPerRoute(300);
    }


    private void destroy() {
        cm.shutdown();
    }

    public String getValueFromRemote(String param) {
        HttpClient httpClient = new DefaultHttpClient(cm);
        //连接超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        //降低延迟，但是增加网络消耗
        //httpClient.getParams().setParameter(CoreConnectionPNames.TCP_NODELAY, true);
        //保持socket状态
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_KEEPALIVE, true);
        //缓冲流大小
        httpClient.getParams().setParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 2 * 1024);
        //socket超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        //socket延迟关闭时间，-1为jre默认设置
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_LINGER, -1);
//        httpClient.getParams().setParameter("Referer", "http://ops.jd.com/rms/search");
        HttpPost httpGet = new HttpPost(url + param);

//        System.out.println("GET  request = "+(url+param));

        if(StringUtils.isNotBlank(refer)){
            httpGet.setHeader("Refer", refer);
        }
        if(StringUtils.isNotBlank(cookie)){
            httpGet.setHeader("Cookie", cookie);
        }
//        httpGet.setHeader("Refer", "http://ca.jd.com");
//        httpGet.setHeader("Cookie","PHPSESSID=i3ap42vtckspuqb6d4dbeb01t0;erp1.jd.com=86DB9E5A0A6DA9C805F600D2EDFC02423D1D47D9E05A8FB7F2F859B33512D17164A9AAEDF929801CF005D8FC94E1D728815223DA18A16A27B3FD658A5BB31D35587CDDD1BD1EA14EE0F4DBCE1A7488CF");

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = null;
        try {
            responseBody = httpClient.execute(httpGet, responseHandler);
//            System.out.println("responseBody = "+responseBody);
        } catch (IOException e) {
            logger.error("--> 调用httpclient获取info时出错!param=" + url + param + " ;", e);
            httpGet.abort();
            return null;
        }
        //删除返回结果前后的小括号
        return responseBody;
    }


    public void postValueFromRemote() throws UnsupportedEncodingException {
        HttpClient httpClient = new DefaultHttpClient(cm);
        //连接超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        //降低延迟，但是增加网络消耗
        //httpClient.getParams().setParameter(CoreConnectionPNames.TCP_NODELAY, true);
        //保持socket状态
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_KEEPALIVE, true);
        //缓冲流大小
        httpClient.getParams().setParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 2 * 1024);
        //socket超时
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        //socket延迟关闭时间，-1为jre默认设置
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_LINGER, -1);
//        httpClient.getParams().setParameter("Referer", "http://ops.jd.com/rms/search");

        HttpPost httpost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        //提交两个参数及值
        nvps.add(new BasicNameValuePair("task.type", "7"));
        nvps.add(new BasicNameValuePair("task.optype", "2"));
        nvps.add(new BasicNameValuePair("task.status", "0"));
        nvps.add(new BasicNameValuePair("task.source", "2"));
        nvps.add(new BasicNameValuePair("task.business", "480068"));
        //设置表单提交编码为UTF-8
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        try {
            HttpResponse response = httpClient.execute(httpost);
            HttpEntity entity = response.getEntity();
            System.out.println("code: " + response.getStatusLine());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
    	HttpUtils util = new HttpUtils();
        util.setCookie("\"ceshi3.com=44D1EAD78797F4376F818F267F6785334352DC85E86FB978AAEF3B40F273ABF46AC2DE5DDF5203CCD5D92EE416CD2F8A3DFDD8B0C155F6BAB4870A8FD4EF20455409FF7C03FA633A07A5E20BA91DC019762FEFE4BA335F00551FC1E2ADB7BD79C92C3FE65F3288B7319F81CD403A1193C52C7145F26876C296DC93C41EC1D4EB290888779229FE98CF98717BEFC0575B; __jda=97936925.1723542384.1423120034.1423993868.1425016338.6; __jdv=97936925|direct|-|none|-; erp1.360buy.com=6548A7BE4C74F6E54A3C888380429CF6F029FD77A3958ADC283233F796894AE7C79639FED23006BB35E436F65A671FB3C6558CE6BEFEDC25AC3930686B7F1D870BE86E4FD98E37974C187B62FECAA8B6; JSESSIONID=37C1B4409B142F8885D05BDD858A22DC.s1");
        util.setRefer("http://exadeliver.360buy.com/login.html");
        util.setUrl("http://zkconfig.360buy.com/client/securityIndex.action");

//        util.postValueFromRemote();

        BufferedReader reader = new BufferedReader(new FileReader(new File("D:\\c.txt")));
        String skuId;

        do {
            skuId = reader.readLine();
            if (null != skuId) {
                String url = "?task.type=1&task.optype=2&task.status=1&task.source=2&task.business=" + skuId;
                util.getValueFromRemote(url);


//                String url2 = "?task.type=5&task.optype=2&task.status=1&task.source=2&task.business=" + skuId;
//                util.getValueFromRemote(url2);

//                Thread.sleep(5L);
            }
        } while (skuId != null);
        System.out.println("over");
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}

