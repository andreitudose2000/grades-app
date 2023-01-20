package com.andreitudose.progwebjava.services;

import com.andreitudose.progwebjava.dtos.ProgrammeDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.ProgrammeRequestDto;
import com.andreitudose.progwebjava.dtos.ProgrammeResponseDto;
import com.andreitudose.progwebjava.dtos.StudentRequestDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.Student;
import com.andreitudose.progwebjava.repositories.ProgrammeRepository;
import com.andreitudose.progwebjava.repositories.StudentRepository;
import com.andreitudose.progwebjava.repositories.YearOfStudyRepository;
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
public class ProgrammeService {
    private final ProgrammeRepository programmeRepository;
    private final StudentRepository studentRepository;
    private Validator validator;

    public ProgrammeService(ProgrammeRepository programmeRepository,
                            StudentRepository studentRepository) {
        this.programmeRepository = programmeRepository;
        this.studentRepository = studentRepository;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public List<ProgrammeResponseDto> getAll(Integer studentId) throws NotFoundException {

        var student = getStudentIfExists(studentId);

        return student.getProgrammes().stream()
                .map(x -> new ProgrammeResponseDto().fromProgramme(x))
                .collect(Collectors.toList());
    }

    public ProgrammeDetailedResponseDto getById(Integer studentId, Integer id) throws NotFoundException {

        var student = getStudentIfExists(studentId);

        var programme = student.getProgrammes().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(programme.isEmpty()) {
            throw new NotFoundException("Programme", "id", id.toString());
        }

        return new ProgrammeDetailedResponseDto().fromProgramme(programme.get());
    }

    public ProgrammeResponseDto create(Integer studentId, ProgrammeRequestDto request) throws NotFoundException, BadRequestException {

        var validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var student = getStudentIfExists(studentId);

        Programme programme = request.toProgramme(new Programme());

        programme.setStudent(student);

        var createdProgramme = programmeRepository.save(programme);

        return new ProgrammeResponseDto().fromProgramme(createdProgramme);
    }

    public ProgrammeResponseDto update(Integer studentId, Integer id, ProgrammeRequestDto request) throws NotFoundException, BadRequestException {

        var validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var student = getStudentIfExists(studentId);

        var programme = student.getProgrammes().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(programme.isEmpty()) {
            throw new NotFoundException("Programme", "id", id.toString());
        }

        var createdProgramme = programmeRepository.save(request.toProgramme(programme.get()));

        return new ProgrammeResponseDto().fromProgramme(createdProgramme);
    }

    public void delete(Integer studentId, Integer id) throws NotFoundException, CannotDeleteException {
        var student = getStudentIfExists(studentId);

        var programme = student.getProgrammes().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(programme.isEmpty()) {
            throw new NotFoundException("Programme", "id", id.toString());
        }

        if(programme.get().getCourseTypes().size() > 0) {
            throw new CannotDeleteException("Programme", "id", id.toString(), "course types");
        }

        if(programme.get().getYearsOfStudy().size() > 0) {
            throw new CannotDeleteException("Programme", "id", id.toString(), "years of study");
        }

        programmeRepository.deleteById(id);
    }

    private Student getStudentIfExists(Integer studentId) throws NotFoundException {
        var student = studentRepository.findById(studentId);

        if(student.isEmpty()) {
            throw new NotFoundException("Student", "id", studentId.toString());
        }

        return student.get();

    }
}
