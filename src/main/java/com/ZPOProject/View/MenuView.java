package com.ZPOProject.View;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;

@ParentLayout(MainView.class)
class MenuBar extends Div implements RouterLayout {

    public MenuBar() {
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);
        Div div = new Div();

        div.add(addMenuElement(ProjectView.class, "Project"));
        div.add(addMenuElement(StudentView.class, "Student"));
        div.add(addMenuElement(TaskView.class, "Task"));

        layout.add(div);
        add(layout);
    }


    private Component addMenuElement(
            Class<? extends Component> navigationTarget,
            String name)
    {
        return new Button(name, buttonClickEvent -> {
            getUI().ifPresent(ui -> ui.navigate(navigationTarget));
        });
    }
}