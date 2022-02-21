/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.util.function.Supplier;

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
  public <O> KafkaTemplate<String, O> actuatorKafkaTemplate(
      ProducerFactory<String, O> pf, ConcurrentKafkaListenerContainerFactory<String, O> factory) {
    KafkaTemplate<String, O> kafkaTemplate = new KafkaTemplate<>(pf);
    factory.getContainerProperties().setMissingTopicsFatal(false);
    factory.setReplyTemplate(kafkaTemplate);
    return kafkaTemplate;
  }

  @Bean
  @ConditionalOnEnabledHealthIndicator("kafka")
  public <O> Supplier<AdminClient> actuatorKafkaAdminClientFactory(ProducerFactory<String, O> pf) {
    return () -> actuatorKafkaAdminClient(pf);
  }

  private <O> AdminClient actuatorKafkaAdminClient(ProducerFactory<String, O> pf) {
    Map<String, Object> props = new HashMap<>();
    props.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        pf.getConfigurationProperties().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));

    props.putAll(sslProps(pf));

    return AdminClient.create(props);
  }

  private <O> Map<String, Object> sslProps(ProducerFactory<String, O> pf) {
    Map<String, Object> props = new HashMap<>();

    putIfPresent(props, pf, CommonClientConfigs.SECURITY_PROTOCOL_CONFIG);
    putIfPresent(props, pf, SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG);
    putIfPresent(props, pf, SslConfigs.SSL_KEYSTORE_TYPE_CONFIG);
    putIfPresent(props, pf, SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG);
    putIfPresent(props, pf, SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG);
    putIfPresent(props, pf, SslConfigs.SSL_KEYSTORE_KEY_CONFIG);

    return props;
  }

  private <O> void putIfPresent(
      Map<String, Object> props,
      ProducerFactory<String, O> pf,
      String prop) {
    if (pf.getConfigurationProperties().get(prop) != null) {
      props.put(prop, pf.getConfigurationProperties().get(prop));
    }
  }
}
