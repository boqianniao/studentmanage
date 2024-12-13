package com.studentmanage.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentmanage.controller.ClassStatisticsController;
import com.studentmanage.entity.Student;
import com.studentmanage.mapper.StudentMapper;
import com.studentmanage.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
@Service
public class StudentServiceImpl implements StudentService {


    @Autowired
    private StudentMapper studentMapper;

    @Value("${app.data.file:students.json}")
    private String dataFile;

    private ObjectMapper objectMapper = new ObjectMapper();

    public boolean saveToFile() {
        try {
            // 获取所有学生数据
            List<Student> allStudents = studentMapper.selectAll();

            // 创建文件
            File file = new File(dataFile);

            // 写入JSON数据
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, allStudents);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public List<Student> sortByStudentId() {
        List<Student> students = studentMapper.selectAll();
        if (students == null) {
            throw new RuntimeException("Failed to get students");
        }else {
            return students;
        }
    }

    @Override
    public Student findByStudentId(String studentId) {
        Student student = studentMapper.selectByStudentId(studentId);
        if (student == null) {
            throw new RuntimeException("Failed to get student");
        }else {
            return student;
        }
    }


    @Override
    public void updateStudent(Student student) {
        // 检查学生是否存在
        Student existingStudent = studentMapper.selectByStudentId(student.getStudentId());
        if (existingStudent == null) {
            throw new RuntimeException("Student not found");
        }

        // 执行更新
        studentMapper.updateByStudentId(student);
    }

    @Override
    public boolean deleteStudent(String studentId) {
        // 检查学生是否存在
        Student existingStudent = studentMapper.selectByStudentId(studentId);
        if (existingStudent == null) {
            throw new RuntimeException("Student not found");
        }

        // 执行删除
        studentMapper.deleteByStudentId(studentId);
        return true;
    }


    @Override
    public List<Student> findAllStudents() {
        List<Student> students = studentMapper.selectAll();
        if (students == null) {
            throw new RuntimeException("Failed to get students");
        }else {
            return students;
        }
    }

    @Override
    public void saveStudent(Student newStudent) {
        studentMapper.insert(newStudent);
    }

    @Override
    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    @Override
    public int getTotalCount() {
        return studentMapper.getTotalCount();
    }

    @Override
    public List<Student> advancedSearch(String studentId, String name, String gender, String className) {
        List<Student> students = studentMapper.advancedSearch(studentId,name,gender,className);
        if (students == null) {
            throw new RuntimeException("Failed to get students");
        }else {
            return students;
        }
    }

    @Override
    public List<ClassStatisticsController.ClassStatistics> getClassStatistics() {
        try {
            List<Map<String, Object>> rawStats = studentMapper.getClassStatistics();
            int totalCount = getTotalCount();

            List<ClassStatisticsController.ClassStatistics> statistics = new ArrayList<>();

            for (Map<String, Object> stat : rawStats) {
                String className = (String) stat.get("class_name");
                int count = ((Number) stat.get("count")).intValue();
                double percentage = (count * 100.0) / totalCount;

                statistics.add(new ClassStatisticsController.ClassStatistics(
                        className, count, percentage
                ));
            }

            return statistics;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
