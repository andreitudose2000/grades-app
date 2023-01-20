package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Programme;
import jakarta.validation.constraints.Size;

public class ProgrammeRequestDto {
    @Size(min = 1, max = 100)
    private String name;

    public Programme toProgramme(Programme programme) {
        programme.setName(this.getName());

        return programme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
