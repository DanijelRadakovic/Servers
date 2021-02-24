package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.producer.LogProducer;
import com.example.demo.conf.RabbitMQConfiguration;

@RunWith(SpringRunner.class)
@DataJpaTest
class DemoApplicationTests {

    @MockBean
    private LogProducer producer;

    @MockBean
    private RabbitMQConfiguration rabbitmqConfig;

    
    @Test
    void contextLoads() {
    }

}
