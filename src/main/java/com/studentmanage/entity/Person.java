package com.studentmanage.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Person {
    private String name;
    private String gender;
    private String idNumber;
    private Date birthDate;
}
