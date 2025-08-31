package com.dell;

import com.dell.rmq.Consumer;
import com.dell.rmq.Producer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
        Consumer.consumingMessagefromRMQ();
        Producer.pushingMessageToRMQ();


    }
}