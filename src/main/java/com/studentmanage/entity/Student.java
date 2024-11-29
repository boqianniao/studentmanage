package com.studentmanage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Student extends Person{
    private String studentId;
    private String className;
    private String phoneNumber;
}
