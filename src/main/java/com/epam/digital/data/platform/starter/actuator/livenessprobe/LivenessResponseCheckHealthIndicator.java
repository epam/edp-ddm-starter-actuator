package com.epam.digital.data.platform.starter.actuator.livenessprobe;

import org.springframework.boot.actuate.availability.AvailabilityStateHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.stereotype.Component;

@Component
public class LivenessResponseCheckHealthIndicator extends AvailabilityStateHealthIndicator {

  public LivenessResponseCheckHealthIndicator(ApplicationAvailability availability) {
    super(availability, LivenessResponseBasedState.class,
        statusMappings -> {
          statusMappings.add(LivenessResponseBasedState.CORRECT, Status.UP);
          statusMappings.add(LivenessResponseBasedState.BROKEN, Status.DOWN);
        });
  }
}
