package com.andreitudose.progwebjava.controllers;

import com.andreitudose.progwebjava.dtos.SemesterResponseDto;
import com.andreitudose.progwebjava.dtos.SemesterRequestDto;
import com.andreitudose.progwebjava.dtos.SemesterDetailedResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.services.SemesterService;
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
@RequestMapping("/students/{studentId}/programmes/{programmeId}/years-of-study/{yearOfStudyId}/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @GetMapping
    public List<SemesterResponseDto> getAll(@PathVariable Integer studentId,
                                            @PathVariable Integer programmeId,
                                            @PathVariable Integer yearOfStudyId)
            throws NotFoundException {
        return semesterService.getAll(studentId, programmeId, yearOfStudyId);
    }

    @GetMapping("/{id}")
    public SemesterDetailedResponseDto getById(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer yearOfStudyId,
            @PathVariable Integer id)
                throws NotFoundException {

        return semesterService.getById(studentId, programmeId, yearOfStudyId, id);
    }

    @PostMapping
    public ResponseEntity<SemesterResponseDto> create(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer yearOfStudyId,
            @Valid @RequestBody SemesterRequestDto request
    )
            throws URISyntaxException, NotFoundException, DuplicateItemException, BadRequestException {
        var response = semesterService.create(studentId, programmeId, yearOfStudyId, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(
                String.format("/students/%s/programmes/%s/years-of-study/%s/semesters/%s",
                        studentId,
                        programmeId,
                        yearOfStudyId,
                        response.getId())));

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SemesterResponseDto> update(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer yearOfStudyId,
            @PathVariable Integer id,
            @Valid @RequestBody SemesterRequestDto request
    )
            throws URISyntaxException, NotFoundException, DuplicateItemException, BadRequestException {
        var response = semesterService.update(studentId, programmeId, yearOfStudyId, id, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(
                String.format("/students/%s/programmes/%s/years-of-study/%s/semesters/%s",
                        studentId,
                        programmeId,
                        yearOfStudyId,
                        response.getId())));

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer yearOfStudyId,
            @PathVariable Integer id
    )
            throws NotFoundException, CannotDeleteException {

        semesterService.delete(studentId, programmeId, yearOfStudyId, id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/average")
    public Double getAverage(@PathVariable Integer id) throws NotFoundException {
        return semesterService.getGradeAverage(id);
    }

    @GetMapping("/{id}/credits")
    public Integer getAccumulatedCredits(@PathVariable Integer id) throws NotFoundException {
        return semesterService.getTotalCredits(id);
    }
}
