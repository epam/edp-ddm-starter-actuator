package com.epam.digital.data.platform.starter.actuator.readinessprobe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;

@ExtendWith(MockitoExtension.class)
class WebServicesHealthIndicatorTest {

  @Mock
  private UrlAvailabilityChecker urlAvailabilityChecker;
  @Mock
  private ReadinessServicesConfig readinessServicesConfig;

  private WebServicesHealthIndicator webServicesHealthIndicator;

  @BeforeEach
  void setUp() {
    when(readinessServicesConfig.getServices()).thenReturn(Collections.emptySet());
    webServicesHealthIndicator =
        new WebServicesHealthIndicator(readinessServicesConfig, urlAvailabilityChecker);
  }

  @Test
  void shouldReportDownStatusIfOneServiceIsDown() {
    when(urlAvailabilityChecker.getDownServices(any())).thenReturn(downServices());

    var health = webServicesHealthIndicator.health();

    assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    assertThat(health.getDetails()).containsEntry("SERVICES_DOWN", Set.of("example.gov.ua"));
  }

  @Test
  void shouldReportUpStatusIfNoServiceIsDown() {
    when(urlAvailabilityChecker.getDownServices(any())).thenReturn(Collections.emptySet());

    var health = webServicesHealthIndicator.health();

    assertThat(health.getStatus()).isEqualTo(Status.UP);
  }

  private Set<String> downServices() {
    return Set.of("example.gov.ua");
  }
}
