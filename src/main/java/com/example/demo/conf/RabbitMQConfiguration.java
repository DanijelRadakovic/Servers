package com.example.demo.conf;


import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class RabbitMQConfiguration {

    public static final String KEYSTORE_PROVIDER = "SunX509";
    public static final String QUEUE_NAME = "logs";

    @Value("${TLS_ENABLED:false}")
    private boolean tlsEnabled;

    /**
     * TLS version.
     */
    @Value("${server.ssl.algorithm}")
    private String algorithm;

    /**
     * Application keystore path.
     */
    @Value("${server.ssl.key-store}")
    private String keystore;

    /**
     * Application keystore type.
     */
    @Value("${server.ssl.key-store-type}")
    private String keystoreType;

    /**
     * Application keystore password.
     */
    @Value("${server.ssl.key-store-password}")
    private String keystorePassword;

    /**
     * Keystore alias for application client credential.
     */
    @Value("${server.ssl.key-alias}")
    private String applicationKeyAlias;

    /**
     * Application truststore path.
     */
    @Value("${server.ssl.trust-store}")
    private String truststore;

    /**
     * Application truststore type.
     */
    @Value("${server.ssl.trust-store-type}")
    private String truststoreType;

    /**
     * Application truststore password.
     */
    @Value("${server.ssl.trust-store-password}")
    private String truststorePassword;

    @Value("${RMQ_HOST:localhost}")
    private String host;

    @Value("${RMQ_PORT:5671}")
    private String port;


    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public ConnectionFactory connectionFactory() {

        try {
            ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
            factory.setHost(host);
            factory.setPort(Integer.parseInt(port));

            if (tlsEnabled) {
                KeyStore keyStore = KeyStore.getInstance(keystoreType);
                keyStore.load(new FileInputStream(keystore), keystorePassword.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEYSTORE_PROVIDER);
                kmf.init(keyStore, keystorePassword.toCharArray());

                KeyStore trustStore = KeyStore.getInstance(truststoreType);
                trustStore.load(new FileInputStream(truststore), truststorePassword.toCharArray());

                TrustManagerFactory tmf = TrustManagerFactory.getInstance(KEYSTORE_PROVIDER);
                tmf.init(trustStore);

                SSLContext sslcontext = SSLContext.getInstance(algorithm);
                sslcontext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                factory.useSslProtocol(sslcontext);
            }

            return factory;

        } catch (Exception e) {
            throw new IllegalStateException("Error while configuring rabbitmq template", e);
        }
    }
}
