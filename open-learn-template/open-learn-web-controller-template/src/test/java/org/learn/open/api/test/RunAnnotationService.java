package org.learn.open.api.test;

import org.learn.open.web.annotation.DemoService;
import org.learn.open.web.annotation.DemoServiceConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RunAnnotationService {
	
	public static void main(String[] args){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DemoServiceConfig.class);
		DemoService ds = ctx.getBean(DemoService.class);
		ds.outputResult();
	}
}
