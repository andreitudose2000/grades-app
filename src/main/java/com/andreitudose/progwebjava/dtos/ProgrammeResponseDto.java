package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Programme;

public class ProgrammeResponseDto {
    private Integer id;
    private String name;
    public ProgrammeResponseDto fromProgramme(Programme programme) {

        setId(programme.getId());
        setName(programme.getName());

        return this;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
