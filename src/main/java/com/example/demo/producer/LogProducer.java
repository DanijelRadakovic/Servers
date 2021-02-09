package com.example.demo.producer;

import com.example.demo.conf.RabbitMQConfiguration;
import com.example.demo.domain.Log;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogProducer {

    private RabbitTemplate rabbitTemplate;

    public LogProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(Log log) {
        rabbitTemplate.convertAndSend("logs", log.toString());
    }
}
