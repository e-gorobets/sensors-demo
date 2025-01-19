package org.lab.prototype;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SensorDataScheduler {
    private final SensorDataPublisher sensorDataPublisher;

    public SensorDataScheduler(SensorDataPublisher sensorDataPublisher) {
        this.sensorDataPublisher = sensorDataPublisher;
    }

    @Scheduled(fixedRateString = "#{@mqttConfig.fixedRate}")
    public void publishTemperatureData() {
        sensorDataPublisher.publishTemperature();
    }
}