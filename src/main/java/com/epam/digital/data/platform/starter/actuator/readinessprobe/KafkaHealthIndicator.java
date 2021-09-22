package com.epam.digital.data.platform.starter.actuator.readinessprobe;

import static com.epam.digital.data.platform.starter.actuator.readinessprobe.KafkaConstants.KAFKA_HEALTH_TOPIC;
import static com.epam.digital.data.platform.starter.actuator.readinessprobe.KafkaConstants.RESPONSE_TIMEOUT;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnEnabledHealthIndicator("kafka")
public class KafkaHealthIndicator implements HealthIndicator {

  private final Logger log = LoggerFactory.getLogger(KafkaHealthIndicator.class);

  private final KafkaTemplate<String, String> kafka;

  private static final String KAFKA_HEALTH_MESSAGE = "Check";

  public KafkaHealthIndicator(@Qualifier("kafkaTemplate") KafkaTemplate<String, String> kafka) {
    this.kafka = kafka;
  }

  @Override
  public Health health() {
    try {
      kafka.send(KAFKA_HEALTH_TOPIC, KAFKA_HEALTH_MESSAGE)
          .get(RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      log.error("KafkaHealthIndicator failed", e);
      return Health.down()
              .withException(e)
              .build();
    }
    return Health.up().build();
  }
}
