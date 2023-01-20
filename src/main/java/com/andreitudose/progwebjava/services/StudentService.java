package com.andreitudose.progwebjava.services;

import com.andreitudose.progwebjava.dtos.StudentDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.StudentRequestDto;
import com.andreitudose.progwebjava.dtos.StudentResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.Student;
import com.andreitudose.progwebjava.repositories.StudentRepository;
import com.andreitudose.progwebjava.utils.ValidationUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final Validator validator;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public List<StudentResponseDto> getAll() {

        var xy = studentRepository.findAll();

        return xy.stream()
                .map(x -> new StudentResponseDto().fromStudent(x))
                .collect(Collectors.toList());
    }

    public StudentDetailedResponseDto getById(Integer id) throws NotFoundException {

        var student = studentRepository.findById(id);

        if(student.isEmpty()) {
            throw new NotFoundException("Student", "id", id.toString());
        }

        return new StudentDetailedResponseDto().fromStudent(student.get());
    }

    public StudentResponseDto create(StudentRequestDto request) throws BadRequestException, DuplicateItemException {

        Set<ConstraintViolation<StudentRequestDto>> validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var students = studentRepository.findAll();

        if(students.stream().anyMatch(x -> x.getEmail().equals(request.getEmail()))) {
            throw new DuplicateItemException("Student", "email", request.getEmail());
        }

        Student student = request.toStudent(new Student());

        var createdStudent = studentRepository.save(student);

        return new StudentResponseDto().fromStudent(createdStudent);
    }

    public StudentResponseDto update(Integer id, StudentRequestDto request)
            throws NotFoundException, BadRequestException, DuplicateItemException {

        Set<ConstraintViolation<StudentRequestDto>> validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var students = studentRepository.findAll();

        if(students.stream().anyMatch(x -> !x.getId().equals(id) && x.getEmail().equals(request.getEmail()))) {
            throw new DuplicateItemException("Student", "email", request.getEmail());
        }

        var student = studentRepository.findById(id);

        if(student.isEmpty()) {
            throw new NotFoundException("Student", "id", id.toString());
        }

        var createdStudent = studentRepository.save(request.toStudent(student.get()));

        return new StudentResponseDto().fromStudent(createdStudent);
    }

    public void delete(Integer id) throws NotFoundException, CannotDeleteException {
        var student = studentRepository.findById(id);

        if(student.isEmpty()) {
            throw new NotFoundException("Student", "id", id.toString());
        }

        if(student.get().getProgrammes().size() > 0) {
            throw new CannotDeleteException("Student", "id", id.toString(), "programmes");
        }

        studentRepository.deleteById(id);
    }
}
