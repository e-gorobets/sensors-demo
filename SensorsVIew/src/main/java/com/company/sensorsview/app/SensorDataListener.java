package com.company.sensorsview.app;

import com.company.sensorsview.entity.Measure;
import com.company.sensorsview.entity.Sensor;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import io.jmix.core.DataManager;
import io.jmix.core.security.SystemAuthenticator;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.Instant;

@Service
public class SensorDataListener implements MqttCallback {
    private final SystemAuthenticator systemAuthenticator;
    private final DataManager dataManager;
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println(throwable.toString());
    }

    public SensorDataListener(DataManager dataManager, SystemAuthenticator systemAuthenticator) {
        this.dataManager = dataManager;
        this.systemAuthenticator = systemAuthenticator;
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Instant.class, new InstantDeserializer()).
                excludeFieldsWithoutExposeAnnotation().create();

        SensorData sensorData = gson.fromJson(mqttMessage.toString(), SensorData.class);

        systemAuthenticator.withSystem(() -> {
            // Создание и сохранение сущности в контексте системной аутентификации
            Measure measure = dataManager.create(Measure.class);
            measure.setValue(String.valueOf(sensorData.getTemperature()));
            measure.setTimestamp(String.valueOf(sensorData.getTime()));

            // Загрузка Sensor по topic через JPQL
            Sensor sensor = dataManager.load(Sensor.class)
                    .query("select s from Sensor s where s.topic = :topic")
                    .parameter("topic", topic)
                    .optional()
                    .orElse(null);

            if (sensor != null) {
                measure.setSensor(sensor);
                dataManager.save(measure);
                System.out.printf("Saved measure for sensor: %s%n", sensor.getTopic());
            } else {
                System.err.printf("Sensor not found for topic: %s%n", topic);
            }

            return null; // Обязательно вернуть значение (может быть null)
        });

        System.out.printf("Read temperature: %d°C (time: %s) from topic: %s%n",
                sensorData.getTemperature(), sensorData.getTime(), topic);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) { }
}

class InstantDeserializer implements JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Instant.parse(json.getAsString());
    }
}

class SensorData {
    @Expose
    private int temperature;
    @Expose
    private Instant time;

    public SensorData() { }

    public SensorData(int temperature, Instant time) {
        this.temperature = temperature;
        this.time = time;
    }

    public int getTemperature() {
        return temperature;
    }

    public Instant getTime() {
        return time;
    }
}