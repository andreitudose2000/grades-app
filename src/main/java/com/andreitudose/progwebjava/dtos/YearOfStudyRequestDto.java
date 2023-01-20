package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.YearOfStudy;
import jakarta.validation.constraints.Min;

public class YearOfStudyRequestDto {
    @Min(1)
    private Integer number;

    public YearOfStudy toYearOfStudy(YearOfStudy yearOfStudy) {
        yearOfStudy.setNumber(this.getNumber());

        return yearOfStudy;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
