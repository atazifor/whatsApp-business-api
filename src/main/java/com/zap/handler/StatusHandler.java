package com.zap.handler;

import com.zap.service.WhatsAppService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatusHandler {
    private final WhatsAppService whatsAppService;

    public StatusHandler(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    public void handle(Map<String, Object> value) {
        List<Map<String, Object>> statuses = (List<Map<String, Object>>) value.get("statuses");
        for (Map<String, Object> status : statuses) {
            String statusType = (String) status.get("status");
            String messageId = (String) status.get("id");
            String recipient = (String) status.get("recipient_id");

            System.out.println("📬 Message " + messageId + " to " + recipient + " is now: " + statusType);
        }
    }
}
