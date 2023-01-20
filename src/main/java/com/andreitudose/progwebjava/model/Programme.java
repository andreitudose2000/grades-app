package com.andreitudose.progwebjava.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="programmes", uniqueConstraints = { @UniqueConstraint(columnNames = {"student_id", "name"})})
public class Programme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Student student;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy="programme", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<YearOfStudy> yearsOfStudy = new HashSet<>();

    @OneToMany(mappedBy="programme", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseType> courseTypes = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<YearOfStudy> getYearsOfStudy() {
        return yearsOfStudy;
    }

    public void setYearsOfStudy(Set<YearOfStudy> yearsOfStudy) {
        this.yearsOfStudy = yearsOfStudy;
    }

    public Set<CourseType> getCourseTypes() {
        return courseTypes;
    }

    public void setCourseTypes(Set<CourseType> courseTypes) {
        this.courseTypes = courseTypes;
    }
}
