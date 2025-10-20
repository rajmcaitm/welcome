package com.dell;

import com.dell.programs.ArrayManager;
import com.dell.rmq.Consumer;
import com.dell.rmq.Producer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
        Consumer.consumingMessagefromRMQ();
        Producer.pushingMessageToRMQ();
        ArrayManager manager = new ArrayManager();
        while (true) {
            int no = new Random().nextInt(100);
            System.err.println(no);
            manager.addNumber(no);
            Thread.sleep(3000);
        }
    }
}