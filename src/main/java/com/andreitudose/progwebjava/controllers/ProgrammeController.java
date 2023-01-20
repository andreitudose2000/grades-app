package com.andreitudose.progwebjava.controllers;

import com.andreitudose.progwebjava.dtos.ProgrammeDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.ProgrammeRequestDto;
import com.andreitudose.progwebjava.dtos.ProgrammeResponseDto;
import com.andreitudose.progwebjava.dtos.StudentDetailedResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.services.ProgrammeService;
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
@RequestMapping("/students/{studentId}/programmes")
public class ProgrammeController {

    private final ProgrammeService programmeService;

    public ProgrammeController(ProgrammeService programmeService) {
        this.programmeService = programmeService;
    }

    @GetMapping
    public List<ProgrammeResponseDto> getAll(@PathVariable Integer studentId) throws NotFoundException {
        return programmeService.getAll(studentId);
    }

    @GetMapping("/{id}")
    public ProgrammeDetailedResponseDto getById(
            @PathVariable Integer studentId,
            @PathVariable Integer id
    )
            throws NotFoundException {

        return programmeService.getById(studentId, id);
    }

    @PostMapping
    public ResponseEntity<ProgrammeResponseDto> create(
            @PathVariable Integer studentId,
            @Valid @RequestBody ProgrammeRequestDto request
    )
            throws URISyntaxException, NotFoundException, BadRequestException, DuplicateItemException {
        var response = programmeService.create(studentId, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(String.format("/students/%s/programmes/%s", studentId, response.getId())));

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgrammeResponseDto> update(
            @PathVariable Integer studentId,
            @PathVariable Integer id,
            @Valid @RequestBody ProgrammeRequestDto request
    )
            throws URISyntaxException, NotFoundException, BadRequestException, DuplicateItemException {
        var response = programmeService.update(studentId, id, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(String.format("/students/%s/programmes/%s", studentId, response.getId())));

        return new ResponseEntity<>(response, responseHeaders, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer studentId, @PathVariable Integer id)
            throws NotFoundException, CannotDeleteException {

        programmeService.delete(studentId, id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/average")
    public Double getAverage(@PathVariable Integer id) throws NotFoundException {
        return programmeService.getGradeAverage(id);
    }

    @GetMapping("/{id}/credits")
    public Integer getAccumulatedCredits(@PathVariable Integer id) throws NotFoundException {
        return programmeService.getTotalCredits(id);
    }
}
