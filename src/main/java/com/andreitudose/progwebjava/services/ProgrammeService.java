package com.andreitudose.progwebjava.services;

import com.andreitudose.progwebjava.dtos.ProgrammeDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.ProgrammeRequestDto;
import com.andreitudose.progwebjava.dtos.ProgrammeResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.Course;
import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.Student;
import com.andreitudose.progwebjava.repositories.ProgrammeRepository;
import com.andreitudose.progwebjava.repositories.StudentRepository;
import com.andreitudose.progwebjava.utils.ValidationUtils;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public ProgrammeResponseDto create(Integer studentId, ProgrammeRequestDto request) throws NotFoundException, BadRequestException, DuplicateItemException {

        var validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var student = getStudentIfExists(studentId);

        if(student.getProgrammes().stream().anyMatch(x -> x.getName().equals(request.getName()))){
            throw new DuplicateItemException("Programme", "name", request.getName());
        }

        Programme programme = request.toProgramme(new Programme());

        programme.setStudent(student);

        var createdProgramme = programmeRepository.save(programme);

        return new ProgrammeResponseDto().fromProgramme(createdProgramme);
    }

    public ProgrammeResponseDto update(Integer studentId, Integer id, ProgrammeRequestDto request) throws NotFoundException, BadRequestException, DuplicateItemException {

        var validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var student = getStudentIfExists(studentId);

        if(student.getProgrammes().stream()
                .anyMatch(x -> !x.getId().equals(id)
                            && x.getName().equals(request.getName()))){
            throw new DuplicateItemException("Programme", "name", request.getName());
        }

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

    public Double getGradeAverage(Integer id) throws NotFoundException {
        var programme = programmeRepository.findById(id);

        if(programme.isEmpty()) {
            throw new NotFoundException("Programme", "id", id.toString());
        }

        var years = programme.get().getYearsOfStudy();

        List<Double> yearAverages = years
                .stream().map(year -> {
                    var semesters = year.getSemesters();

                    List<Double> semesterAverages = semesters
                            .stream().map(semester -> {
                                var courses = semester.getCourses();

                                var totalNumberOfCredits = courses.stream()
                                        .map(Course::getNumberOfCredits)
                                        .reduce(0, Integer::sum);

                                var sum = courses.stream()
                                        .map(x -> Double.valueOf(x.getGrade() * x.getNumberOfCredits()))
                                        .reduce((double) 0, Double::sum);

                                return sum / totalNumberOfCredits;
                            }).toList();

                    return semesterAverages.stream().reduce((double)0, Double::sum) / semesters.size();
                }).toList();

        return yearAverages.stream().reduce((double)0, Double::sum) / years.size();
    }

    public Integer getTotalCredits(Integer id) throws NotFoundException {
        var programme = programmeRepository.findById(id);

        if(programme.isEmpty()) {
            throw new NotFoundException("Programme", "id", id.toString());
        }

        return programme.get().getYearsOfStudy().stream()
                .map(yearOfStudy -> yearOfStudy.getSemesters().stream()
                        .map(semester -> semester.getCourses().stream()
                                .filter(x -> x.getGrade() >= 5)
                                .map(Course::getNumberOfCredits)
                                .reduce(0, Integer::sum)
                        )
                        .reduce(0, Integer::sum)
                )
                .reduce(0, Integer::sum);
    }

    private Student getStudentIfExists(Integer studentId) throws NotFoundException {
        var student = studentRepository.findById(studentId);

        if(student.isEmpty()) {
            throw new NotFoundException("Student", "id", studentId.toString());
        }

        return student.get();

    }
}
