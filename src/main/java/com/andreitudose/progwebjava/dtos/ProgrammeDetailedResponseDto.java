package com.andreitudose.progwebjava.dtos;

import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.YearOfStudy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProgrammeDetailedResponseDto {
    private Integer id;
    private String name;
    private Set<YearOfStudyResponseDto> yearsOfStudy = new HashSet<>();
    private Set<CourseTypeResponseDto> courseTypes = new HashSet<>();
    public ProgrammeDetailedResponseDto fromProgramme(Programme programme) {

        setId(programme.getId());
        setName(programme.getName());
        setYearsOfStudy(
                programme.getYearsOfStudy().stream()
                        .map(x -> new YearOfStudyResponseDto().fromYearOfStudy(x))
                        .collect(Collectors.toSet())
        );
        setCourseTypes(
                programme.getCourseTypes().stream()
                        .map(x -> new CourseTypeResponseDto().fromCourseType(x))
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<YearOfStudyResponseDto> getYearsOfStudy() {
        return yearsOfStudy;
    }

    public void setYearsOfStudy(Set<YearOfStudyResponseDto> yearsOfStudy) {
        this.yearsOfStudy = yearsOfStudy;
    }

    public Set<CourseTypeResponseDto> getCourseTypes() {
        return courseTypes;
    }

    public void setCourseTypes(Set<CourseTypeResponseDto> courseTypes) {
        this.courseTypes = courseTypes;
    }
}
