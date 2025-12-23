package com.mediwave.billing.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaConfig {
    // Kafka configuration only enabled when bootstrap-servers is defined
}
