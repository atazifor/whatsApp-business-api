package com.zap.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zap.config.properties.WhatsAppProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class WhatsAppService {
    Logger logger = LoggerFactory.getLogger(WhatsAppService.class);

    private final WebClient webClient;
    private final WhatsAppProperties whatsAppProperties;

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
        String uri = String.format("%s/%s/%s/messages", whatsAppProperties.getBaseUrl(), whatsAppProperties.getVersion(), whatsAppProperties.getPhoneNumberId());
        //"https://graph.facebook.com/v22.0/" + phoneNumberId + "/messages"
        logger.info("Bearer Token {}", whatsAppProperties.getAccessToken());
        webClient.post()
            .uri(uri)
            .header("Authorization", "Bearer " + whatsAppProperties.getAccessToken())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(response -> System.out.println("✅ Template sent: " + response))
            .doOnError(error -> System.err.println("❌ Error sending message: " + error.getMessage()))
            .subscribe();
    }

}
