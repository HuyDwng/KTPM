/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.bttb;

//import com.bttb.pojo.MaintenanceSchedule;
//import com.bttb.services.ScheduleServices;
//import java.net.URL;
//import java.sql.SQLException;
//import java.util.ResourceBundle;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javafx.collections.ObservableList;
//import javafx.fxml.Initializable;
import com.bttb.pojo.MaintenanceSchedule;
import com.bttb.pojo.User;
import com.bttb.services.ScheduleServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);

            if (view instanceof AnchorPane) {
                AnchorPane.setTopAnchor(view, 0.0);
                AnchorPane.setBottomAnchor(view, 0.0);
                AnchorPane.setLeftAnchor(view, 0.0);
                AnchorPane.setRightAnchor(view, 0.0);
            }
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

//    @FXML
//    private void initialize() {
//        // Optionally load default view
//        openDeviceManagement(null);
//    }
    @FXML
    private void btnLogout(ActionEvent event) {
        // TODO: Xử lý đăng xuất ở đây nếu cần
        System.out.println("Đăng xuất");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        ScheduleServices ss = new ScheduleServices();
        try {
            ObservableList<MaintenanceSchedule> schedules = ss.getAllSchedules();
            if (schedules != null && !schedules.isEmpty()) {
                MaintenanceScheduleController.showUpcomingMaintenance(schedules, 3);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   

}
