
package com.testing.integration.controller;

import com.testing.integration.service.GroqService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groq")
public class GroqTestController {

    private final GroqService groqService;

    public GroqTestController(GroqService groqService) {
        this.groqService = groqService;
    }

    @GetMapping("/test")
    public String testGroq() {
        return groqService.testConnection();
    }
}
