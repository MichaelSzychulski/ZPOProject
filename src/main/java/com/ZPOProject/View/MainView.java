package com.ZPOProject.View;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

public class MainView extends Div implements RouterLayout {

    public MainView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

        layout.add(new H1("Michael Szychulski - ZPO Project"));

        add(layout);
    }

}
