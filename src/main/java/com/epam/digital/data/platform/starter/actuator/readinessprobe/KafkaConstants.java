package com.epam.digital.data.platform.starter.actuator.readinessprobe;

public class KafkaConstants {

  public static final String KAFKA_HEALTH_TOPIC = "kafka-health-check";
  public static final Long TOPIC_CREATION_TIMEOUT = 60L;
  public static final Long RESPONSE_TIMEOUT = 4000L;
  public static final Long RETENTION_MS = 1000L;

  private KafkaConstants() {
  }
}
