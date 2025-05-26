package com.zap.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.zap.service.WhatsAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MessageHandler {
    Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private final WhatsAppService whatsAppService;

    public MessageHandler(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    public void handle(Map<String, Object> value) {
        List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
        Map<String, Object> message = messages.get(0);
        String text = ((Map<String, String>)message.get("text")).get("body");
        String from = (String)message.get("from");

        logger.info("📩 Message from {}: {}", from, text);

        WhatsAppService.TemplatePayload template = whatsAppService.chooseTemplate(text);
        whatsAppService.sendTemplateMessage(template.name(), template.languageCode(), from, template.components());
    }

    public void handle(JsonNode messages) {
        for(JsonNode message: messages) {
            String type = message.path("type").asText();
            String from = message.path("from").asText();
            String messageId = message.path("id").asText();
            switch(type) {
                case "text" -> {
                    String body = message.path("text").path("body").asText();
                    logger.info("Received text from {} : {}", from, body);
                    WhatsAppService.TemplatePayload template = whatsAppService.chooseTemplate(body);
                    whatsAppService.sendTemplateMessage(template.name(), template.languageCode(), from, template.components());
                }
                case "interactive" -> {
                    JsonNode interactive = message.path("interactive");
                    String interactiveType = interactive.path("type").asText();

                    if ("nfm_reply".equals(interactiveType)) {
                        String responseJson = interactive.path("nfm_reply").path("response_json").toPrettyString();
                        logger.info("Received Flow reply: {}", responseJson);
                    } else {
                        logger.info("Received other interactive: {}", interactiveType);
                    }
                }
                default -> {
                    logger.info("Unhandled message type: {}", type);
                }
            }

        }
    }
}
