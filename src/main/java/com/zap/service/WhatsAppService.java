package com.zap.service;

import java.util.HashMap;
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

    public record TemplatePayload(String name, List<String> templateParams){}

    public TemplatePayload chooseTemplate(String messageText) {
        if (messageText.contains("reminder")) {
            return new TemplatePayload("event_reminder",
                    List.of("Startup Pitch Night", "Nourri Express", "May 5", "5:00 PM", "Djeuga Palace")
            );
        } else if (messageText.contains("help") || messageText.contains("support")) {
            return new TemplatePayload("customer_support",
                    List.of()
            );
        } else {
            return new TemplatePayload("hello_world",
                    null
            );
        }
    }

    public void sendTemplateMessage(String templateName, String recipientPhone, List<String> templateParams) {
        Map<String , Object> template = new HashMap<>();
        template.put("name", templateName);
        template.put("language", Map.of("code", "en_US"));
        if(templateParams != null && !templateParams.isEmpty()) {
            List<Map<String, String>> params = templateParams.stream()
                    .map(param -> Map.of(
                            "type", "text",
                            "text", param)
                    )
                    .toList();
            template.put("components", List.of(
                    Map.of(
                            "type", "body",
                        "parameters", params
                    )
            ));
        }

        Map<String, Object> requestBody = Map.of(
            "messaging_product", "whatsapp",
            "to", recipientPhone,
            "type", "template",
            "template", template
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
