package com.epam.digital.data.platform.starter.actuator.config;

import com.epam.digital.data.platform.starter.actuator.readinessprobe.UrlAvailabilityChecker;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

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

  @Bean
  @ConditionalOnEnabledHealthIndicator("kafka")
  public <O> AdminClient actuatorKafkaAdminClient(ProducerFactory<String, O> pf) {
    Map<String, Object> props = new HashMap<>();
    props.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        pf.getConfigurationProperties().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));

    props.putAll(sslProps(pf));

    return AdminClient.create(props);
  }

  private <O> Map<String, Object> sslProps(ProducerFactory<String, O> pf) {
    Map<String, Object> props = new HashMap<>();

    putIfPresent(pf, props, CommonClientConfigs.SECURITY_PROTOCOL_CONFIG);
    putIfPresent(pf, props, SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG);
    putIfPresent(pf, props, SslConfigs.SSL_KEYSTORE_TYPE_CONFIG);
    putIfPresent(pf, props, "ssl.truststore.certificates");
    putIfPresent(pf, props, "ssl.keystore.certificate.chain");
    putIfPresent(pf, props, "ssl.keystore.key");

    return props;
  }

  private <O> void putIfPresent(ProducerFactory<String,O> pf, Map<String, Object> props, String prop) {
    if (pf.getConfigurationProperties().get(prop) != null) {
      props.put(prop, pf.getConfigurationProperties().get(prop));
    }
  }
}
