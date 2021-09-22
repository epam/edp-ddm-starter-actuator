package com.epam.digital.data.platform.starter.actuator.readinessprobe;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class WebServicesHealthIndicator implements HealthIndicator {

  private final Logger log = LoggerFactory.getLogger(WebServicesHealthIndicator.class);
  private final UrlAvailabilityChecker urlAvailabilityChecker;
  private final Set<String> services;

  public WebServicesHealthIndicator(
      ReadinessServicesConfig servicesConfig, UrlAvailabilityChecker urlAvailabilityChecker) {
    this.services = servicesConfig.getServices();
    this.urlAvailabilityChecker = urlAvailabilityChecker;
  }

  @Override
  public Health health() {
    var downResponses = urlAvailabilityChecker.getDownServices(services);

    if (downResponses.isEmpty()) {
      return Health.up().build();
    }

    log.error("WebServicesHealthIndicator failed: {}", downResponses);
    return Health.down()
        .withDetail("SERVICES_DOWN", downResponses)
        .build();
  }
}
