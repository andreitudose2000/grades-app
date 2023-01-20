package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Semester;

public class SemesterResponseDto {
    private Integer id;
    private Integer number;

    public SemesterResponseDto fromSemester(Semester semester) {

        setId(semester.getId());
        setNumber(semester.getNumber());

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
}
