package com.epam.digital.data.platform.starter.actuator.livenessprobe;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class LivenessStateHandler {

  private final Integer livenessProbeFailureThreshold;

  private final ApplicationEventPublisher applicationEventPublisher;
  private final ApplicationAvailability applicationAvailability;

  private final AtomicInteger unhealthyConsecutiveRequestsCounter = new AtomicInteger(0);

  public LivenessStateHandler(
      @Value("${probes.liveness.failureThreshold:10}") Integer livenessProbeFailureThreshold,
      ApplicationEventPublisher applicationEventPublisher,
      ApplicationAvailability applicationAvailability) {
    this.livenessProbeFailureThreshold = livenessProbeFailureThreshold;
    this.applicationEventPublisher = applicationEventPublisher;
    this.applicationAvailability = applicationAvailability;
  }

  public <T> void handleResponse(T response, Predicate<T> unhealthyResponseCondition) {
    if (unhealthyResponseCondition.test(response)) {
      if (unhealthyConsecutiveRequestsCounter.incrementAndGet() >= livenessProbeFailureThreshold) {
        AvailabilityChangeEvent.publish(
            applicationEventPublisher, this, LivenessResponseBasedState.BROKEN);
      }
    } else {
      unhealthyConsecutiveRequestsCounter.set(0);
      LivenessResponseBasedState currState = applicationAvailability.getState(
              LivenessResponseBasedState.class, LivenessResponseBasedState.BROKEN);
      if (LivenessResponseBasedState.BROKEN.equals(currState)) {
        AvailabilityChangeEvent.publish(
            applicationEventPublisher, this, LivenessResponseBasedState.CORRECT);
      }
    }
  }
}
