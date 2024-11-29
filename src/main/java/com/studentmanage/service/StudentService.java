package com.studentmanage.service;


import com.studentmanage.controller.ClassStatisticsController;
import com.studentmanage.entity.Student;

import java.util.List;

@SuppressWarnings("ALL")
public interface StudentService {

    /**
     * 保存数据到文件
     * @return boolean
     */
    boolean saveToFile();

    /**
     * 根据学号排序
     * @return List<Student>
     */
    List<Student> sortByStudentId() ;

    /**
     * 根据学号查找学生
     * @param studentId
     * @return Student
     */
    Student findByStudentId(String studentId) ;

    /**
     * 删除学生
     * @param studentId
     * @return boolean
     */
    boolean deleteStudent(String studentId) ;

    /**
     * 搜索全部学生
     * @return List<Student>
     */
    List<Student> findAllStudents();

    /**
     * 插入学生信息
     * @param newStudent
     */
    void saveStudent(Student newStudent);

    /**
     * 设置数据文件
     * @param absolutePath
     */
    void setDataFile(String absolutePath);

    /**
     * 获得学生总数
     * @return
     */
    int getTotalCount();

    /**
     * 高级搜索
     * @param studentId
     * @param name
     * @param gender
     * @param className
     * @return List<Student>
     */
    List<Student> advancedSearch(String studentId, String name, String gender, String className);

    /**
     * 获得班级统计信息
     * @return List<ClassStatisticsController.ClassStatistics>
     */
    List<ClassStatisticsController.ClassStatistics> getClassStatistics();

    /**
     * 更新学生信息
     * @param student
     */
    void updateStudent(Student student);
}