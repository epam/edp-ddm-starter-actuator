package com.epam.digital.data.platform.starter.actuator.readinessprobe;

import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "management.health.webservices.readiness")
public class ReadinessServicesConfig {

  private Set<String> services = new HashSet<>();

  public Set<String> getServices() {
    return services;
  }

  public void setServices(Set<String> services) {
    this.services = services;
  }
}
