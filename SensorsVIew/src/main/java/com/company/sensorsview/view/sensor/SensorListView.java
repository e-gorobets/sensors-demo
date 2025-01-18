package com.company.sensorsview.view.sensor;

import com.company.sensorsview.entity.Measure;
import com.company.sensorsview.entity.Sensor;
import com.company.sensorsview.view.main.MainView;
import com.company.sensorsview.view.measure.MeasureListView;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.internal.Pair;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiNavigationProperties;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.data.grid.ContainerDataGridItems;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.action.BaseAction;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;


@Route(value = "sensors", layout = MainView.class)
@ViewController(id = "Sensor.list")
@ViewDescriptor(path = "sensor-list-view.xml")
@DialogMode(width = "64em")
public class SensorListView extends StandardListView<Sensor> {
    @ViewComponent
    private DataGrid<Sensor> sensorsDataGrid;

    @ViewComponent
    private CollectionContainer<Sensor> sensorsDc;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Notifications notifications;
    @Autowired
    private ViewNavigators viewNavigators;
    @ViewComponent
    private JmixButton readButton;
    @ViewComponent("sensorsDataGrid.viewPlot")
    private BaseAction sensorsDataGridViewPlot;

    @Subscribe("sensorsDataGrid.viewPlot")
    public void onSensorsDataGridViewPlot(final ActionPerformedEvent event) {
        UUID sensorId = getSelectedSensorId();

        if (sensorId == null) { return; }
        RouteParameters routeParameters = new RouteParameters("id", sensorId.toString());
        viewNavigators.listView(this, Measure.class).withBackwardNavigation(true)
                .withRouteParameters(routeParameters)
                .navigate();
    }

    private UUID getSelectedSensorId() {
        Sensor selectedSensor = sensorsDataGrid.getSingleSelectedItem();
        return selectedSensor != null ? selectedSensor.getId() : null;
    }

    @Subscribe
    public void onInit(InitEvent event) {
        sensorsDataGridViewPlot.setEnabled(false);
        sensorsDataGrid.addComponentColumn(sensor -> {
            String value = dataManager.loadValue(
                    "select m.value from Measure m where m.sensor.id = :sensorId order by m.timestamp desc", String.class)
                .parameter("sensorId", sensor.getId())
                .maxResults(1)
                .optional()
                .orElse("None");
            return new Span(value);
        }).setHeader("Last Temperature");
        sensorsDataGrid.setItems(new ContainerDataGridItems<>(sensorsDc));
        sensorsDataGrid.addSelectionListener(selectionEvent -> {
            sensorsDataGridViewPlot.setEnabled(getSelectedSensorId() != null);
        });

    }
}