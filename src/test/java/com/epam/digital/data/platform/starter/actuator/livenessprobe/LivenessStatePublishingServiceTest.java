package com.epam.digital.data.platform.starter.actuator.livenessprobe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LivenessStatePublishingServiceTest {

  private LivenessStatePublishingService livenessStatePublishingService;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @Test
  void expectCorrectStateEventIsPublishedOnStart() {
    livenessStatePublishingService = new LivenessStatePublishingService(applicationEventPublisher);

    livenessStatePublishingService.initLivenessStateOnStartup();

    ArgumentCaptor<AvailabilityChangeEvent<LivenessResponseBasedState>> eventCaptor =
        ArgumentCaptor.forClass(AvailabilityChangeEvent.class);
    verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getState()).isEqualTo(LivenessResponseBasedState.CORRECT);
  }
}
