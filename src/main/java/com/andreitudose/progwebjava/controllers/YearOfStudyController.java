package com.andreitudose.progwebjava.controllers;

import com.andreitudose.progwebjava.dtos.YearOfStudyDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.YearOfStudyRequestDto;
import com.andreitudose.progwebjava.dtos.YearOfStudyResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.services.YearOfStudyService;
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
@RequestMapping("/students/{studentId}/programmes/{programmeId}/years-of-study")
public class YearOfStudyController {

    private final YearOfStudyService yearOfStudyService;

    public YearOfStudyController(YearOfStudyService yearOfStudyService) {
        this.yearOfStudyService = yearOfStudyService;
    }

    @GetMapping
    public List<YearOfStudyResponseDto> getAll(@PathVariable Integer studentId, @PathVariable Integer programmeId)
            throws NotFoundException {
        return yearOfStudyService.getAll(studentId, programmeId);
    }

    @GetMapping("/{id}")
    public YearOfStudyDetailedResponseDto getById(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer id)
                throws NotFoundException {

        return yearOfStudyService.getById(studentId, programmeId, id);
    }

    @PostMapping
    public ResponseEntity<YearOfStudyResponseDto> create(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @Valid @RequestBody YearOfStudyRequestDto request
    )
            throws URISyntaxException, NotFoundException, DuplicateItemException, BadRequestException {
        var response = yearOfStudyService.create(studentId, programmeId, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(
                String.format("/students/%s/programmes/%s/years-of-study/%s",
                        studentId,
                        programmeId,
                        response.getId())));

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<YearOfStudyResponseDto> update(
            @PathVariable Integer studentId,
            @PathVariable Integer programmeId,
            @PathVariable Integer id,
            @Valid @RequestBody YearOfStudyRequestDto request
    )
            throws URISyntaxException, NotFoundException, DuplicateItemException, BadRequestException {
        var response = yearOfStudyService.update(studentId, programmeId, id, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(
                String.format("/students/%s/programmes/%s/years-of-study/%s",
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

        yearOfStudyService.delete(studentId, programmeId, id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/average")
    public Double getAverage(@PathVariable Integer id) throws NotFoundException {
        return yearOfStudyService.getGradeAverage(id);
    }

    @GetMapping("/{id}/credits")
    public Integer getAccumulatedCredits(@PathVariable Integer id) throws NotFoundException {
        return yearOfStudyService.getTotalCredits(id);
    }
}
