/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.bttb;

import com.bttb.pojo.Device;
import com.bttb.services.DeviceServices;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MaintenanceScheduleController implements Initializable{
    @FXML private TableView<Device> tableDevices;
    @FXML private TableColumn<Device, String> colName;
    @FXML private TableColumn<Device, String> colStatus;

    private DeviceServices deviceService = new DeviceServices();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Liên kết cột với thuộc tính trong class Device
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Nạp dữ liệu vào TableView
        loadDevices();
    }    

    private void loadDevices() {
        try {
            List<Device> devices = deviceService.getDevices();
            tableDevices.setItems(FXCollections.observableArrayList(devices));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
