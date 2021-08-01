package com.epam.digital.data.platform.starter.actuator.livenessprobe;

import org.springframework.boot.availability.AvailabilityState;

public enum LivenessResponseBasedState implements AvailabilityState {
  CORRECT,
  BROKEN
}