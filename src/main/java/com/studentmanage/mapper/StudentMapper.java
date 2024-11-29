package com.studentmanage.mapper;

import com.studentmanage.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {


    @Select("select * from student order by student_id")
    List<Student> selectAll();

    @Delete("delete from student where student_id = #{studentId}")
    void deleteByStudentId(String studentId);

    @Update("update student set name=#{name}, gender=#{gender}, id_number=#{idNumber}, " +
            "class_name=#{className}, phone_number=#{phoneNumber} " +
            "where student_id=#{studentId}")
    void updateByStudentId(Student student);

    @Select("select * from student where student_id = #{studentId}")
    Student selectByStudentId(String studentId);

    @Insert("insert into student(student_id, name,gender,id_number,birth_date,class_name,phone_number)" +
            "        values(#{studentId},#{name},#{gender},#{idNumber},#{birthDate},#{className},#{phoneNumber})")
    void insert(Student student);

    @Select("select count(*) from student")
    int getTotalCount();

    List<Student> advancedSearch(String studentId, String name, String gender, String className);

    @Select("SELECT class_name, COUNT(*) as count FROM student GROUP BY class_name ORDER BY class_name")
    List<Map<String, Object>> getClassStatistics();
}
