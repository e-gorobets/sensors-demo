package com.company.sensorsview.view.sensor;

import com.company.sensorsview.entity.Sensor;
import com.company.sensorsview.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "sensors/:id", layout = MainView.class)
@ViewController(id = "Sensor.detail")
@ViewDescriptor(path = "sensor-detail-view.xml")
@EditedEntityContainer("sensorDc")
public class SensorDetailView extends StandardDetailView<Sensor> {
}