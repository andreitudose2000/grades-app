package com.andreitudose.progwebjava.controllers;

import com.andreitudose.progwebjava.dtos.CourseRequestDto;
import com.andreitudose.progwebjava.dtos.CourseResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.services.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/students/{studentId}/programmes/{programmeId}/years-of-study/{yearOfStudyId}/semesters/{semesterId}/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponseDto> getAll(@PathVariable Integer studentId,
                                          @PathVariable Integer programmeId,
                                          @PathVariable Integer yearOfStudyId,
                                          @PathVariable Integer semesterId)
            throws NotFoundException {
        return courseService.getAll(studentId, programmeId, yearOfStudyId, semesterId);
    }

    @GetMapping("/{id}")
    public CourseResponseDto getById(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer yearOfStudyId,
            @PathVariable Integer semesterId,
            @PathVariable Integer id)
                throws NotFoundException {

        return courseService.getById(studentId, programmeId, yearOfStudyId, semesterId, id);
    }

    @PostMapping
    public ResponseEntity<CourseResponseDto> create(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer yearOfStudyId,
            @PathVariable Integer semesterId,
            @Valid @RequestBody CourseRequestDto request
    )
            throws URISyntaxException, NotFoundException, BadRequestException {
        var response = courseService.create(studentId, programmeId, yearOfStudyId, semesterId, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(
                String.format("/students/%s/programmes/%s/years-of-study/%s/semesters/%s/courses/%s",
                        studentId,
                        programmeId,
                        yearOfStudyId,
                        semesterId,
                        response.getId())));

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> update(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer yearOfStudyId,
            @PathVariable Integer semesterId,
            @PathVariable Integer id,
            @Valid @RequestBody CourseRequestDto request
    )
            throws URISyntaxException, NotFoundException, BadRequestException {
        var response = courseService.update(studentId, programmeId, yearOfStudyId, semesterId, id, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(
                String.format("/students/%s/programmes/%s/years-of-study/%s/semesters/%s/courses/%s",
                        studentId,
                        programmeId,
                        yearOfStudyId,
                        semesterId,
                        response.getId())));

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer yearOfStudyId,
            @PathVariable Integer semesterId,
            @PathVariable Integer id
    )
            throws NotFoundException, CannotDeleteException {

        courseService.delete(studentId, programmeId, yearOfStudyId, semesterId, id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
