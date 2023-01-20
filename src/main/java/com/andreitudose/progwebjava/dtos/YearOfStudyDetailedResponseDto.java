package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.YearOfStudy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class YearOfStudyDetailedResponseDto {
    private Integer id;
    private Integer number;
    private int calendarYearOfStart;
    private int calendarYearOfEnd;
    private Set<SemesterResponseDto> semesters = new HashSet<>();
    public YearOfStudyDetailedResponseDto fromYearOfStudy(YearOfStudy yearOfStudy) {

        setId(yearOfStudy.getId());
        setNumber(yearOfStudy.getNumber());
        setSemesters(
                yearOfStudy.getSemesters().stream()
                        .map(x -> new SemesterResponseDto().fromSemester(x))
                        .collect(Collectors.toSet())
        );
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

    public Set<SemesterResponseDto> getSemesters() {
        return semesters;
    }

    public void setSemesters(Set<SemesterResponseDto> semesters) {
        this.semesters = semesters;
    }
}
