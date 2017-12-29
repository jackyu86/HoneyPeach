package com.open.gw.event;

public enum GWEventType {

    /**
     * 开启网关
     */
    OPENGW,

    /**
     * 关闭网关
     */
    CLOSEGW,

    /**
     * 检查实例是否存活
     */
    CHECK,

    /**
     * gw server 状态
     */
    STATUS
}
