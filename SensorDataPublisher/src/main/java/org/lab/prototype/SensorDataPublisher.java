package org.lab.prototype;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
public class SensorDataPublisher {

    private final MqttClient mqttClient;
    private final Random random = new Random();

    public SensorDataPublisher(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void publishTemperature() {
        String topic = "-----------------"; // TODO: Должно браться из файла настройки.
        int temperature = 20 + random.nextInt(41); // TODO: Генерация температуры от 20 до 60; Должно браться из файла настройки.
        Instant time = Instant.now();
        String payload = String.format("{\"temperature\": %d, \"time\": \"%s\"}",
                                                temperature, time);

        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            mqttClient.publish(topic, message);
            System.out.printf("Published temperature: %d°C (time: %s) to topic: %s%n", temperature, time, topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}