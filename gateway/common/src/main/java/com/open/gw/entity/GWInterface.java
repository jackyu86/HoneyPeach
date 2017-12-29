package com.open.gw.entity;

import java.util.Date;

public class GWInterface {

    private int id;

    private String interfaceName;

    private String alias;

    private String method;

    private Date createTime;

    //0 关闭 1 开启
    private int switchStatus = 1;

    public GWInterface(String interfaceName, String alias, String method) {
        this.interfaceName = interfaceName;
        this.alias = alias;
        this.method = method;
    }

    public GWInterface() {
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(int switchStatus) {
        this.switchStatus = switchStatus;
    }
}
