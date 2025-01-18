package com.company.sensorsview.view.main;

import com.company.sensorsview.view.sensor.SensorListView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route("")
@ViewController(id = "MainView")
@ViewDescriptor(path = "main-view.xml")
public class MainView extends StandardMainView {
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if the root path is accessed
        if (event.getLocation().getPath().isEmpty()) {
            // Redirect to the Dashboard route when accessing the root path
            event.forwardTo(SensorListView.class);
        }
    }
}
