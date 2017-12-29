package com.open.gw.event;

import com.open.gw.entity.GWInterface;

import java.util.List;


public class GWEvent {

    private GWEventType eventType;

    private List<GWInterface> gwInterfaceInfos;

    public GWEventType getEventType() {
        return eventType;
    }

    public void setEventType(GWEventType eventType) {
        this.eventType = eventType;
    }

    public List<GWInterface> getGwInterfaceInfos() {
        return gwInterfaceInfos;
    }

    public void setGwInterfaceInfos(List<GWInterface> gwInterfaceInfos) {
        this.gwInterfaceInfos = gwInterfaceInfos;
    }
}
