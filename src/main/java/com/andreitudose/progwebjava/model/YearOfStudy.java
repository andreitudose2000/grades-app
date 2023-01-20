package com.andreitudose.progwebjava.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="yearsOfStudy")
public class YearOfStudy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Programme programme;

    @Min(value = 1)
    private Integer number;

    @Min(value = 1900)
    @Max(value = 2100)
    private int calendarYearOfStart;


    @Min(value = 1900)
    @Max(value = 2100)
    private int calendarYearOfEnd;

    @OneToMany(mappedBy="yearOfStudy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Semester> semesters = new HashSet<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Programme getProgramme() {
        return programme;
    }

    public void setProgramme(Programme programme) {
        this.programme = programme;
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

    public Set<Semester> getSemesters() {
        return semesters;
    }

    public void setSemesters(Set<Semester> semesters) {
        this.semesters = semesters;
    }
}
