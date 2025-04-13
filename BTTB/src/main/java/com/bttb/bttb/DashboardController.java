/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.bttb;

import com.bttb.pojo.MaintenanceSchedule;
import com.bttb.services.ScheduleServices;
import java.net.URL;
import java.sql.SQLException;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author nhanh
 */
public class DashboardController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private ScheduleServices ss = new ScheduleServices();
    
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<MaintenanceSchedule> schedules = null;
        try {
            schedules = ss.getAllSchedules();
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        MaintenanceScheduleController.showUpcomingMaintenance(schedules, 7);
    }    
    
}
