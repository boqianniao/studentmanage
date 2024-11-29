package com.studentmanage.controller;

import com.studentmanage.entity.Student;
import com.studentmanage.service.StudentService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ALL")
@Component
@Controller
public class MainController {

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> studentIdColumn;

    @FXML
    private TableColumn<Student, String> nameColumn;

    @FXML
    private TableColumn<Student, String> genderColumn;

    @FXML
    private TableColumn<Student, String> idCardColumn;

    @FXML
    private TableColumn<Student, String> birthdayColumn;

    @FXML
    private TableColumn<Student, String> classNameColumn;

    @FXML
    private TableColumn<Student, String> phoneColumn;

    @FXML
    private ComboBox<String> searchTypeComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Label totalCountLabel;

    @Autowired
    private StudentService studentService;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 初始化表格列
        initializeColumns();
        // 绑定数据
        studentTable.setItems(studentList);
        //统计总人数
        updateTotalCount();
    }


    /**
     * 保存到文件中
     */
    @FXML
    public void handleSave() {
        try {
            // 显示文件选择器
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存学生数据");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON文件", "*.json"),
                    new FileChooser.ExtensionFilter("所有文件", "*.*")
            );

            // 获取当前窗口
            Window window = studentTable.getScene().getWindow();

            // 显示保存对话框
            File file = fileChooser.showSaveDialog(window);

            if (file != null) {
                // 更新配置中的文件路径
                studentService.setDataFile(file.getAbsolutePath());

                // 执行保存
                if (studentService.saveToFile()) {
                    // 显示成功消息
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("成功");
                    alert.setHeaderText(null);
                    alert.setContentText("数据已成功保存到文件！");
                    alert.showAndWait();
                } else {
                    throw new Exception("保存失败");
                }
            }

        } catch (Exception e) {
            // 显示错误消息
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("保存失败");
            alert.setContentText("无法保存数据到文件：" + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * 退出应用
     */
    @FXML
    public void handleExit() {
        // 显示确认对话框
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("确认退出");
        confirmDialog.setHeaderText("退出应用");
        confirmDialog.setContentText("是否要保存数据后退出？");

        // 自定义按钮
        ButtonType buttonTypeSaveAndExit = new ButtonType("保存并退出");
        ButtonType buttonTypeExit = new ButtonType("直接退出");
        ButtonType buttonTypeCancel = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmDialog.getButtonTypes().setAll(buttonTypeSaveAndExit, buttonTypeExit, buttonTypeCancel);

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent()) {
            if (result.get() == buttonTypeSaveAndExit) {
                // 保存数据后退出
                try {
                    if (studentService.saveToFile()) {
                        Platform.exit();
                    } else {
                        // 保存失败，询问是否仍要退出
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                                "保存数据失败，是否仍要退出？",
                                ButtonType.YES, ButtonType.NO);
                        errorAlert.setTitle("保存失败");
                        if (errorAlert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                            Platform.exit();
                        }
                    }
                } catch (Exception e) {
                    // 发生异常，询问是否仍要退出
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR,
                            "保存数据时发生错误：" + e.getMessage() + "\n是否仍要退出？",
                            ButtonType.YES, ButtonType.NO);
                    errorAlert.setTitle("错误");
                    if (errorAlert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                        Platform.exit();
                    }
                }
            } else if (result.get() == buttonTypeExit) {
                // 直接退出
                Platform.exit();
            }
        }
    }

    /**
     * 添加学生
     */
    @FXML
    public void showAddStudent() {
        try {
            // 加载添加学生对话框的FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addStudent.fxml"));
            DialogPane dialogPane = loader.load();

            // 创建对话框
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("添加学生");
            dialog.setDialogPane(dialogPane);

            // 显示对话框并等待用户响应
            Optional<ButtonType> clickedButton = dialog.showAndWait();

            // 如果用户点击了确定按钮
            if (clickedButton.isPresent() && clickedButton.get() == ButtonType.OK) {
                AddStudentController controller = loader.getController();
                Student newStudent = controller.getStudent();

                // 添加学生到列表
                studentList.add(newStudent);
                // 保存到服务层
                studentService.saveStudent(newStudent);
                // 更新总人数显示
                updateTotalCount();
            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("无法打开添加学生对话框");
            alert.setContentText("错误信息: " + e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * 编辑学生
     */
    @FXML
    public void showEditStudent() {
        // 获取选中的学生
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("警告");
            alert.setHeaderText("未选择学生");
            alert.setContentText("请先在表格中选择要编辑的学生。");
            alert.showAndWait();
            return;
        }

        try {
            // 加载编辑对话框
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/exitStudent.fxml"));
            DialogPane dialogPane = loader.load();

            // 创建对话框
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("编辑学生信息");
            dialog.setDialogPane(dialogPane);

            // 获取控制器并设置学生数据
            EditStudentController controller = loader.getController();
            controller.setStudent(selectedStudent);

            // 显示对话框并等待用户响应
            Optional<ButtonType> clickedButton = dialog.showAndWait();

            // 如果用户点击了确定按钮
            if (clickedButton.isPresent() && clickedButton.get() == ButtonType.OK) {
                // 获取更新后的学生信息
                Student updatedStudent = controller.getUpdatedStudent();

                // 更新数据库
                studentService.updateStudent(updatedStudent);

                // 更新表格显示
                int selectedIndex = studentTable.getSelectionModel().getSelectedIndex();
                studentList.set(selectedIndex, updatedStudent);

                // 显示成功消息
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("成功");
                alert.setHeaderText(null);
                alert.setContentText("学生信息更新成功！");
                alert.showAndWait();
            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("无法打开编辑对话框");
            alert.setContentText("错误信息: " + e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * 删除学生
     */
    @FXML
    public void showDeleteStudent() {
        // 获取选中的学生
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("警告");
            alert.setHeaderText("未选择学生");
            alert.setContentText("请先在表格中选择要删除的学生。");
            alert.showAndWait();
            return;
        }

        // 显示确认对话框
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("确认删除");
        confirmDialog.setHeaderText("删除学生信息");
        confirmDialog.setContentText(String.format("确定要删除学号为 %s 的学生 %s 吗？",
                selectedStudent.getStudentId(), selectedStudent.getName()));

        Optional<ButtonType> result = confirmDialog.showAndWait();

        // 如果用户点击了确定按钮
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 执行删除操作
                if (studentService.deleteStudent(selectedStudent.getStudentId())) {
                    // 从表格中移除
                    studentList.remove(selectedStudent);
                    // 更新总人数显示
                    updateTotalCount();

                    // 显示成功消息
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("成功");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("学生信息已成功删除！");
                    successAlert.showAndWait();
                } else {
                    // 显示错误消息
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("错误");
                    errorAlert.setHeaderText("删除失败");
                    errorAlert.setContentText("无法删除学生信息，请稍后重试。");
                    errorAlert.showAndWait();
                }
            } catch (Exception e) {
                // 显示异常错误
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("错误");
                errorAlert.setHeaderText("删除失败");
                errorAlert.setContentText("发生错误：" + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }


    /**
     * 根据学生学号查询
     */
    @FXML
    public void searchByStudentId() {
        // 创建对话框
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("按学号查询");
        dialog.setHeaderText("请输入学号");
        dialog.setContentText("学号:");

        // 显示对话框并等待输入
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String studentId = result.get().trim();

            if (studentId.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("警告");
                alert.setHeaderText(null);
                alert.setContentText("学号不能为空！");
                alert.showAndWait();
                return;
            }

            try {
                // 查询学生
                Student student = studentService.findByStudentId(studentId);

                if (student != null) {
                    // 清空当前表格选择
                    studentTable.getSelectionModel().clearSelection();

                    // 在表格中查找并选中该学生
                    for (int i = 0; i < studentList.size(); i++) {
                        if (studentList.get(i).getStudentId().equals(studentId)) {
                            studentTable.getSelectionModel().select(i);
                            studentTable.scrollTo(i);
                            return;
                        }
                    }

                    // 如果表格中没有，则从数据库加载
                    studentList.add(student);
                    studentTable.getSelectionModel().select(student);
                    studentTable.scrollTo(student);

                    updateTotalCount();

                } else {
                    // 未找到学生
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("查询结果");
                    alert.setHeaderText(null);
                    alert.setContentText("未找到学号为 " + studentId + " 的学生。");
                    alert.showAndWait();
                }

            } catch (Exception e) {
                // 查询出错
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText("查询失败");
                alert.setContentText("查询过程中发生错误：" + e.getMessage());
                alert.showAndWait();
            }
        }
    }


    /**
     * 高级搜索
     */
    @FXML
    public void showAdvancedSearch() {
        try {
            // 加载高级搜索对话框
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/advancedsearch.fxml"));
            DialogPane dialogPane = loader.load();

            // 创建对话框
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("高级搜索");
            dialog.setDialogPane(dialogPane);

            // 显示对话框并等待用户响应
            Optional<ButtonType> clickedButton = dialog.showAndWait();

            // 如果用户点击了确定按钮
            if (clickedButton.isPresent() && clickedButton.get() == ButtonType.OK) {
                AdvancedSearchController controller = loader.getController();

                // 获取搜索条件
                String studentId = controller.getStudentId();
                String name = controller.getName();
                String gender = controller.getGender();
                String className = controller.getClassName();

                // 执行搜索
                List<Student> results = studentService.advancedSearch(studentId, name, gender, className);

                // 更新表格显示
                studentList.clear();
                studentList.addAll(results);

                updateTotalCount();


                // 显示结果数量
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("搜索结果");
                alert.setHeaderText(null);
                alert.setContentText(String.format("找到 %d 条匹配的记录。", results.size()));
                alert.showAndWait();
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("搜索失败");
            alert.setContentText("执行搜索时发生错误：" + e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * 显示班级统计
     */
    @FXML
    public void showClassStatistics() {
        try {
            // 加载统计对话框
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/classStatistics.fxml"));
            DialogPane dialogPane = loader.load();

            // 创建对话框
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("班级统计");
            dialog.setDialogPane(dialogPane);

            // 获取统计数据
            List<ClassStatisticsController.ClassStatistics> statistics =
                    studentService.getClassStatistics();
            int totalCount = studentService.getTotalCount();

            // 设置数据
            ClassStatisticsController controller = loader.getController();
            controller.setStatistics(statistics, totalCount);

            // 显示对话框
            dialog.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("统计失败");
            alert.setContentText("生成统计信息时发生错误：" + e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * 刷新表格
     */
    @FXML
    public void refreshTable() {
        // 刷新表格数据
            studentList.clear();

            List<Student> students = studentService.findAllStudents();

            studentList.addAll(students);

            updateTotalCount();
    }


    /**
     * 提交搜索
     */
    @FXML
    public void handleSearch() {
        String searchType = searchTypeComboBox.getValue();
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("警告");
            alert.setHeaderText(null);
            alert.setContentText("请输入搜索关键词！");
            alert.showAndWait();
            return;
        }

        try {
            List<Student> results;

            // 根据不同的搜索类型执行不同的搜索
            switch (searchType) {
                case "学号":
                    results = studentService.advancedSearch(keyword, null, null, null);
                    break;
                case "姓名":
                    results = studentService.advancedSearch(null, keyword, null, null);
                    break;
                case "班级":
                    results = studentService.advancedSearch(null, null, null, keyword);
                    break;
                default:
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText(null);
                    alert.setContentText("请选择搜索类型！");
                    alert.showAndWait();
                    return;
            }

            // 更新表格显示
            studentList.clear();
            studentList.addAll(results);

            updateTotalCount();

            // 显示结果数量
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("搜索结果");
            alert.setHeaderText(null);
            alert.setContentText(String.format("找到 %d 条匹配的记录。", results.size()));
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("搜索失败");
            alert.setContentText("执行搜索时发生错误：" + e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * 提交排序
     */
    @FXML
    public void handleSort() {
        try {
            // 获取排序后的数据
            List<Student> sortedStudents = studentService.sortByStudentId();

            // 更新表格显示
            studentList.clear();
            studentList.addAll(sortedStudents);

            // 显示成功消息
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("排序完成");
            alert.setHeaderText(null);
            alert.setContentText("学生信息已按学号排序。");
            alert.showAndWait();

        } catch (Exception e) {
            // 显示错误消息
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("排序失败");
            alert.setContentText("排序过程中发生错误：" + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * 更新总人数显示
     */
    private void updateTotalCount() {
        try {
            // 获取当前表格中的学生数量
            int totalCount = studentList.size();
            // 更新标签显示
            totalCountLabel.setText(String.format("总人数：%d", totalCount));
        } catch (Exception e) {
            // 发生错误时显示为0
            totalCountLabel.setText("总人数：0");
            e.printStackTrace();
        }
    }

    /**
     * 初始化表格列
     */
    private void initializeColumns() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        idCardColumn.setCellValueFactory(new PropertyValueFactory<>("idNumber"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        refreshTable();
    }


}