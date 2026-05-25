package com.dell.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class VaultConfig {

    @Value("${rabbit.username}")
    private String username;

    @Value("${rabbit.password}")
    private String password;

    @Value("${rabbit.host}")
    private String host;

    @Value("${rabbit.port}")
    private int port;

    @Value("${rabbit.vhost}")
    private String virtualHost;


    @Bean
    public com.rabbitmq.client.ConnectionFactory connectionFactory() throws Exception {

        try {
            com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setVirtualHost(virtualHost);
            factory.useSslProtocol();
            log.info("RabbitMQ SSL Connection Configured Successfully");
            log.info("RabbitMQ Host: {}, Port: {}, VHost: {}", host, port, virtualHost);
            return factory;
        } catch (Exception e) {
            log.error("Failed to create RabbitMQ connection factory", e);
            throw new IllegalStateException("RabbitMQ connection initialization failed", e);
        }
    }

}