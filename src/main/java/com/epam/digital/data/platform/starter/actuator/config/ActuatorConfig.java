package com.epam.digital.data.platform.starter.actuator.config;

import com.epam.digital.data.platform.starter.actuator.readinessprobe.UrlAvailabilityChecker;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class ActuatorConfig {

  @Bean
  public UrlAvailabilityChecker urlAvailabilityChecker(ActuatorResponseErrorHandler errorHandler) {
    // Building custom restTemplate inside to avoid duplicate bean types
    var restTemplate = new RestTemplateBuilder().errorHandler(errorHandler).build();
    return new UrlAvailabilityChecker(restTemplate);
  }

  @Bean
  @ConditionalOnEnabledHealthIndicator("kafka")
  public <O> KafkaTemplate<String, O> kafkaTemplate(
      ProducerFactory<String, O> pf, ConcurrentKafkaListenerContainerFactory<String, O> factory) {
    KafkaTemplate<String, O> kafkaTemplate = new KafkaTemplate<>(pf);
    factory.getContainerProperties().setMissingTopicsFatal(false);
    factory.setReplyTemplate(kafkaTemplate);
    return kafkaTemplate;
  }
}
