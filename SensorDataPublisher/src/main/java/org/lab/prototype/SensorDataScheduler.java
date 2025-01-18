package org.lab.prototype;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SensorDataScheduler {

    private final SensorDataPublisher sensorDataPublisher;

    public SensorDataScheduler(SensorDataPublisher sensorDataPublisher) {
        this.sensorDataPublisher = sensorDataPublisher;
    }

    @Scheduled(fixedRate = 5_000) // TODO: поменять частоту на каждые 10 минут (600_000)
    public void publishTemperatureData() {
        sensorDataPublisher.publishTemperature();
    }
}