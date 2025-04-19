package com.bttb.controller;

import com.bttb.pojo.Device;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DeviceManagementControllerTest {

    static {
        try {
            javafx.application.Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {

        }
    }

    private DeviceManagementController controller;

    @BeforeEach
    void setUp() {
        controller = new DeviceManagementController();
        ObservableList<Device> allDevices = FXCollections.observableArrayList(
                new Device(1, "PC Lab 1", "Đang hoạt động"),
                new Device(2, "Máy in HP", "Hỏng hóc"),
                new Device(3, "Laptop Dell", "Đang hoạt động"),
                new Device(4, "Máy chiếu Epson", "Bảo trì")
        );
        controller.allDeviceList = allDevices;
        controller.filteredDeviceList = FXCollections.observableArrayList();
        controller.deviceTable = new TableView<>();
    }

    @Test
    void testFilterDevicesByName() {
        controller.filterDevices("pc", "Tất cả");
        List<Device> result = controller.filteredDeviceList;
        assertEquals(1, result.size());
        assertEquals("PC Lab 1", result.get(0).getName());
    }

    @Test
    void testFilterDevicesByNameCaseInsensitive() {
        controller.filterDevices("lApToP", "Tất cả");
        List<Device> result = controller.filteredDeviceList;
        assertEquals(1, result.size());
        assertEquals("Laptop Dell", result.get(0).getName());
    }

    @Test
    void testFilterDevicesByStatus() {
        controller.filterDevices("", "Đang hoạt động");
        List<Device> result = controller.filteredDeviceList;
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> d.getStatus().equals("Đang hoạt động")));
    }

    @Test
    void testFilterDevicesByNameAndStatus() {
        controller.filterDevices("laptop", "Đang hoạt động");
        List<Device> result = controller.filteredDeviceList;
        assertEquals(1, result.size());
        assertEquals("Laptop Dell", result.get(0).getName());
    }

    @Test
    void testFilterDevicesNoResult() {
        controller.filterDevices("Không tồn tại", "Tất cả");
        List<Device> result = controller.filteredDeviceList;
        assertEquals(0, result.size());
    }

    @Test
    void testFilterDevicesEmptyKeyword() {
        controller.filterDevices("", "Tất cả");
        List<Device> result = controller.filteredDeviceList;
        assertEquals(4, result.size());
    }

    @Test
    void testFilterDevicesInvalidStatus() {
        controller.filterDevices("", "Không tồn tại");
        List<Device> result = controller.filteredDeviceList;
        assertEquals(0, result.size());
    }

    @Test
    void testFilterDevicesNameAndStatusNoMatch() {
        controller.filterDevices("PC", "Bảo trì");
        List<Device> result = controller.filteredDeviceList;
        assertEquals(0, result.size());
    }
}
