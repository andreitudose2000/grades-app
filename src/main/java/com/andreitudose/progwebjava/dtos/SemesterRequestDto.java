package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Semester;
import jakarta.validation.constraints.Min;

public class SemesterRequestDto {
    @Min(1)
    private Integer number;

    public Semester toSemester(Semester semester) {
        semester.setNumber(this.getNumber());

        return semester;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
