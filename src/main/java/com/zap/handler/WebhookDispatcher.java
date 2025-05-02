package com.zap.handler;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WebhookDispatcher {
    private final MessageHandler messageHandler;
    private final StatusHandler statusHandler;

    public WebhookDispatcher(MessageHandler messageHandler, StatusHandler statusHandler) {
        this.messageHandler = messageHandler;
        this.statusHandler = statusHandler;
    }

    public void dispatch(Map<String, Object> payload) {
        try {
            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");
            for(Map<String, Object> entry: entries) {
                List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");
                for(Map<String, Object> change: changes) {
                    Map<String, Object> value = (Map<String, Object>) change.get("value");
                    if(value.containsKey("messages")) {
                        messageHandler.handle(value);
                    }else if (value.containsKey("statuses")) {
                        statusHandler.handle(value);
                    } else {
                        System.out.println("🔍 Unhandled webhook type: " + value.keySet());
                    }
                }
            }
        } catch(Exception e) {
            System.err.println("❌ Webhook dispatch error: " + e.getMessage());
        }
    }
}
