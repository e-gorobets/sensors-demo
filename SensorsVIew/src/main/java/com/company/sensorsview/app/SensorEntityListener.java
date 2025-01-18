package com.company.sensorsview.app;
import com.company.sensorsview.entity.Sensor;
import io.jmix.core.DataManager;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SensorEntityListener {
    @Autowired
    MqttSubscriptionService mqttSubscriptionService;

    @Autowired
    DataManager dataManager;

    @EventListener
    public void onSensorChanged(EntityChangedEvent<Sensor> event) {
        if (event.getType() == EntityChangedEvent.Type.CREATED) {
            Sensor sensor = dataManager.load(Sensor.class).id(event.getEntityId().getValue()).one();

            mqttSubscriptionService.subscribeToTopic(sensor.getTopic());
        } else if (event.getType() == EntityChangedEvent.Type.DELETED) {
            String topic = event.getChanges().getOldValue("topic");
            if (topic != null) {
                mqttSubscriptionService.unsubscribeFromTopic(topic);
            }
        } else if (event.getType() == EntityChangedEvent.Type.UPDATED) {
            Sensor sensor = dataManager.load(Sensor.class).id(event.getEntityId().getValue()).one();
            String topic = event.getChanges().getOldValue("topic");
            mqttSubscriptionService.unsubscribeFromTopic(topic);
            mqttSubscriptionService.subscribeToTopic(sensor.getTopic());
        }
    }
}
