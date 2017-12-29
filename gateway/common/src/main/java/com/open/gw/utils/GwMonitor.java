package com.open.gw.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GwMonitor {

    private String appId;

    private String ip;

    private String interfaceName;

    private String methodName;

    private AtomicInteger callTimes = new AtomicInteger(0); //总调用次数

    private AtomicInteger elapse = new AtomicInteger(0); // 总耗时

    private AtomicInteger in = new AtomicInteger(0); //总接收流量大小

    private AtomicInteger out = new AtomicInteger(0); //总发送流量大小

    //调用时间明细
    private final ConcurrentHashMap<Integer, AtomicInteger> statMap = new ConcurrentHashMap<Integer, AtomicInteger>();

    //接收流量大小明细
    private final ConcurrentHashMap<Integer, AtomicInteger> inDataMap = new ConcurrentHashMap<Integer, AtomicInteger>();

    //发送流量大小明细
    private final ConcurrentHashMap<Integer, AtomicInteger> outDataMap = new ConcurrentHashMap<Integer, AtomicInteger>();



}
