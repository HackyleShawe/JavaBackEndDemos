package com.ks.demo.wh.calendar.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class CalendarServiceTest {

    @Autowired
    private CalendarService calendarService;

    @Test
    public void testHoliday_1() {
        boolean holiday = calendarService.isHoliday();
        System.out.println(holiday);
        holiday = calendarService.isHoliday(LocalDate.of(2026,1,22));
        System.out.println(holiday);

        holiday = calendarService.isHoliday(LocalDate.of(2026,1,1));
        System.out.println(holiday);
        holiday = calendarService.isHoliday(LocalDate.of(2026,2,15));
        System.out.println(holiday);
    }

    @Test
    public void testNextHoliday_1() {
        LocalDate localDate = calendarService.nextHoliday();
        System.out.println("===="+localDate);
        localDate = calendarService.nextHoliday(LocalDate.of(2026,1,23));
        System.out.println("===="+localDate);
        localDate = calendarService.nextHoliday(LocalDate.of(2026,1,23), 1);
        System.out.println("===="+localDate);

        localDate = calendarService.nextHoliday(LocalDate.of(2026,1,1));
        System.out.println("===="+localDate);
        localDate = calendarService.nextHoliday(LocalDate.of(2026,1,1), 1);
        System.out.println("===="+localDate);

        localDate = calendarService.nextHoliday(LocalDate.of(2026,2,15));
        System.out.println("===="+localDate);
        localDate = calendarService.nextHoliday(LocalDate.of(2026,2,15),2);
        System.out.println("===="+localDate);
    }

    @Test
    public void testPreviousHoliday() {
        LocalDate localDate = calendarService.previousHoliday();
        System.out.println("===="+localDate);
        localDate = calendarService.previousHoliday(LocalDate.of(2026,1,21));
        System.out.println("===="+localDate);

        localDate = calendarService.previousHoliday(LocalDate.of(2026,1,21), 1);
        System.out.println("===="+localDate);

        //localDate = calendarService.previousHoliday(LocalDate.of(2026,1,1));
        //System.out.println("===="+localDate);
        //localDate = calendarService.previousHoliday(LocalDate.of(2026,1,1), 2);
        //System.out.println("===="+localDate);

        localDate = calendarService.previousHoliday(LocalDate.of(2026,2,15));
        System.out.println("===="+localDate);
        localDate = calendarService.previousHoliday(LocalDate.of(2026,2,15),2);
        System.out.println("===="+localDate);
    }

    @Test
    public void testCountHoliday() {
        int holidays = calendarService.countHolidays();
        System.out.println("=============" + holidays);


        holidays = calendarService.countHolidays(LocalDate.of(2026,1,1), LocalDate.of(2026,2,15));
        System.out.println("=============" + holidays);

        holidays = calendarService.countHolidays(LocalDate.of(2026,1,1), LocalDate.of(2027,2,15));
        System.out.println("=============" + holidays);
    }

    @Test
    public void testWorkday() {
        boolean workday = calendarService.isWorkday();
        System.out.println("========="+workday);
        workday = calendarService.isWorkday(LocalDate.of(2026,1,23));
        System.out.println("========="+workday);

        workday = calendarService.isWorkday(LocalDate.of(2026,1,1));
        System.out.println("========="+workday);

        workday = calendarService.isWorkday(LocalDate.of(2026,2,15));
        System.out.println("========="+workday);

    }

    @Test
    public void testNextWorkday() {
        LocalDate localDate = calendarService.nextWorkday();
        System.out.println("===="+localDate);
        localDate = calendarService.nextWorkday(LocalDate.of(2026,1,23));
        System.out.println("===="+localDate);

        localDate = calendarService.nextWorkday(LocalDate.of(2026,1,24), 2);
        System.out.println("===="+localDate);

        localDate = calendarService.nextWorkday(LocalDate.of(2026,1,1));
        System.out.println("===="+localDate);
        localDate = calendarService.nextWorkday(LocalDate.of(2026,1,1), 1);
        System.out.println("===="+localDate);

        localDate = calendarService.nextWorkday(LocalDate.of(2026,2,15));
        System.out.println("===="+localDate);
        localDate = calendarService.nextWorkday(LocalDate.of(2026,2,15),2);
        System.out.println("===="+localDate);
    }

    @Test
    public void testPreviousWorkday() {
        LocalDate localDate = calendarService.previousWorkday();
        System.out.println("===="+localDate);
        localDate = calendarService.previousWorkday(LocalDate.of(2026,1,21));
        System.out.println("===="+localDate);

        localDate = calendarService.previousWorkday(LocalDate.of(2026,1,21), 1);
        System.out.println("===="+localDate);

        localDate = calendarService.previousWorkday(LocalDate.of(2026,1,1));
        System.out.println("===="+localDate);
        localDate = calendarService.previousWorkday(LocalDate.of(2026,1,1), 1);
        System.out.println("===="+localDate);

        localDate = calendarService.previousWorkday(LocalDate.of(2026,2,15));
        System.out.println("===="+localDate);
        localDate = calendarService.previousWorkday(LocalDate.of(2026,2,15),2);
        System.out.println("===="+localDate);
    }

    @Test
    public void testCountWorkday() {
        int workdays = calendarService.countWorkdays();
        System.out.println("=============" + workdays);


        workdays = calendarService.countWorkdays(LocalDate.of(2026,1,1), LocalDate.of(2026,2,15));
        System.out.println("=============" + workdays);

        workdays = calendarService.countWorkdays(LocalDate.of(2026,1,1), LocalDate.of(2027,2,15));
        System.out.println("=============" + workdays);
    }

}
