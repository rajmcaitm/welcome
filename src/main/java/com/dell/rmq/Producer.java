package com.dell.rmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

    private final static String QUEUE_NAME1 = "my_queue";
    private final static String QUEUE_NAME2 = "rmq_test";
    private final static String QUEUE_NAME3 = "samrat_dharm";

    public static void pushingMessageToRMQ() throws Exception {
        long count = 1;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://psyzlqct:4qL1oOK9zItvFmpqShj49PbM6fYFf5qg@shrimp.rmq.cloudamqp.com/psyzlqct");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declare a durable queue (survives broker restart)
            channel.queueDeclare(QUEUE_NAME1, true, false, false, null);
            channel.queueDeclare(QUEUE_NAME2, true, false, false, null);
            channel.queueDeclare(QUEUE_NAME3, true, false, false, null);

            // Send 10 tasks
            while (true){
                String message = "Hello RMQ how are you : " + count++;
                channel.basicPublish("", QUEUE_NAME1,
                        null, message.getBytes("UTF-8"));
                channel.basicPublish("", QUEUE_NAME2,
                        null, message.getBytes("UTF-8"));
                channel.basicPublish("", QUEUE_NAME3,
                        null, message.getBytes("UTF-8"));
                System.out.println("Message Sent: '" + message + "'");
                Thread.sleep(1_000);
            }
        }
    }
}
