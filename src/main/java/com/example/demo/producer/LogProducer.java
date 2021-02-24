package com.example.demo.producer;

import com.example.demo.conf.RabbitMQConfiguration;
import com.example.demo.domain.Log;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class LogProducer {

    private final ConnectionFactory factory;

    public LogProducer(ConnectionFactory factory) {
        this.factory = factory;
    }

    public void send(Log log) throws IOException, TimeoutException {
        String queueName = RabbitMQConfiguration.QUEUE_NAME;

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
        channel.basicPublish("", queueName, null, log.toString().getBytes());
        channel.close();
        connection.close();
    }
}
