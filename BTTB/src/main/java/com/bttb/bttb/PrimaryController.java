package com.bttb.bttb;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    
    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        ObservableList<String> roles = FXCollections.observableArrayList("Admin", "Technician");
        roleComboBox.setItems(roles);
    }
}
