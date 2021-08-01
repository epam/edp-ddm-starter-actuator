package com.epam.digital.data.platform.starter.actuator.livenessprobe;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LivenessStatePublishingService {

  private final ApplicationEventPublisher applicationEventPublisher;

  public LivenessStatePublishingService(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @EventListener(ApplicationStartedEvent.class)
  public void initLivenessStateOnStartup() {
    AvailabilityChangeEvent.publish(
        applicationEventPublisher, this, LivenessResponseBasedState.CORRECT);
  }
}
