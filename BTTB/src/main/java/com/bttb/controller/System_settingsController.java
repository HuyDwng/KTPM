/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.bttb.controller;

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
public class System_settingsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private ComboBox<String> logRetention;
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> logOptions = FXCollections.observableArrayList(
            "6 tháng", "1 năm", "Tùy chỉnh"
        );
        logRetention.setItems(logOptions);
    }    
    
}
