package org.learn.open.monitor.web.config;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.learn.open.entity.QueueName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMqConfig {
	@Bean  
    public Queue msgQueue() {  
        return new ActiveMQQueue(QueueName.MSG_QUEUE);
    }
}
