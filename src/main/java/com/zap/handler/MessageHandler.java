package com.zap.handler;

import com.zap.service.WhatsAppService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MessageHandler {
    private final WhatsAppService whatsAppService;

    public MessageHandler(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    public void handle(Map<String, Object> value) {
        List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
        Map<String, Object> message = messages.get(0);
        String text = ((Map<String, String>)message.get("text")).get("body");
        String from = (String)message.get("from");

        System.out.println("📩 Message from " + from + ": " + text);

        String template = whatsAppService.chooseTemplate(text);
        whatsAppService.sendTemplateMessage(template, from);
    }
}
