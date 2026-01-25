package com.ks.demo.wh.holiday.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class HolidayServiceTest {

    @Autowired
    private HolidayService holidayService;

    @Test
    public void testHoliday_1() {
        boolean holiday = holidayService.isHoliday();
        System.out.println(holiday);
        holiday = holidayService.isHoliday(LocalDate.of(2026,1,21));
        System.out.println(holiday);

        holiday = holidayService.isHoliday(LocalDate.of(2026,1,1));
        System.out.println(holiday);
        holiday = holidayService.isHoliday(LocalDate.of(2026,2,15));
        System.out.println(holiday);
    }

    @Test
    public void testNextHoliday_1() {
        LocalDate localDate = holidayService.nextHoliday();
        System.out.println("===="+localDate);
        localDate = holidayService.nextHoliday(LocalDate.of(2026,1,21));
        System.out.println("===="+localDate);

        localDate = holidayService.nextHoliday(LocalDate.of(2026,1,21), 1);
        System.out.println("===="+localDate);

        localDate = holidayService.nextHoliday(LocalDate.of(2026,1,1));
        System.out.println("===="+localDate);
        localDate = holidayService.nextHoliday(LocalDate.of(2026,1,1), 1);
        System.out.println("===="+localDate);

        localDate = holidayService.nextHoliday(LocalDate.of(2026,2,15));
        System.out.println("===="+localDate);
        localDate = holidayService.nextHoliday(LocalDate.of(2026,2,15),2);
        System.out.println("===="+localDate);
    }

    @Test
    public void testPreviousHoliday() {
        LocalDate localDate = holidayService.previousHoliday();
        System.out.println("===="+localDate);
        localDate = holidayService.previousHoliday(LocalDate.of(2026,1,21));
        System.out.println("===="+localDate);

        localDate = holidayService.previousHoliday(LocalDate.of(2026,1,21), 1);
        System.out.println("===="+localDate);

        localDate = holidayService.previousHoliday(LocalDate.of(2026,1,1));
        System.out.println("===="+localDate);
        localDate = holidayService.previousHoliday(LocalDate.of(2026,1,1), 1);
        System.out.println("===="+localDate);

        localDate = holidayService.previousHoliday(LocalDate.of(2026,2,15));
        System.out.println("===="+localDate);
        localDate = holidayService.previousHoliday(LocalDate.of(2026,2,15),2);
        System.out.println("===="+localDate);
    }

    @Test
    public void testCountHoliday() {
        int holidays = holidayService.countHolidays();
        System.out.println("=============" + holidays);


        holidays = holidayService.countHolidays(LocalDate.of(2026,1,1), LocalDate.of(2026,2,15));
        System.out.println("=============" + holidays);

        holidays = holidayService.countHolidays(LocalDate.of(2026,1,1), LocalDate.of(2027,2,15));
        System.out.println("=============" + holidays);
    }

    @Test
    public void testWorkday() {
        boolean workday = holidayService.isWorkday();
        System.out.println("========="+workday);
        workday = holidayService.isWorkday(LocalDate.of(2026,1,23));
        System.out.println("========="+workday);

        workday = holidayService.isWorkday(LocalDate.of(2026,1,1));
        System.out.println("========="+workday);

        workday = holidayService.isWorkday(LocalDate.of(2026,2,15));
        System.out.println("========="+workday);

    }

    @Test
    public void testNextWorkday() {
        LocalDate localDate = holidayService.nextWorkday();
        System.out.println("===="+localDate);
        localDate = holidayService.nextWorkday(LocalDate.of(2026,1,23));
        System.out.println("===="+localDate);

        localDate = holidayService.nextWorkday(LocalDate.of(2026,1,24), 2);
        System.out.println("===="+localDate);

        localDate = holidayService.nextWorkday(LocalDate.of(2026,1,1));
        System.out.println("===="+localDate);
        localDate = holidayService.nextWorkday(LocalDate.of(2026,1,1), 1);
        System.out.println("===="+localDate);

        localDate = holidayService.nextWorkday(LocalDate.of(2026,2,15));
        System.out.println("===="+localDate);
        localDate = holidayService.nextWorkday(LocalDate.of(2026,2,15),2);
        System.out.println("===="+localDate);
    }

    @Test
    public void testPreviousWorkday() {
        LocalDate localDate = holidayService.previousWorkday();
        System.out.println("===="+localDate);
        localDate = holidayService.previousWorkday(LocalDate.of(2026,1,21));
        System.out.println("===="+localDate);

        localDate = holidayService.previousWorkday(LocalDate.of(2026,1,21), 1);
        System.out.println("===="+localDate);

        localDate = holidayService.previousWorkday(LocalDate.of(2026,1,1));
        System.out.println("===="+localDate);
        localDate = holidayService.previousWorkday(LocalDate.of(2026,1,1), 1);
        System.out.println("===="+localDate);

        localDate = holidayService.previousWorkday(LocalDate.of(2026,2,15));
        System.out.println("===="+localDate);
        localDate = holidayService.previousWorkday(LocalDate.of(2026,2,15),2);
        System.out.println("===="+localDate);
    }

    @Test
    public void testCountWorkday() {
        int workdays = holidayService.countWorkdays();
        System.out.println("=============" + workdays);


        workdays = holidayService.countWorkdays(LocalDate.of(2026,1,1), LocalDate.of(2026,2,15));
        System.out.println("=============" + workdays);

        workdays = holidayService.countWorkdays(LocalDate.of(2026,1,1), LocalDate.of(2027,2,15));
        System.out.println("=============" + workdays);
    }

}
