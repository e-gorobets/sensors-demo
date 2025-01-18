package com.company.sensorsview.app;

import com.company.sensorsview.config.MqttConfig;
import com.company.sensorsview.entity.Sensor;
import io.jmix.core.DataManager;
import io.jmix.core.security.SystemAuthenticator;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MqttSubscriptionService {

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private SystemAuthenticator systemAuthenticator;

    private final Map<String, Integer> activeSubscriptions = new HashMap<>();


    public void initializeSubscriptions() {
        systemAuthenticator.withSystem(() -> {
            List<Sensor> topics = dataManager.load(Sensor.class).all().list();
            topics.forEach(sensor -> subscribeToTopic(sensor.getTopic()));
            return null;
        });
    }

    public void subscribeToTopic(String topic) {
        if (!activeSubscriptions.containsKey(topic)) {
            try {
                mqttClient.subscribe(topic, 1);
                activeSubscriptions.put(topic, 1);
                System.out.println("Subscribed to topic: " + topic);
            } catch (Exception e) {
                throw new RuntimeException("Failed to subscribe to topic: " + topic, e);
            }
        }
    }

    public void unsubscribeFromTopic(String topic) {
        if (activeSubscriptions.containsKey(topic)) {
            try {
                mqttClient.unsubscribe(topic);
                activeSubscriptions.remove(topic);
                System.out.println("Unsubscribed from topic: " + topic);
            } catch (MqttException e) {
                throw new RuntimeException("Failed to unsubscribe from topic: " + topic, e);
            }
        }
    }

}
