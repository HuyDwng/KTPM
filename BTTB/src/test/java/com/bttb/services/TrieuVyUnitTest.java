/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.services;



import com.bttb.bttb.Device_managementController;
import com.bttb.bttb.MaintenanceScheduleController;
import com.bttb.pojo.Device;
import com.bttb.pojo.DeviceType;
import com.bttb.pojo.MaintenanceSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 *
 * @author nhanh
 */
public class TrieuVyUnitTest {
    static {
    try {
        javafx.application.Platform.startup(() -> {});
    } catch (IllegalStateException e) {
        // JavaFX already initialized
    }
}
    private Device_managementController controller;

    @BeforeEach
    void setUp() {
        controller = new Device_managementController();

        // Giả lập danh sách thiết bị
        ObservableList<Device> allDevices = FXCollections.observableArrayList(
                new Device(1, "PC Lab 1", "Đang hoạt động"),
                new Device(2, "Máy in HP", "Hỏng hóc"),
                new Device(3, "Laptop Dell", "Đang hoạt động"),
                new Device(4, "Máy chiếu Epson", "Bảo trì")
        );
        
        // Gán vào controller
        controller.allDeviceList = allDevices;
        controller.filteredDeviceList = FXCollections.observableArrayList();
        controller.deviceTable = new TableView<>();

    }

    @Test
    void testAddDevice() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        DeviceType deviceType = new DeviceType(1, "Máy tính");
        Device device = new Device("PC Lab 1", "Đang hoạt động", deviceType.getId());
        when(deviceServices.addDevice(any(Device.class))).thenReturn(true);

        boolean result = deviceServices.addDevice(device);
        assertTrue(result);
    }

    @Test
    void testDeleteDevice() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        when(deviceServices.deleteDevice(10)).thenReturn(true);

        boolean result = deviceServices.deleteDevice(10);
        assertTrue(result);
    }

    @Test
    void testUpdateDeviceStatus() {
        DeviceServices deviceServices = mock(DeviceServices.class);
        when(deviceServices.updateDeviceStatus(5, "Hỏng hóc")).thenReturn(true);

        boolean result = deviceServices.updateDeviceStatus(5, "Hỏng hóc");
        assertTrue(result);
    }

//    @Test
//    void testShowUpcomingMaintenance() {
//        MaintenanceSchedule ms = new MaintenanceSchedule(
//                1,
//                "PC Lab 1",
//                "Nguyễn Văn A",
//                LocalDate.now().plusDays(2),
//                LocalTime.of(10, 0),
//                "Hàng tháng",
//                LocalDate.now().plusDays(30),
//                LocalDate.now(),
//                null
//                          
//        );
//        List<MaintenanceSchedule> schedules = List.of(ms);
//
//        assertDoesNotThrow(() -> MaintenanceScheduleController.showUpcomingMaintenance(schedules, 7));
//    }

    @Test
    void testFilterDevicesByName() {
        controller.filterDevices("pc", "Tất cả");
        List<Device> result = controller.filteredDeviceList;
        assertEquals(1, result.size());
        assertEquals("PC Lab 1", result.get(0).getName());
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
}
