/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
