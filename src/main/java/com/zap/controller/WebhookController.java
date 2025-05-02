package com.zap.controller;

import com.zap.handler.WebhookDispatcher;
import org.springframework.web.bind.annotation.RestController;

import com.zap.service.WhatsAppService;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
public class WebhookController {

    private final WebhookDispatcher dispatcher;

    public WebhookController(WebhookDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    private static final String VERIFY_TOKEN = "zap_verify"; // use the same

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
        @RequestParam(value = "hub.mode", required = false) String mode,
        @RequestParam(value = "hub.verify_token", required = false) String token,
        @RequestParam(value = "hub.challenge", required = false) String challenge
    ) {
        System.out.println("Verify webhook called ");
        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
            return ResponseEntity.ok(challenge);
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
        }
    }
    


    @GetMapping("/hello")
    public String hello() {
        return "Hello from Zap Demo";
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println(" ==== Received webhook: === \n" + payload + "\n =====");
        dispatcher.dispatch(payload);

        return ResponseEntity.ok().build();
    }
    

}
