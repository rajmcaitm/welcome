package com.dell.rmq;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class Consumer {

    private static final String QUEUE_NAME1 = "my_queue";
    private static final String QUEUE_NAME2 = "rmq_test";
    private static final String QUEUE_NAME3 = "samrat_dharm";

    @Autowired
    private com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory;

    private Connection connection;
    private Channel channel;

    @PostConstruct
    public void startConsumer() {

        try {
            log.info("Initializing RabbitMQ Consumer...");

            // Create connection
            connection = rabbitConnectionFactory.newConnection();

            // Create channel
            channel = connection.createChannel();

            // Configure QoS
            channel.basicQos(1);

            // Declare queues
            declareQueue(QUEUE_NAME1);
            declareQueue(QUEUE_NAME2);
            declareQueue(QUEUE_NAME3);

            // Create consumer callback
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                long deliveryTag = delivery.getEnvelope().getDeliveryTag();

                log.info("Message received from queue [{}] : {}", delivery.getEnvelope().getRoutingKey(), message);

                try {

                    // Business logic processing
                    processMessage(message);

                    // Acknowledge message
                    channel.basicAck(deliveryTag, false);

                    log.info("Message acknowledged successfully. DeliveryTag={}", deliveryTag);

                } catch (Exception ex) {

                    log.error("Error processing message. DeliveryTag={}, Error={}", deliveryTag, ex.getMessage(), ex);

                    // Reject message without requeue
                    channel.basicNack(deliveryTag, false, false);
                }
            };

            // Cancel callback
            CancelCallback cancelCallback = consumerTag -> log.warn("Consumer cancelled: {}", consumerTag);

            // Start consuming
            consumeQueue(QUEUE_NAME1, deliverCallback, cancelCallback);
            consumeQueue(QUEUE_NAME2, deliverCallback, cancelCallback);
            consumeQueue(QUEUE_NAME3, deliverCallback, cancelCallback);

            log.info("RabbitMQ Consumer started successfully");

        } catch (Exception ex) {
            log.error("Failed to initialize RabbitMQ consumer", ex);
            throw new RuntimeException("RabbitMQ Consumer startup failed", ex);
        }
    }

    private void declareQueue(String queueName) throws IOException {

        channel.queueDeclare(queueName, true, false, false, null);
        log.info("Queue declared successfully: {}", queueName);
    }

    private void consumeQueue(String queueName, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException {
        channel.basicConsume(queueName, false, deliverCallback, cancelCallback);
        log.info("Started consuming queue: {}", queueName);
    }

    private void processMessage(String message) throws InterruptedException {
        log.info("Processing message: {}", message);
        // Simulate processing
        Thread.sleep(message.length() * 100L);
        log.info("Message processed successfully");
    }

    @PreDestroy
    public void shutdown() {

        log.info("Shutting down RabbitMQ consumer...");

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

            log.error(
                    "Error while shutting down RabbitMQ resources",
                    ex
            );
        }
    }
}