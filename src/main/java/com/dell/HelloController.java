package com.dell;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/welcome")
    public String welcome(@RequestParam(defaultValue = "Guest") String name) {
        return "Hello - " + name + " Welcome to Bangalore...";
    }
}