package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Student;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StudentDetailedResponseDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<ProgrammeResponseDto> programmes = new HashSet<>();

    public StudentDetailedResponseDto fromStudent(Student student) {

        setId(student.getId());
        setFirstName(student.getFirstName());
        setLastName(student.getLastName());
        setEmail(student.getEmail());
        setProgrammes(
                student.getProgrammes().stream()
                        .map(x -> new ProgrammeResponseDto().fromProgramme(x))
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<ProgrammeResponseDto> getProgrammes() {
        return programmes;
    }

    public void setProgrammes(Set<ProgrammeResponseDto> programmes) {
        this.programmes = programmes;
    }
}
