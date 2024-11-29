package com.studentmanage.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ClassStatisticsController {
    
    @FXML private TableView<ClassStatistics> statisticsTable;
    @FXML private TableColumn<ClassStatistics, String> classNameColumn;
    @FXML private TableColumn<ClassStatistics, Integer> studentCountColumn;
    @FXML private TableColumn<ClassStatistics, String> percentageColumn;
    @FXML private Label totalLabel;
    
    private ObservableList<ClassStatistics> statisticsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        studentCountColumn.setCellValueFactory(new PropertyValueFactory<>("studentCount"));
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));

        // 设置表格排序
        classNameColumn.setSortType(TableColumn.SortType.ASCENDING);
        studentCountColumn.setSortType(TableColumn.SortType.DESCENDING);

        // 允许表格排序
        statisticsTable.getSortOrder().add(classNameColumn);

        statisticsTable.setItems(statisticsList);
    }
    
    public void setStatistics(List<ClassStatistics> statistics, int totalCount) {
        statisticsList.clear();
        statisticsList.addAll(statistics);
        totalLabel.setText(String.format("总人数：%d", totalCount));
    }
    
    public static class ClassStatistics {
        private String className;
        private int studentCount;
        private String percentage;
        
        public ClassStatistics(String className, int studentCount, double percentage) {
            this.className = className;
            this.studentCount = studentCount;
            this.percentage = String.format("%.2f%%", percentage);
        }
        
        // Getters
        public String getClassName() { return className; }
        public int getStudentCount() { return studentCount; }
        public String getPercentage() { return percentage; }
    }
}