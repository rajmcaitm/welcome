package com.dell;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class HelloController {

    @GetMapping("/welcome")
    public String welcome() {
        String[] fortunes = {
                "🌟 Today is a great day to start something new.",
                "🍀 Luck is on your side — take that chance.",
                "🚀 Your hard work will pay off sooner than you think.",
                "🌼 A smile you share today will return to you.",
                "🔥 Challenges ahead, but you are stronger than them."
        };

        String[] funFacts = {
                "Honey never spoils — archaeologists found 3000-year-old honey still edible!",
                "Bananas are berries, but strawberries are not.",
                "Octopuses have three hearts and blue blood.",
                "Sharks existed before trees.",
                "Wombat poop is cube-shaped."
        };

        Random random = new Random();
        String fortune = fortunes[random.nextInt(fortunes.length)];
        String fact = funFacts[random.nextInt(funFacts.length)];

        return String.format(
                "Welcome Raj! 👋\n\nYour fortune: %s\nFun Fact: %s\n\nHave an amazing day! 🌈",
                fortune, fact
        );
    }
}