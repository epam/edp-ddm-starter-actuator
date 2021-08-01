package com.epam.digital.data.platform.starter.actuator.livenessprobe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.availability.ApplicationAvailability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LivenessResponseCheckHealthIndicatorTest {

  private LivenessResponseCheckHealthIndicator livenessResponseCheckHealthIndicator;

  @Mock
  private ApplicationAvailability applicationAvailability;

  @Test
  void expectCustomStateIsMappedCorrectlyToHealth() {
    when(applicationAvailability.getState(any())).thenReturn(LivenessResponseBasedState.CORRECT);
    livenessResponseCheckHealthIndicator =
        new LivenessResponseCheckHealthIndicator(applicationAvailability);

    Health actual = livenessResponseCheckHealthIndicator.health();

    assertThat(actual.getStatus()).isEqualTo(Status.UP);
  }
}
