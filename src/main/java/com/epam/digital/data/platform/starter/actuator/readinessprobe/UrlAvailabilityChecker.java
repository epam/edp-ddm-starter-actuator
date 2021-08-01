package com.epam.digital.data.platform.starter.actuator.readinessprobe;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class UrlAvailabilityChecker {

  private final RestTemplate restTemplate;

  public UrlAvailabilityChecker(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Set<String> getDownServices(Set<String> services) {
    return services.stream()
        .filter(this::isNotHealthy)
        .collect(Collectors.toSet());
  }

  private boolean isNotHealthy(String url) {
    try {
      return restTemplate.exchange(url, HttpMethod.GET, null, String.class)
          .getStatusCode().isError();
    } catch (ResourceAccessException ex) {
      return true;
    }
  }
}
