package com.microservice.gateway.api_gateway_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "app.usage-tracking.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTopicConfig {

    @Value("${app.usage-tracking.topic:app-usage-events}")
    private String usageTopic;

    @Bean
    public NewTopic appUsageTopic() {
        return TopicBuilder.name(usageTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
