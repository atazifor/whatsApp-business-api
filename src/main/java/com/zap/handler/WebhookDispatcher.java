package com.zap.handler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookDispatcher {
    Logger logger = LoggerFactory.getLogger(WebhookDispatcher.class);

    private final MessageHandler messageHandler;
    private final StatusHandler statusHandler;

    public void dispatch(JsonNode node) {
        JsonNode value = node.at("/entry/0/changes/0/value");
        if(value.has("messages")) {
            JsonNode messages = value.get("messages");
            logger.info("We got a message: {}", messages);
            messageHandler.handle(messages);
        }
        if(value.has("statuses")) {
            JsonNode statuses = value.get("statuses");
            logger.info("Status updates: {}", statuses);
            statusHandler.handle(statuses);
        }
    }
}
