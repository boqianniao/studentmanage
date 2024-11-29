package com.studentmanage.controller;


import com.studentmanage.entity.Student;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

@Controller
public class EditStudentController {
    
    @FXML private TextField studentIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField idCardField;
    @FXML private TextField classNameField;
    @FXML private TextField phoneField;
    
    private Student student;
    
    public void setStudent(Student student) {
        this.student = student;
        // 填充表单
        studentIdField.setText(student.getStudentId());
        nameField.setText(student.getName());
        genderComboBox.setValue(student.getGender());
        idCardField.setText(student.getIdNumber());
        classNameField.setText(student.getClassName());
        phoneField.setText(student.getPhoneNumber());
    }
    
    public Student getUpdatedStudent() {
        student.setName(nameField.getText());
        student.setGender(genderComboBox.getValue());
        student.setIdNumber(idCardField.getText());
        student.setClassName(classNameField.getText());
        student.setPhoneNumber(phoneField.getText());
        return student;
    }
}