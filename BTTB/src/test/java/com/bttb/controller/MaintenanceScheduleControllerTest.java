package com.bttb.controller;

import com.bttb.pojo.MaintenanceSchedule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MaintenanceScheduleControllerTest {

    @Test
    void testGetUpcomingSchedulesAndBuildContent() {
        MaintenanceSchedule ms1 = new MaintenanceSchedule(
                1, "Máy tính", "Nguyễn Hoàng Triệu Vỹ", LocalDate.now().plusDays(2), LocalTime.of(10, 0), "Hàng tháng", LocalDate.now().plusDays(30), LocalDate.now(), null
        );
        MaintenanceSchedule ms2 = new MaintenanceSchedule(
                2, "Máy in ", "Trần Văn Vỹ", LocalDate.now().plusDays(8), LocalTime.of(9, 0), "Hàng tuần", LocalDate.now().plusDays(15), LocalDate.now(), null
        );
        List<MaintenanceSchedule> schedules = List.of(ms1, ms2);

        List<MaintenanceSchedule> upcoming = MaintenanceScheduleController.getUpcomingSchedules(schedules, 7);
        assertEquals(1, upcoming.size());
        assertEquals("Máy tính", upcoming.get(0).getDeviceName());

        String content = MaintenanceScheduleController.buildMaintenanceAlertContent(upcoming);
        assertTrue(content.contains("Máy tính"));
        assertTrue(content.contains("Nguyễn Hoàng Triệu Vỹ"));
    }

    @Test
    void testEmptyScheduleList() {
        List<MaintenanceSchedule> schedules = List.of();
        List<MaintenanceSchedule> upcoming = MaintenanceScheduleController.getUpcomingSchedules(schedules, 7);
        assertTrue(upcoming.isEmpty());
        String content = MaintenanceScheduleController.buildMaintenanceAlertContent(upcoming);
        assertTrue(content.isEmpty() || content.isBlank());
    }

    @Test
    void testAllSchedulesOutOfRange() {
        MaintenanceSchedule ms1 = new MaintenanceSchedule(
                1, "Máy tính", "Nguyễn Hoàng Triệu Vỹ", LocalDate.now().plusDays(20), LocalTime.of(10, 0), "Hàng tháng", LocalDate.now().plusDays(30), LocalDate.now(), null
        );
        MaintenanceSchedule ms2 = new MaintenanceSchedule(
                2, "Máy in ", "Trần Văn Vỹ", LocalDate.now().plusDays(15), LocalTime.of(9, 0), "Hàng tuần", LocalDate.now().plusDays(15), LocalDate.now(), null
        );
        List<MaintenanceSchedule> schedules = List.of(ms1, ms2);
        List<MaintenanceSchedule> upcoming = MaintenanceScheduleController.getUpcomingSchedules(schedules, 7);
        assertTrue(upcoming.isEmpty());
    }

    @Test
    void testMultipleSchedulesSameDay() {
        LocalDate date = LocalDate.now().plusDays(3);
        MaintenanceSchedule ms1 = new MaintenanceSchedule(
                1, "Máy tính", "Nguyễn Hoàng Triệu Vỹ", date, LocalTime.of(10, 0), "Hàng tháng", date.plusDays(30), LocalDate.now(), null
        );
        MaintenanceSchedule ms2 = new MaintenanceSchedule(
                2, "Máy in ", "Trần Văn Vỹ", date, LocalTime.of(9, 0), "Hàng tuần", date.plusDays(15), LocalDate.now(), null
        );
        List<MaintenanceSchedule> schedules = List.of(ms1, ms2);
        List<MaintenanceSchedule> upcoming = MaintenanceScheduleController.getUpcomingSchedules(schedules, 7);
        assertEquals(2, upcoming.size());
    }

    @Test
    void testScheduleInPast() {
        MaintenanceSchedule ms1 = new MaintenanceSchedule(
                1, "Máy tính", "Nguyễn Hoàng Triệu Vỹ", LocalDate.now().minusDays(2), LocalTime.of(10, 0), "Hàng tháng", LocalDate.now().plusDays(30), LocalDate.now(), null
        );
        List<MaintenanceSchedule> schedules = List.of(ms1);
        List<MaintenanceSchedule> upcoming = MaintenanceScheduleController.getUpcomingSchedules(schedules, 7);
        assertTrue(upcoming.isEmpty());
    }

    @Test
    void testScheduleWithNullDate() {
        MaintenanceSchedule ms1 = new MaintenanceSchedule(
                1, "Máy tính", "Nguyễn Hoàng Triệu Vỹ", null, LocalTime.of(10, 0), "Hàng tháng", LocalDate.now().plusDays(30), LocalDate.now(), null
        );
        List<MaintenanceSchedule> schedules = List.of(ms1);
        List<MaintenanceSchedule> upcoming = MaintenanceScheduleController.getUpcomingSchedules(schedules, 7);
        assertTrue(upcoming.isEmpty());
    }
}
