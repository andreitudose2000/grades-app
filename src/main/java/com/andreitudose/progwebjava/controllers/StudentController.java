package com.andreitudose.progwebjava.controllers;

import com.andreitudose.progwebjava.dtos.StudentDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.StudentRequestDto;
import com.andreitudose.progwebjava.dtos.StudentResponseDto;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.services.StudentService;
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
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentResponseDto> getAll() {
        return studentService.getAll();
    }


    @GetMapping("/{id}")
    public StudentDetailedResponseDto getById(@PathVariable Integer id)
            throws NotFoundException {

        return studentService.getById(id);
    }

    @PostMapping
    public ResponseEntity<StudentResponseDto> create(@Valid @RequestBody StudentRequestDto request)
            throws URISyntaxException {
        var response = studentService.create(request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(String.format("/students/%s", response.getId())));

        return new ResponseEntity<StudentResponseDto>(response, responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody StudentRequestDto request
    )
            throws URISyntaxException, NotFoundException {
        var response = studentService.update(id, request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(new URI(String.format("/students/%s", response.getId())));

        return new ResponseEntity<StudentResponseDto>(response, responseHeaders, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id)
            throws NotFoundException, CannotDeleteException {

        studentService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
