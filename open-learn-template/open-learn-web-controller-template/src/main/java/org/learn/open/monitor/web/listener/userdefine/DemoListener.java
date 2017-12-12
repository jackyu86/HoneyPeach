package org.learn.open.monitor.web.listener.userdefine;

import org.learn.open.monitor.web.events.DemoEvent;
import org.springframework.context.ApplicationListener;

public class DemoListener implements ApplicationListener<DemoEvent> {

	@Override
	public void onApplicationEvent(DemoEvent event) {
		String msg = event.getMsg();
		
		System.out.println("我接受到了Demo-Publish发布的消息"+msg);
	}

}
