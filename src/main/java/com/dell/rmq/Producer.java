package com.dell.rmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class Producer {

    private static final String QUEUE_NAME1 = "my_queue";
    private static final String QUEUE_NAME2 = "rmq_test";
    private static final String QUEUE_NAME3 = "samrat_dharm";

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
            channel.confirmSelect();
            // Declare queues
            declareQueue(QUEUE_NAME1);
            declareQueue(QUEUE_NAME2);
            declareQueue(QUEUE_NAME3);

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
                    Thread.sleep(2000);
                } catch (Exception ex) {
                    log.error("Error while publishing messages", ex);
                }
            }

        });

        producerThread.setName("rabbitmq-producer-thread");

        producerThread.setDaemon(true);

        producerThread.start();

        log.info("Continuous Producer thread started");
    }

    private void declareQueue(String queueName) throws IOException {

        channel.queueDeclare(queueName, true, false, false, null);

        log.info("Queue declared successfully: {}", queueName);
    }

    public void pushingMessageToRMQ() {

        long messageId = counter.getAndIncrement();

        String message = "Hello RMQ how are you : " + messageId;

        try {

            publishMessage(QUEUE_NAME1, message);

            publishMessage(QUEUE_NAME2, message);

            publishMessage(QUEUE_NAME3, message);

            log.info("Message published successfully: {}", message);

        } catch (Exception ex) {

            log.error("Failed to publish message: {}", message, ex);
        }
    }

    private void publishMessage(String queueName, String message) throws IOException, InterruptedException, TimeoutException {

        channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));

        // Wait for broker confirmation
        channel.waitForConfirmsOrDie(5000);

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