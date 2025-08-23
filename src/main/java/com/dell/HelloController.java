package com.dell;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
public class HelloController {

    @GetMapping("/welcome")
    public String welcome() {
        String greeting;
        int hour = LocalTime.now().getHour();

        if (hour < 12) {
            greeting = "Good Morning";
        } else if (hour < 18) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        return "Welcome Raj! 👋 " + greeting + ", have a great day!";
    }
}