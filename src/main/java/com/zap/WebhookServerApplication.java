package com.zap;

import com.zap.config.properties.WhatsAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(WhatsAppProperties.class)
@SpringBootApplication
public class WebhookServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebhookServerApplication.class, args);
	}

}
