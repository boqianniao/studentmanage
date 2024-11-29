package com.studentmanage.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

@Controller
public class AdvancedSearchController {
    
    @FXML private TextField studentIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField classNameField;
    
    public String getStudentId() {
        return studentIdField.getText().trim();
    }
    
    public String getName() {
        return nameField.getText().trim();
    }
    
    public String getGender() {
        return genderComboBox.getValue();
    }
    
    public String getClassName() {
        return classNameField.getText().trim();
    }
}