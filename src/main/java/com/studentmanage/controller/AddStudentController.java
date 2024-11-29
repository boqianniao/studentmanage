package com.studentmanage.controller;


import com.studentmanage.entity.Student;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.stream.IntStream;

@Controller
public class AddStudentController {
    
    @FXML private TextField studentIdField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField idCardField;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private ComboBox<Integer> dayComboBox;
    @FXML private TextField classNameField;
    @FXML private TextField phoneField;
    
    @FXML
    public void initialize() {
        // 初始化年份下拉框（1900-当前年份）
        int currentYear = LocalDate.now().getYear();
        yearComboBox.getItems().addAll(
                IntStream.rangeClosed(1900, currentYear)
                        .boxed()
                        .toArray(Integer[]::new)
        );
        
        // 初始化月份下拉框（1-12）
        monthComboBox.getItems().addAll(
                IntStream.rangeClosed(1, 12)
                        .boxed()
                        .toArray(Integer[]::new)
        );
        
        // 初始化日期下拉框（1-31）
        updateDayComboBox();
        
        // 当年份或月份改变时，更新日期下拉框
        yearComboBox.setOnAction(e -> updateDayComboBox());
        monthComboBox.setOnAction(e -> updateDayComboBox());
    }
    
    private void updateDayComboBox() {
        dayComboBox.getItems().clear();
        int maxDay = 31; // 默认31天
        
        if (monthComboBox.getValue() != null) {
            int month = monthComboBox.getValue();
            if (month == 4 || month == 6 || month == 9 || month == 11) {
                maxDay = 30;
            } else if (month == 2) {
                maxDay = 28;
                if (yearComboBox.getValue() != null) {
                    int year = yearComboBox.getValue();
                    if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                        maxDay = 29;
                    }
                }
            }
        }
        
        dayComboBox.getItems().addAll(
                IntStream.rangeClosed(1, maxDay)
                        .boxed()
                        .toArray(Integer[]::new)
        );
    }
    
    public Student getStudent() {
        Student student = new Student();
        student.setStudentId(studentIdField.getText());
        student.setName(nameField.getText());
        student.setGender(genderComboBox.getValue());
        student.setIdNumber(idCardField.getText());
        
        Date birthDate = new Date();
        birthDate.setTime(yearComboBox.getValue());
        birthDate.setTime(monthComboBox.getValue());
        birthDate.setTime(dayComboBox.getValue());
        student.setBirthDate(birthDate);
        
        student.setClassName(classNameField.getText());
        student.setPhoneNumber(phoneField.getText());
        
        return student;
    }
}