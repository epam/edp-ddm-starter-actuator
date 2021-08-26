package com.epam.digital.data.platform.starter.actuator.readinessprobe;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.epam.digital.data.platform.starter.actuator.readinessprobe.KafkaConstants.KAFKA_HEALTH_TOPIC;
import static com.epam.digital.data.platform.starter.actuator.readinessprobe.KafkaConstants.RESPONSE_TIMEOUT;
import static org.apache.kafka.common.config.TopicConfig.RETENTION_MS_CONFIG;

@Component
@ConditionalOnEnabledHealthIndicator("kafka")
public class KafkaHealthCheckTopicCreator {

  private static final Long RETENTION_MS = 1000L;

  private static final int NUM_PARTITIONS = 1;
  private static final short REPLICATION_FACTOR = 1;

  private final AdminClient actuatorKafkaAdminClient;

  public KafkaHealthCheckTopicCreator(AdminClient actuatorKafkaAdminClient) {
    this.actuatorKafkaAdminClient = actuatorKafkaAdminClient;
  }

  @PostConstruct
  public void createKafkaTopic() {
    if (!isTopicExist()) {
      create();
    }
  }

  private boolean isTopicExist() {
    try {
      return actuatorKafkaAdminClient.listTopics()
          .names()
          .get(RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS)
          .contains(KAFKA_HEALTH_TOPIC);
    } catch (Exception e) {
      throw new CreateKafkaTopicException("Failed to retrieve existing kafka topics: ", e);
    }
  }

  private void create() {
    var createTopicsResult = actuatorKafkaAdminClient.createTopics(getConfiguredHealthTopics());
    try {
      createTopicsResult.all().get(RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new CreateKafkaTopicException("Failed to create a kafka topic: ", e);
    }
  }

  private Collection<NewTopic> getConfiguredHealthTopics() {
    var newTopic = new NewTopic(KAFKA_HEALTH_TOPIC, NUM_PARTITIONS, REPLICATION_FACTOR);
    newTopic.configs(Map.of(RETENTION_MS_CONFIG, Long.toString(RETENTION_MS)));
    return Collections.singleton(newTopic);
  }

  static class CreateKafkaTopicException extends RuntimeException {

    public CreateKafkaTopicException(String message, Exception e) {
      super(message, e);
    }
  }
}
