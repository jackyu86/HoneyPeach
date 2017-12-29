package com.open.gw;

import com.jd.jsf.gd.transport.Callback;
import com.open.gw.event.GWEvent;


/**
 * Title: <br>
 * <p/>
 * Description: <br>
 * <p/>
 *
 *
 *
 * @since 2015/03/27 16:28
 */
public interface GWServerRegistryService {

    /**
     * GWServer 注册到GW Console
     *
     * @param ip
     * @param pid
     * @param port
     * @param cb
     * @return
     */
    boolean register(String ip,int pid,int port,Callback<GWEvent,String> cb);


    boolean unRegister(String ip,int pid);


}
