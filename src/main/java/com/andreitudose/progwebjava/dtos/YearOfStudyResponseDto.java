package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.YearOfStudy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class YearOfStudyResponseDto {
    private Integer id;
    private Integer number;
    private int calendarYearOfStart;
    private int calendarYearOfEnd;
    public YearOfStudyResponseDto fromYearOfStudy(YearOfStudy yearOfStudy) {

        setId(yearOfStudy.getId());
        setNumber(yearOfStudy.getNumber());
        setCalendarYearOfStart(yearOfStudy.getCalendarYearOfStart());
        setCalendarYearOfEnd(yearOfStudy.getCalendarYearOfEnd());
        return this;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
