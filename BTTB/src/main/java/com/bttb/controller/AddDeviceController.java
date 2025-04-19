package com.bttb.controller;

import com.bttb.pojo.Device;
import com.bttb.pojo.DeviceType;
import com.bttb.services.DeviceServices;
import com.bttb.services.DeviceTypeServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddDeviceController {

    @FXML
    private TextField txtName;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private ComboBox<DeviceType> typeComboBox;

    private DeviceServices deviceService;
    private DeviceTypeServices deviceTypeServices;

    public void setDeviceManagementController(DeviceManagementController controller) {
        this.deviceService = new DeviceServices(controller);
        this.deviceTypeServices = new DeviceTypeServices();
    }

    public void addDevice() {
        String name = txtName.getText();
        String status = statusComboBox.getValue();
        DeviceType selectedType = typeComboBox.getValue();
        
        if (name.isEmpty() || status == null || selectedType == null) {
            showAlert("Vui lòng nhập đầy đủ thông tin!", Alert.AlertType.ERROR);
            return;
        }
//        System.out.print(selectedType);
//        System.out.print(selectedType.getId());

        Device device = new Device(name, status, selectedType.getId());
        System.out.println(device);
        boolean success = deviceService.addDevice(device);

        if (success) {
            showAlert("Thêm thiết bị thành công!", Alert.AlertType.INFORMATION);
            ((Stage) txtName.getScene().getWindow()).close();

        } else {
            showAlert("Lỗi khi thêm thiết bị!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    public void initialize() {
        // Danh sách trạng thái thiết bị
        ObservableList<String> statusList = FXCollections.observableArrayList("Đang hoạt động", "Hỏng hóc", "Đang sửa");
        this.deviceTypeServices = new DeviceTypeServices();
        statusComboBox.setItems(statusList);

        statusComboBox.setValue("Đang hoạt động");
        
        ObservableList<DeviceType> typeList = FXCollections.observableArrayList(deviceTypeServices.getAllDeviceTypes());
        typeComboBox.setItems(typeList);
    }
}
