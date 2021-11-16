# ddm-starter-actuator

### Overview

* Project with configuration for an actuator related functionality.

### Usage

1. Specify dependency in your service:

```xml

<dependencies>
  ...
  <dependency>
    <groupId>com.epam.digital.data.platform</groupId>
    <artifactId>ddm-starter-actuator</artifactId>
    <version>...</version>
  </dependency>
  ...
</dependencies>
```

2. Auto-configuration should be activated through the `@SpringBootApplication` annotation or
   using `@EnableAutoConfiguration` annotation in main class;

3. Configuration properties example:

```yaml
probes:
  liveness:
    failureThreshold: 10

management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      enabled: true
      show-details: always
      group:
        liveness:
          include: livenessState
        readiness:
          include: readinessState
  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true
    kafka:
      enabled: false
```

More information
about [Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html).

### Available health indicators

* `com.epam.digital.data.platform.starter.actuator.livenessprobe.LivenessStateHandler` - exposes
  the `Liveness` application availability state;
* `com.epam.digital.data.platform.starter.actuator.readinessprobe.KafkaHealthIndicator` - exposes
  the `Readiness` Kafka service availability state;
* `com.epam.digital.data.platform.starter.actuator.readinessprobe.WebServicesHealthIndicator` -
  exposes the `Readiness` specified services availability state.

### Test execution

* Tests could be run via maven command:
    * `mvn verify` OR using appropriate functions of your IDE.

### License

The ddm-starter-actuator is released under version 2.0 of
the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).