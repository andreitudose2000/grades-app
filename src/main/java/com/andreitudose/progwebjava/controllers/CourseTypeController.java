package com.andreitudose.progwebjava.controllers;

import com.andreitudose.progwebjava.dtos.CourseTypeDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.CourseTypeRequestDto;
import com.andreitudose.progwebjava.dtos.CourseTypeResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.services.CourseTypeService;
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
@RequestMapping("/students/{studentId}/programmes/{programmeId}/course-types")
public class CourseTypeController {

    private final CourseTypeService courseTypeService;

    public CourseTypeController(CourseTypeService courseTypeService) {
        this.courseTypeService = courseTypeService;
    }

    @GetMapping
    public List<CourseTypeResponseDto> getAll(@PathVariable Integer studentId, @PathVariable Integer programmeId)
            throws NotFoundException {
        return courseTypeService.getAll(studentId, programmeId);
    }

    @GetMapping("/{id}")
    public CourseTypeDetailedResponseDto getById(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer id)
                throws NotFoundException {

        return courseTypeService.getById(studentId, programmeId, id);
    }

    @PostMapping
    public ResponseEntity<CourseTypeResponseDto> create(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @Valid @RequestBody CourseTypeRequestDto request
    )
            throws URISyntaxException, NotFoundException, DuplicateItemException, BadRequestException {
        var response = courseTypeService.create(studentId, programmeId, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(
                String.format("/students/%s/programmes/%s/course-types/%s",
                        studentId,
                        programmeId,
                        response.getId())));

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseTypeResponseDto> update(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer id,
            @Valid @RequestBody CourseTypeRequestDto request
    )
            throws URISyntaxException, NotFoundException, DuplicateItemException, BadRequestException {
        var response = courseTypeService.update(studentId, programmeId, id, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(
                String.format("/students/%s/programmes/%s/course-types/%s",
                        studentId,
                        programmeId,
                        response.getId())));

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer id
    )
            throws NotFoundException, CannotDeleteException {

        courseTypeService.delete(studentId, programmeId, id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
