package com.example.demo.conf;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String QUEUE_NAME = "logs";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }

}
