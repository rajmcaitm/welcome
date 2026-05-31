package com.dell.rmq;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
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

@Component
@Slf4j
public class Consumer {

    @Value("${rabbitmq.queues}")
    private String queuesConfig;

    @Value("${rabbitmq.consumer.prefetch-count:1}")
    private int prefetchCount;

    @Value("${rabbitmq.consumer.auto-ack:false}")
    private boolean autoAck;

    @Value("${rabbitmq.consumer.processing-delay-multiplier:100}")
    private long processingDelayMultiplier;

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

    @PostConstruct
    public void startConsumer() {

        try {
            log.info("Initializing RabbitMQ Consumer...");

            // Create connection
            connection = rabbitConnectionFactory.newConnection();

            // Create channel
            channel = connection.createChannel();

            // Configure QoS
            channel.basicQos(prefetchCount);

            // Get queue list from properties
            List<String> queueNames = Arrays.asList(queuesConfig.split(","));
            log.info("Configured queues: {}", queueNames);

            // Declare queues
            for (String queueName : queueNames) {
                declareQueue(queueName.trim());
            }

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
            for (String queueName : queueNames) {
                consumeQueue(queueName.trim(), deliverCallback, cancelCallback);
            }

            log.info("RabbitMQ Consumer started successfully");

        } catch (Exception ex) {
            log.error("Failed to initialize RabbitMQ consumer", ex);
            throw new RuntimeException("RabbitMQ Consumer startup failed", ex);
        }
    }

    private void declareQueue(String queueName) throws IOException {

        channel.queueDeclare(queueName, queueDurable, queueExclusive, queueAutoDelete, null);
        log.info("Queue declared successfully: {} (durable={}, exclusive={}, auto-delete={})",
                queueName, queueDurable, queueExclusive, queueAutoDelete);
    }

    private void consumeQueue(String queueName, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException {
        channel.basicConsume(queueName, autoAck, deliverCallback, cancelCallback);
        log.info("Started consuming queue: {} (auto-ack={})", queueName, autoAck);
    }

    private void processMessage(String message) throws InterruptedException {
        log.info("Processing message: {}", message);
        // Simulate processing based on configurable delay
        Thread.sleep(message.length() * processingDelayMultiplier);
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