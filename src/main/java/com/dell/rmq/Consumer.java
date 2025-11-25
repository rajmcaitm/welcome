package com.dell.rmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Consumer {

    private final static String QUEUE_NAME1 = "my_queue";
    private final static String QUEUE_NAME2 = "rmq_test";
    private final static String QUEUE_NAME3 = "samrat_dharm";

    public static void consumingMessagefromRMQ() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://psyzlqct:4qL1oOK9zItvFmpqShj49PbM6fYFf5qgS@shrimp.rmq.cloudamqp.com/psyzlqct");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Durable queue (must match producer)
        channel.queueDeclare(QUEUE_NAME1, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME2, true, false, false, null);
        channel.queueDeclare(QUEUE_NAME3, true, false, false, null);
        System.out.println(" [*] Waiting for tasks. To exit press CTRL+C");

        // Fair dispatch (don't give more than 1 message at a time to a worker)
        channel.basicQos(1);

        // Consumer callback
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String task = new String(delivery.getBody(), "UTF-8");
            System.out.println("Message Received: '" + task + "'");

            try {
                // Simulate work (sleep based on task length)
                Thread.sleep(task.length() * 100L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                //System.out.println("Processing done: '" + task + "'");
                // Send manual acknowledgment
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        // Consume with manual ack
        channel.basicConsume(QUEUE_NAME1, false, deliverCallback, consumerTag -> { });
        channel.basicConsume(QUEUE_NAME2, false, deliverCallback, consumerTag -> { });
        channel.basicConsume(QUEUE_NAME3, false, deliverCallback, consumerTag -> { });
    }
}