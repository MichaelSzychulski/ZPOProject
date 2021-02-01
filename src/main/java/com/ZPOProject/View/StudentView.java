package com.ZPOProject.View;

import com.ZPOProject.models.Project;
import com.ZPOProject.models.Student;
import com.ZPOProject.service.ProjectService;
import com.ZPOProject.service.StudentService;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Route(value = "student", layout = MenuBar.class)
public class StudentView extends VerticalLayout {
    @Autowired
    private StudentService studentService;
    @Autowired
    private ProjectService projectService;

    private TextField studentIdInput = new TextField();
    private Button searchStudentByIdButton = new Button("Search");

    private FormLayout formLayout = new FormLayout();

    private List<AbstractSinglePropertyField> textFields;

    private TextField studentFieldName = new TextField();
    private TextField studentFieldSurname = new TextField();
    private TextField studentFieldIndexNum = new TextField();
    private TextField studentFieldEmail = new TextField();

    private RadioButtonGroup<Boolean> landline = new RadioButtonGroup<>();

    private Button saveStudentButton = new Button("Save");
    private Button deleteStudentButton = new Button("Delete");

    private RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();

    private Grid<Project> projects = new Grid<>();

    private Student currentStudent = null;

    public StudentView() {
        studentFieldName.setMaxLength(50);
        studentFieldName.setRequired(true);
        studentFieldSurname.setRequired(true);
        studentFieldIndexNum.setRequired(true);
        landline.setRequired(true);

        textFields = List.of(
                studentFieldName,
                studentFieldSurname,
                studentFieldIndexNum,
                studentFieldEmail
        );

        formLayout.addFormItem(studentFieldName, "Name");
        formLayout.addFormItem(studentFieldSurname, "Surname");
        formLayout.addFormItem(studentFieldIndexNum, "Index Number");
        formLayout.addFormItem(studentFieldEmail, "Email");
        formLayout.addFormItem(landline, "Landline? ");


        studentIdInput.setLabel("Input Student ID:");
        studentIdInput.setClearButtonVisible(true);

        searchStudentByIdButton.addClickListener(l -> {
            searchStudentById();

        });

        saveStudentButton.addClickListener(l -> {
            getStudentFromTextFields().ifPresent(t -> {
                studentService.setStudent(t);
                studentIdInput.setValue("");
                textFields.forEach(text -> text.setValue(""));
            });
        });

        deleteStudentButton.addClickListener(l -> {
            deleteStudent(currentStudent);
        });

        radioGroup.setLabel("Edit existing Student or save new?");
        radioGroup.setItems("Create new Student", "Edit existing");
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setValue("Create new Student");

        radioGroup.addValueChangeListener(l -> {
            if (l.getValue().equals("Create new Student")) {
                textFields.forEach(t -> t.setValue(""));
                currentStudent = null;
            }
        });

        landline.setItems(true, false);
        landline.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        landline.setValue(false);

        projects.addColumn(Project::getProjectId).setHeader("ID");
        projects.addColumn(Project::getName).setHeader("Name");
        projects.addColumn(Project::getReturnDate).setHeader("Return Date");

        formLayout.addFormItem(projects, "Projects");

        add(studentIdInput,
                searchStudentByIdButton,
                formLayout,
                radioGroup,
                saveStudentButton,
                deleteStudentButton);

    }

    public void searchStudentById() {
        String value = studentIdInput.getValue();
        if (!value.equals("")) {
            studentService.getStudent(Integer.valueOf(value))
                    .ifPresentOrElse(t -> {
                        populateFields(t);
                        currentStudent = t;
                        refreshProjectList(currentStudent.getStudentId());
                    }, () -> {
                        textFields.forEach(t -> t.setValue(""));
                    });
        } else {
            textFields.forEach(t -> t.setValue(""));
        }
    }

    public void populateFields(Student student) {
        studentFieldName.setValue(student.getName());
        studentFieldSurname.setValue(student.getSurname());
        studentFieldIndexNum.setValue(student.getIndexNum());
        studentFieldEmail.setValue(student.getEmail());
        landline.setValue(student.isLandline());
    }

    public void deleteStudent(Student student) {
        if (student != null) {
            studentService.deleteStudent(student.getStudentId());
        }
    }

    public Optional<Student> getStudentFromTextFields() {
        Optional<Student> result = Optional.empty();
        if (!studentFieldName.isEmpty() &&
                !studentFieldSurname.isEmpty() &&
                !landline.isEmpty() &&
                !studentFieldIndexNum.isEmpty()) {
            Student newStudent;
            if (radioGroup.getValue() == "Create new Student") newStudent = new Student(
                    studentFieldName.getValue(),
                    studentFieldSurname.getValue(),
                    studentFieldIndexNum.getValue(),
                    studentFieldEmail.getValue(),
                    landline.getValue());
            else {
                newStudent = currentStudent;
                newStudent.setName(studentFieldName.getValue());
                newStudent.setSurname(studentFieldSurname.getValue());
                newStudent.setIndexNum(studentFieldIndexNum.getValue());
                newStudent.setEmail(studentFieldEmail.getValue());
                newStudent.setLandline(landline.getValue());
            }
            result = Optional.of(newStudent);
        }
        return result;
    }

    public void refreshProjectList(Integer studentId){
        List<Project> projectList = projectService.findByStudent(studentId);
        projects.setItems(projectList);
    }


}