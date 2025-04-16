/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.bttb;

import com.bttb.pojo.MaintenanceSchedule;
import com.bttb.services.ScheduleServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author nhanh
 */
//public class DashboardController implements Initializable {
/**
 * Initializes the controller class.
 */
//}    
public class DashboardController implements Initializable {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button btnDevice, btnSchedule, btnRepairHistory, btnLogout, btnAddTechnician;

    @FXML
    private Button btnMaintenanceAlert;

    private List<MaintenanceSchedule> upcomingSchedules = new ArrayList<>();

    private ScheduleServices ss = new ScheduleServices();

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);

            // Căn giữa view trong StackPane
            StackPane.setAlignment(view, Pos.CENTER);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDeviceManagement(ActionEvent event) {
        loadView("/com/bttb/bttb/device_management.fxml");
    }

    @FXML
    private void openMaintenanceSchedule(ActionEvent event) {
        loadView("/com/bttb/bttb/maintenance_schedule.fxml");
    }

    @FXML
    private void openRepairHistory(ActionEvent event) {
        loadView("/com/bttb/bttb/repair_history.fxml");
    }

    @FXML
    private void openAddTechnician(ActionEvent event) {
        loadView("/com/bttb/bttb/add_technician.fxml");
    }

    @FXML
    private void btnLogout(ActionEvent event) {
        // TODO: Xử lý đăng xuất ở đây nếu cần
        System.out.println("Đăng xuất");
    }

    @FXML
    private void showUpcomingMaintenanceInfo(ActionEvent event) {
        if (upcomingSchedules.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo bảo trì");
            alert.setHeaderText("Không có thiết bị cần bảo trì");
            alert.setContentText("Chưa có thiết bị nào cần bảo trì trong 10 ngày tới.");
            alert.show();
        } else {
            MaintenanceScheduleController.showUpcomingMaintenance(upcomingSchedules, 10);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ObservableList<MaintenanceSchedule> schedules = ss.getAllSchedules();
            if (schedules != null && !schedules.isEmpty()) {
                upcomingSchedules = MaintenanceScheduleController.getUpcomingSchedules(schedules, 10);

                if (!upcomingSchedules.isEmpty()) {
                    btnMaintenanceAlert.setText("Bảo trì (" + upcomingSchedules.size() + ")");
                } else {
                    btnMaintenanceAlert.setText("Bảo trì (0)");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }

        openDeviceManagement(null);
    }
}
