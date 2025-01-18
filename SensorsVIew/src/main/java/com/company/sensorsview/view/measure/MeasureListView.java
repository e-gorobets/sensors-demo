package com.company.sensorsview.view.measure;

import com.company.sensorsview.entity.Measure;
import com.company.sensorsview.view.main.MainView;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;

import java.util.UUID;


@Route(value = "sensors/:id/measures", layout = MainView.class)
@ViewController(id = "Measure.list")
@ViewDescriptor(path = "measure-list-view.xml")
@DialogMode(width = "64em")
public class MeasureListView extends StandardListView<Measure> {
    private UUID sensorId;
    @ViewComponent
    private CollectionLoader<Measure> measuresDl;
    @ViewComponent
    private JmixButton refreshButton;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters routeParameters = event.getRouteParameters();
        this.sensorId = UUID.fromString(routeParameters.get("id").get());
        super.beforeEnter(event);
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        measuresDl.setParameter("sensorId", this.sensorId);
        measuresDl.load();
    }

    @Subscribe
    public void onInit(InitEvent event) {
        refreshButton.addClickListener(event1 ->
                measuresDl.load()
        );
    }
}