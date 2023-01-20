package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.YearOfStudy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class YearOfStudyRequestDto {
    @Min(1)
    private Integer number;

    @Min(value = 1900)
    @Max(value = 2100)
    private int calendarYearOfStart;


    @Min(value = 1900)
    @Max(value = 2100)
    private int calendarYearOfEnd;

    public YearOfStudy toYearOfStudy(YearOfStudy yearOfStudy) {
        yearOfStudy.setNumber(this.getNumber());
        yearOfStudy.setCalendarYearOfStart(this.getCalendarYearOfStart());
        yearOfStudy.setCalendarYearOfEnd(this.getCalendarYearOfEnd());

        return yearOfStudy;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public int getCalendarYearOfStart() {
        return calendarYearOfStart;
    }

    public void setCalendarYearOfStart(int calendarYearOfStart) {
        this.calendarYearOfStart = calendarYearOfStart;
    }

    public int getCalendarYearOfEnd() {
        return calendarYearOfEnd;
    }

    public void setCalendarYearOfEnd(int calendarYearOfEnd) {
        this.calendarYearOfEnd = calendarYearOfEnd;
    }
}
