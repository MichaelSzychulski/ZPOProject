package com.ZPOProject.View;

import com.ZPOProject.models.Project;
import com.ZPOProject.models.Student;
import com.ZPOProject.models.Task;
import com.ZPOProject.service.ProjectService;
import com.ZPOProject.service.StudentService;
import com.ZPOProject.service.TaskService;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Route(value = "project", layout=MenuBar.class)
@RouteAlias(value = "", layout=MenuBar.class)
public class ProjectView extends VerticalLayout {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TaskService taskService;

    private TextField projectIdInput = new TextField();
    private Button searchProjectByIdButton = new Button("Search");

    private FormLayout formLayout = new FormLayout();

    private List<AbstractSinglePropertyField> textFields;

    private TextField projectFieldName = new TextField();
    private TextArea projectFieldDesc = new TextArea();
    private DateTimePicker projectFieldCreationDate = new DateTimePicker();
    private DatePicker projectFieldReturnDate = new DatePicker();

    private Button saveProjectButton = new Button("Save");
    private Button deleteProjectButton = new Button("Delete");

    private RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();

    private Grid<Student> students = new Grid<>();
    private Grid<Task> tasks = new Grid<>();

    private Project currentProject = null;

    public ProjectView() {
        projectFieldName.setMaxLength(50);
        projectFieldName.setMinLength(3);

        projectFieldDesc.setMaxLength(1000);

        projectFieldName.setRequired(true);
        projectFieldReturnDate.setRequired(true);

        textFields = List.of(
                projectFieldName,
                projectFieldDesc
        );

        projectFieldCreationDate.setReadOnly(true);

        formLayout.addFormItem(projectFieldName, "Name");
        formLayout.addFormItem(projectFieldDesc, "Description");
        formLayout.addFormItem(projectFieldCreationDate, "Creation Date");
        formLayout.addFormItem(projectFieldReturnDate, "Return Date");


        projectIdInput.setLabel("Input Project ID:");
        projectIdInput.setClearButtonVisible(true);

        searchProjectByIdButton.addClickListener(l -> {
            searchProjectById();
        });

        saveProjectButton.addClickListener(l -> {
            getProjectFromTextFields().ifPresent(t -> {
                projectService.setProject(t);
                projectIdInput.setValue("");
                textFields.forEach(text -> text.setValue(""));
                projectFieldCreationDate.setValue(null);
                projectFieldReturnDate.setValue(null);
            });
        });

        deleteProjectButton.addClickListener(l -> {
            deleteProject(currentProject);
        });

        radioGroup.setLabel("Edit existing Project or save new?");
        radioGroup.setItems("Create new Project", "Edit existing");
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setValue("Create new Project");

        radioGroup.addValueChangeListener(l -> {
            if (l.getValue().equals("Create new Project")) {
                textFields.forEach(t -> t.setValue(""));
                projectFieldCreationDate.setValue(null);
                projectFieldReturnDate.setValue(null);
                currentProject = null;
            }
        });

        formLayout.addFormItem(students, "Students");
        formLayout.addFormItem(tasks, "Tasks");

        students.addColumn(Student::getStudentId).setHeader("ID");
        students.addColumn(Student::getName).setHeader("Name");
        students.addColumn(Student::getSurname).setHeader("Surname");
        students.addColumn(Student::getIndexNum).setHeader("Index Number");

        tasks.addColumn(Task::getTaskId).setHeader("ID");
        tasks.addColumn(Task::getName).setHeader("Name");
        tasks.addColumn(Task::getTaskOrder).setHeader("Order");

        add(projectIdInput,
                searchProjectByIdButton,
                formLayout,
                radioGroup,
                saveProjectButton,
                deleteProjectButton);

    }

    public void searchProjectById() {
        String value = projectIdInput.getValue();
        if (!value.equals("")) {
            projectService.getProject(Integer.valueOf(value))
                    .ifPresentOrElse(t -> {
                        populateFields(t);
                        currentProject = t;
                        refreshTasksGrid(t.getProjectId());
                        refreshStudentsGrid(t.getProjectId());
                    }, () -> {
                        textFields.forEach(t -> t.setValue(""));
                        projectFieldCreationDate.setValue(null);
                        projectFieldReturnDate.setValue(null);
                        tasks.setItems(List.of());
                        students.setItems(List.of());
                    });
        } else {
            textFields.forEach(t -> t.setValue(""));
            projectFieldCreationDate.setValue(null);
            projectFieldReturnDate.setValue(null);
        }
    }

    public void populateFields(Project project) {
        projectFieldName.setValue(project.getName());
        projectFieldDesc.setValue(project.getDescription());
        projectFieldCreationDate.setValue(project.getCreationDate());
        projectFieldReturnDate.setValue(project.getReturnDate());
    }

    public void deleteProject(Project project) {
        if (project != null) {
            projectService.deleteProject(project.getProjectId());
        }
    }

    public Optional<Project> getProjectFromTextFields() {
        Optional<Project> result = Optional.empty();
        if (!projectFieldName.isEmpty() && !projectFieldReturnDate.isEmpty()) {
            Project newProject;
            if (radioGroup.getValue() == "Create new Project") newProject = new Project(
                    projectFieldName.getValue(),
                    projectFieldDesc.getValue(),
                    projectFieldReturnDate.getValue());
            else {
                newProject = currentProject;
                newProject.setName(projectFieldName.getValue());
                newProject.setDescription(projectFieldDesc.getValue());
                newProject.setReturnDate(projectFieldReturnDate.getValue());
            }
            result = Optional.of(newProject);
        }
        return result;
    }

    public void refreshStudentsGrid(Integer projectId){
        students.setItems(studentService.getStudentsByProject(projectId));
    }

    public void refreshTasksGrid(Integer projectId){
        tasks.setItems(taskService.getTasksByProject(projectId));
    }


}