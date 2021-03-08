package com.example.demo.unit;

import com.example.demo.conf.RabbitMQConfiguration;
import com.example.demo.producer.LogProducer;
import com.example.demo.repository.ServerRepository;
import com.example.demo.service.ServerService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServerServiceTest {

    @MockBean
    private LogProducer producer;

    @MockBean
    private RabbitMQConfiguration rabbitmqConfig;

    @MockBean
    private ServerRepository serverRepository;

    @Autowired
    private ServerService serverService;

    @Before
    public void setUp() {
        Mockito.when(serverRepository.findAll()).thenReturn(new ArrayList<>());
    }

    @Test
    void getAllTest() {
        assertThat(serverService.getAll()).isEmpty();
    }
}