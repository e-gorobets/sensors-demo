package com.company.sensorsview.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MqttInitializer {
    @Autowired
    private MqttSubscriptionService mqttSubscriptionService;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        mqttSubscriptionService.initializeSubscriptions();
    }
}