package com.epam.digital.data.platform.starter.actuator.readinessprobe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

@ExtendWith(MockitoExtension.class)
class KafkaHealthIndicatorTest {

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  private KafkaHealthIndicator kafkaHealthIndicator;
  private SettableListenableFuture<SendResult<String, String>> future;

  @BeforeEach
  void setUp() {
    kafkaHealthIndicator = new KafkaHealthIndicator(kafkaTemplate);
    future = new SettableListenableFuture<>();
    future.set(new SendResult<>(null, null));
  }

  @Test
  void shouldReturnUpStatusWhenKafkaMessageSentAndGotBack() {
    when(kafkaTemplate.send(any(), any())).thenReturn(future);

    var health = kafkaHealthIndicator.health();

    assertThat(health.getStatus()).isEqualTo(Status.UP);
  }

  @Test
  void shouldReturnDownStatusWhenKafkaResultedInException() {
    when(kafkaTemplate.send(any(), any())).thenThrow(new RuntimeException());

    var health = kafkaHealthIndicator.health();

    assertThat(health.getStatus()).isEqualTo(Status.DOWN);
  }
}
