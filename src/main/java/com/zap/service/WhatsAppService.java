package com.zap.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WhatsAppService {
    private final WebClient webClient;
    private final String phoneNumberId = "571710919355097";
    //system generated access token
    private final String token = "EAAKbvwbT8zABO2HHq1akVQceuW1J1SStRBNGsQ4hZAPzwbavB2NYvyL8YSIPZBKOFRogFyg5vEzcgqwZCUrFJQwxi9MMG1nwBCbJho6GDkqkuCZAGtmeY1TNy4gs82Wo02czXkKlAFKsVS0BX9Che2pDJJ6xrxZCkKSwQ3ZAxgruqe21AHmyhNCYdZANpI7t9Jq7Qi4ChVpsV6jOZAPpAnHSgEfwOQm4HH3XODUkl5eZB";

    public WhatsAppService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void handleIncomingMessage(Map<String, Object> payload) {

        Map<String, Object> message = extractMessage(payload);
        String userMessage = ((Map<String, String>) message.get("text")).get("body");

        System.out.println("USER MESSAGE: " + message);
        String sender = (String) message.get("from");

        String templateName = chooseTemplate(userMessage.toLowerCase());

        sendTemplateMessage(templateName, sender);
        
    }

    private Map<String, Object> extractMessage(Map<String, Object> payload) {
        List<Map<String, Object>> entry = (List<Map<String, Object>>) payload.get("entry");
        Map<String, Object> change = (Map<String, Object>) ((List<Map<String, Object>>) entry.get(0).get("changes")).get(0);
        Map<String, Object> value = (Map<String, Object>) change.get("value");
        return ((List<Map<String, Object>>) value.get("messages")).get(0);
    }

    private String chooseTemplate(String messageText) {
        if (messageText.contains("reminder")) {
            return "event_reminder";
        } else if (messageText.contains("help") || messageText.contains("support")) {
            return "customer_support";
        } else {
            return "hello_world";
        }
    }

    public void sendTemplateMessage(String templateName, String recipientPhone) {
        Map<String, Object> requestBody = Map.of(
            "messaging_product", "whatsapp",
            "to", recipientPhone,
            "type", "template",
            "template", Map.of(
                "name", templateName,
                "language", Map.of("code", "en_US")
            )
        );

        webClient.post()
            .uri("https://graph.facebook.com/v22.0/" + phoneNumberId + "/messages")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(response -> System.out.println("✅ Template sent: " + response))
            .doOnError(error -> System.err.println("❌ Error sending message: " + error.getMessage()))
            .subscribe();
    }

}
