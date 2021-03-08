package com.example.demo.integration;

import com.example.demo.conf.RabbitMQConfiguration;
import com.example.demo.producer.LogProducer;
import com.example.demo.service.ServerService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServerServiceIT {

    @MockBean
    private LogProducer producer;

    @MockBean
    private RabbitMQConfiguration rabbitmqConfig;

    @Autowired
    private ServerService serverService;

    @Test
    void getAll() {
        assertThat(serverService.getAll()).isEmpty();
    }
}
