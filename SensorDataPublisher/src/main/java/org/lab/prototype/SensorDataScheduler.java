package org.lab.prototype;

import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@PropertySource("classpath:application.properties")
@Component
public class SensorDataScheduler {
    private final SensorDataPublisher sensorDataPublisher;

    public SensorDataScheduler(SensorDataPublisher sensorDataPublisher) {
        this.sensorDataPublisher = sensorDataPublisher;
    }

    @Scheduled(fixedRateString = "${fixed.rate}")
    public void publishTemperatureData() {
        sensorDataPublisher.publishTemperature();
    }
}