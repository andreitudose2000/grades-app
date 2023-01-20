package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Student;

public class StudentResponseDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;

    public StudentResponseDto fromStudent(Student student) {

        setId(student.getId());
        setFirstName(student.getFirstName());
        setLastName(student.getLastName());
        setEmail(student.getEmail());

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
}
