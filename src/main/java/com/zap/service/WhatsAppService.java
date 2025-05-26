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

    public record TemplatePayload(
            String name,
            String languageCode,
            List<Map<String, Object>> components
    ) {}

    public TemplatePayload chooseTemplate(String messageText) {
        if (messageText.contains("reminder")) {
            return new TemplatePayload("event_reminder", "en_US", List.of(
                Map.of(
                    "type", "body",
                    "parameters", List.of(
                            Map.of("type", "text", "text", "Startup Pitch Night"),
                            Map.of("type", "text", "text", "Nourri Express"),
                            Map.of("type", "text", "text", "May 5"),
                            Map.of("type", "text", "text", "5:00 PM"),
                            Map.of("type", "text", "text", "Djeuga Palace")
                    )
                )
            ));
        } else if (messageText.contains("apt") || messageText.contains("appointment")) {
            return new TemplatePayload("name_dob_capture", "en", List.of(
                  Map.of(
                      "type", "header",
                      "parameters", List.of(Map.of(
                              "type", "image",
                              "image", Map.of("link", "https://i.imgur.com/cRnpp1Q.jpeg")
                      ))
                  ),
                    Map.of(
                    "type", "button",
                    "sub_type", "flow",
                    "index", "0",
                    "parameters", List.of(Map.of(
                            "type", "action",
                            "action", Map.of(
                                    "flow_token", "TEST_TOKEN",
                                    "flow_action_data", Map.of()
                            )
                        ))
                    )
            ));
        } else {
            return new TemplatePayload("hello_world", "en_US",
                    null
            );
        }
    }

    public void sendTemplateMessage(String templateName, String languageCode, String recipientPhone, List<Map<String, Object>> components) {
        Map<String , Object> template = new HashMap<>();
        template.put("name", templateName);
        template.put("language", Map.of("code", languageCode));
        if(components != null && !components.isEmpty()) {
            template.put("components", components);
        }

        Map<String, Object> requestBody = Map.of(
            "messaging_product", "whatsapp",
            "to", recipientPhone,
            "type", "template",
            "template", template
        );
        String uri = String.format("%s/%s/%s/messages", whatsAppProperties.getBaseUrl(), whatsAppProperties.getVersion(), whatsAppProperties.getPhoneNumberId());
        //"https://graph.facebook.com/v22.0/" + phoneNumberId + "/messages"
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
