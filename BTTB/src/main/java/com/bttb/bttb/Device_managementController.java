/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.bttb;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author nhanh
 */
public class Device_managementController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private ComboBox<String> statusFilter;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
            "Tất cả", "Đang hoạt động", "Hỏng hóc", "Đang sửa chữa", "Đã thanh lý"
        );
        statusFilter.setItems(statusOptions);
    }

}
