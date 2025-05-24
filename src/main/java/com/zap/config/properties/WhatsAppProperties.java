package com.zap.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "whatsapp.api")
@Getter
@RequiredArgsConstructor
public class WhatsAppProperties {
    private final String baseUrl;
    private final String version;
    private final String accessToken;
    private final String whatsappBusinessAccountId;
    private final String phoneNumber;
    private final String phoneNumberId;
}
