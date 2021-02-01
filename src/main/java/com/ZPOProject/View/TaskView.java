package com.ZPOProject.View;

import com.ZPOProject.models.Project;
import com.ZPOProject.models.Task;
import com.ZPOProject.service.ProjectService;
import com.ZPOProject.service.TaskService;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Route(value = "task", layout = MenuBar.class)
public class TaskView extends VerticalLayout {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectService projectService;

    private TextField taskIdInput = new TextField();
    private Button searchTaskByIdButton = new Button("Search");

    private FormLayout formLayout = new FormLayout();

    private List<AbstractSinglePropertyField> toClear;

    private TextField taskFieldProjectName = new TextField();
    private TextField taskFieldProjectId= new TextField();
    private TextField taskFieldName = new TextField();
    private TextField taskFieldOrder = new TextField();
    private TextArea taskFieldDescription = new TextArea();
    private TextField taskFieldDateAdded = new TextField();

    private Button saveTaskButton = new Button("Save");
    private Button deleteTaskButton = new Button("Delete");

    private RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();

    private Task currentTask = null;

    public TaskView() {
        taskFieldName.setMaxLength(50);
        taskFieldDescription.setMaxLength(1000);

        taskFieldName.setRequired(true);

        toClear = List.of(
                taskFieldProjectName,
                taskFieldName,
                taskFieldOrder,
                taskFieldDateAdded,
                taskFieldProjectId
        );

        formLayout.addFormItem(taskFieldName, "Task Name");
        formLayout.addFormItem(taskFieldProjectName, "Project Name");
        formLayout.addFormItem(taskFieldProjectId, "Project Id");
        formLayout.addFormItem(taskFieldOrder, "Order");
        formLayout.addFormItem(taskFieldDescription, "Description");
        formLayout.addFormItem(taskFieldDateAdded, "Date Added");

        taskIdInput.setLabel("Input Task ID:");
        taskIdInput.setClearButtonVisible(true);

        searchTaskByIdButton.addClickListener(l -> {
            searchTaskById();
        });

        saveTaskButton.addClickListener(l -> {
            getTaskFromTextFields().ifPresent(t -> {
                taskService.setTask(t);
                taskIdInput.setValue("");
                toClear.forEach(text -> text.setValue(""));
            });
        });

        deleteTaskButton.addClickListener(l -> {
            deleteTask(currentTask);
        });

        radioGroup.setLabel("Edit existing Task or save new?");
        radioGroup.setItems("Create new Task", "Edit existing");
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setValue("Create new Task");

        radioGroup.addValueChangeListener(l -> {
           if(l.getValue().equals("Create new Task")){
               toClear.forEach(t -> t.setValue(""));
               taskFieldDateAdded.setEnabled(false);
               taskFieldProjectName.setEnabled(false);
               currentTask = null;
           }
           else{
               taskFieldDateAdded.setEnabled(true);
               taskFieldProjectName.setEnabled(true);
           }
        });

        add(taskIdInput, searchTaskByIdButton, formLayout,
                radioGroup, saveTaskButton, deleteTaskButton);

    }

    public void searchTaskById() {
        String value = taskIdInput.getValue();
        if(!value.equals("")){
            taskService.getTask(Integer.valueOf(value))
                    .ifPresentOrElse(t -> {
                        populateFields(t);
                        currentTask = t;
                    }, () -> {
                        toClear.forEach(t -> t.setValue(""));
                    });
        } else {
            toClear.forEach(t -> t.setValue(""));
        }
    }

    public void populateFields(Task task){
        taskFieldProjectName.setValue(task.getProject().getName());
        taskFieldProjectId.setValue(task.getProject().getProjectId().toString());
        taskFieldName.setValue(task.getName());
        taskFieldOrder.setValue(task.getTaskOrder().toString());
        taskFieldDescription.setValue(task.getDescription());
        taskFieldDateAdded.setValue(task.getAddedDate().toString());
    }

    public void deleteTask(Task task){
        if(task != null){
            taskService.deleteTask(task.getTaskId());
        }
    }

    public Optional<Task> getTaskFromTextFields(){
        Optional<Task> result = Optional.empty();
        Optional<Project> project = projectService.getProject(
                Integer.valueOf(taskFieldProjectId.getValue()));
        if(project.isPresent() && !taskFieldName.isEmpty()){
            Task newTask;
            if(radioGroup.getValue() == "Create new Task") newTask = new Task(
                    project.get(),
                    taskFieldName.getValue(),
                    Integer.valueOf(taskFieldOrder.getValue()),
                    taskFieldDescription.getValue());
            else {
                newTask = currentTask;
                newTask.setTaskOrder(Integer.valueOf(taskFieldOrder.getValue()));
                newTask.setDescription(taskFieldDescription.getValue());
                newTask.setName(taskFieldName.getValue());
            }
            result = Optional.of(newTask);
        }
        return result;
    }
}
