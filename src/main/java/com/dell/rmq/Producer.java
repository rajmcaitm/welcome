package com.dell.rmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class Producer {

    @Value("${rabbitmq.queues}")
    private String queuesConfig;

    @Value("${rabbitmq.producer.message-interval-ms:2000}")
    private long messageIntervalMs;

    @Value("${rabbitmq.producer.publisher-confirm-timeout-ms:5000}")
    private long publisherConfirmTimeoutMs;

    @Value("${rabbitmq.producer.enable-publisher-confirms:true}")
    private boolean enablePublisherConfirms;

    @Value("${rabbitmq.producer.message-template:Hello RMQ how are you : {messageId}}")
    private String messageTemplate;

    @Value("${rabbitmq.queue.durable:true}")
    private boolean queueDurable;

    @Value("${rabbitmq.queue.exclusive:false}")
    private boolean queueExclusive;

    @Value("${rabbitmq.queue.auto-delete:false}")
    private boolean queueAutoDelete;

    @Autowired
    private com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory;

    private Connection connection;
    private Channel channel;

    private volatile boolean running = true;

    private final AtomicLong counter = new AtomicLong(1);

    @PostConstruct
    public void initialize() {

        try {

            log.info("Initializing RabbitMQ Producer...");
            // Create connection
            connection = rabbitConnectionFactory.newConnection();
            // Create channel
            channel = connection.createChannel();
            // Enable publisher confirm mode
            if (enablePublisherConfirms) {
                channel.confirmSelect();
                log.info("Publisher confirms enabled");
            }
            // Get queue list from properties
            List<String> queueNames = Arrays.asList(queuesConfig.split(","));
            log.info("Configured queues: {}", queueNames);
            // Declare queues
            for (String queueName : queueNames) {
                declareQueue(queueName.trim());
            }

            log.info("RabbitMQ Producer initialized successfully");

            // Start continuous producer thread
            startPublishingMessageToRMQ();

        } catch (Exception ex) {

            log.error("Failed to initialize RabbitMQ Producer", ex);

            throw new RuntimeException("RabbitMQ Producer startup failed", ex);
        }
    }

    private void startPublishingMessageToRMQ() {
        Thread producerThread = new Thread(() -> {
            while (running) {
                try {
                    pushingMessageToRMQ();
                    // Delay between messages
                    Thread.sleep(messageIntervalMs);
                } catch (Exception ex) {
                    log.error("Error while publishing messages", ex);
                }
            }

        });

        producerThread.setName("rabbitmq-producer-thread");

        producerThread.setDaemon(true);

        producerThread.start();

        log.info("Continuous Producer thread started with interval: {}ms", messageIntervalMs);
    }

    private void declareQueue(String queueName) throws IOException {

        channel.queueDeclare(queueName, queueDurable, queueExclusive, queueAutoDelete, null);
        log.info("Queue declared successfully: {} (durable={}, exclusive={}, auto-delete={})",
                queueName, queueDurable, queueExclusive, queueAutoDelete);
    }

    public void pushingMessageToRMQ() {

        long messageId = counter.getAndIncrement();

        String message = messageTemplate.replace("{messageId}", String.valueOf(messageId));

        try {

            // Get queue list from properties
            List<String> queueNames = Arrays.asList(queuesConfig.split(","));

            for (String queueName : queueNames) {
                publishMessage(queueName.trim(), message);
            }

            log.info("Message published successfully: {}", message);

        } catch (Exception ex) {

            log.error("Failed to publish message: {}", message, ex);
        }
    }

    private void publishMessage(String queueName, String message) throws IOException, InterruptedException, TimeoutException {

        channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));

        // Wait for broker confirmation if enabled
        if (enablePublisherConfirms) {
            channel.waitForConfirmsOrDie(publisherConfirmTimeoutMs);
        }

        log.info("Message sent to queue [{}]: {}", queueName, message);
    }

    @PreDestroy
    public void shutdown() {

        running = false;

        log.info("Shutting down RabbitMQ Producer...");

        try {

            if (channel != null && channel.isOpen()) {

                channel.close();

                log.info("RabbitMQ channel closed");
            }

            if (connection != null && connection.isOpen()) {

                connection.close();

                log.info("RabbitMQ connection closed");
            }

        } catch (IOException | TimeoutException ex) {

            log.error("Error while shutting down RabbitMQ resources", ex);
        }
    }
}