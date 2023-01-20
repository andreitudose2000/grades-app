package com.andreitudose.progwebjava.services;

import com.andreitudose.progwebjava.dtos.SemesterDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.SemesterRequestDto;
import com.andreitudose.progwebjava.dtos.SemesterResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.*;
import com.andreitudose.progwebjava.repositories.SemesterRepository;
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
public class SemesterService {
    private final SemesterRepository semesterRepository;
    private final StudentRepository studentRepository;
    private final Validator validator;

    public SemesterService(SemesterRepository semesterRepository,
                           StudentRepository studentRepository) {
        this.semesterRepository = semesterRepository;
        this.studentRepository = studentRepository;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public List<SemesterResponseDto> getAll(Integer studentId,
                                            Integer programmeId,
                                            Integer yearOfStudyId) throws NotFoundException {

        var yearOfStudy = getYearOfStudyIfExists(studentId, programmeId, yearOfStudyId);

        return yearOfStudy.getSemesters().stream()
                .map(x -> new SemesterResponseDto().fromSemester(x))
                .collect(Collectors.toList());
    }

    public SemesterDetailedResponseDto getById(Integer studentId,
                                                  Integer programmeId,
                                                  Integer yearOfStudyId,
                                                  Integer id)
            throws NotFoundException {

        var yearOfStudy = getYearOfStudyIfExists(studentId, programmeId, yearOfStudyId);

        var semester = yearOfStudy.getSemesters().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(semester.isEmpty()) {
            throw new NotFoundException("Semester", "id", id.toString());
        }

        return new SemesterDetailedResponseDto().fromSemester(semester.get());
    }

    public SemesterResponseDto create(Integer studentId,
                                         Integer programmeId,
                                         Integer yearOfStudyId,
                                         SemesterRequestDto request)
            throws NotFoundException, BadRequestException, DuplicateItemException {

        Set<ConstraintViolation<SemesterRequestDto>> validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var yearOfStudy = getYearOfStudyIfExists(studentId, programmeId, yearOfStudyId);

        if(yearOfStudy.getSemesters().stream().anyMatch(x -> x.getNumber().equals(request.getNumber()))) {
            throw new DuplicateItemException("Semester", "number", request.getNumber().toString());
        }

        Semester semester = request.toSemester(new Semester());

        semester.setYearOfStudy(yearOfStudy);

        var createdSemester = semesterRepository.save(semester);

        return new SemesterResponseDto().fromSemester(createdSemester);
    }

    public SemesterResponseDto update(Integer studentId,
                                      Integer programmeId,
                                      Integer yearOfStudyId,
                                      Integer id,
                                      SemesterRequestDto request)
            throws NotFoundException, BadRequestException, DuplicateItemException {

        Set<ConstraintViolation<SemesterRequestDto>> validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var yearOfStudy = getYearOfStudyIfExists(studentId, programmeId, yearOfStudyId);

        if(yearOfStudy.getSemesters().stream().anyMatch(x ->
                !x.getId().equals(id) && x.getNumber().equals(request.getNumber()))) {
            throw new DuplicateItemException("Semester", "number", request.getNumber().toString());
        }

        var semester = yearOfStudy.getSemesters().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(semester.isEmpty()) {
            throw new NotFoundException("Semester", "id", id.toString());
        }

        var updatedSemester = semesterRepository.save(request.toSemester(semester.get()));

        return new SemesterResponseDto().fromSemester(updatedSemester);
    }

    public void delete(Integer studentId, Integer programmeId, Integer yearOfStudyId, Integer id)
            throws NotFoundException, CannotDeleteException {
        var yearOfStudy = getYearOfStudyIfExists(studentId, programmeId, yearOfStudyId);

        var semester = yearOfStudy.getSemesters().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(semester.isEmpty()) {
            throw new NotFoundException("Semester", "id", id.toString());
        }

        if(semester.get().getCourses().size() > 0) {
            throw new CannotDeleteException("Semester", "id", id.toString(), "courses");
        }

        semesterRepository.deleteById(id);
    }

    public Double getGradeAverage(Integer semesterId) throws NotFoundException {
        var semester = semesterRepository.findById(semesterId);

        if(semester.isEmpty()) {
            throw new NotFoundException("Semester", "id", semesterId.toString());
        }

        var courses = semester.get().getCourses();

        var totalNumberOfCredits = courses.stream()
                .map(Course::getNumberOfCredits)
                .reduce(0, Integer::sum);

        var sum = courses
                .stream().map(x -> Double.valueOf(x.getGrade() * x.getNumberOfCredits()))
                .reduce((double) 0, Double::sum);

        return sum / totalNumberOfCredits;
    }

    public Integer getTotalCredits(Integer id) throws NotFoundException {
        var semester = semesterRepository.findById(id);

        if(semester.isEmpty()) {
            throw new NotFoundException("Semester", "id", id.toString());
        }

        return semester.get().getCourses().stream()
                        .filter(x -> x.getGrade() >= 5)
                        .map(Course::getNumberOfCredits)
                        .reduce(0, Integer::sum);
    }

    private Student getStudentIfExists(Integer studentId) throws NotFoundException {
        var student = studentRepository.findById(studentId);

        if(student.isEmpty()) {
            throw new NotFoundException("Student", "id", studentId.toString());
        }

        return student.get();
    }

    private Programme getProgrammeIfExists(Integer studentId, Integer programmeId) throws NotFoundException {

        var student = getStudentIfExists(studentId);

        var programme = student.getProgrammes().stream().filter(x -> x.getId().equals(programmeId)).findFirst();

        if(programme.isEmpty()) {
            throw new NotFoundException("Programme", "id", programmeId.toString());
        }

        return programme.get();

    }

    private YearOfStudy getYearOfStudyIfExists(Integer studentId,
                                               Integer programmeId,
                                               Integer yearOfStudyId)
            throws NotFoundException {

        var programme = getProgrammeIfExists(studentId, programmeId);

        var yearOfStudy = programme.getYearsOfStudy().stream().filter(x -> x.getId().equals(yearOfStudyId)).findFirst();

        if(yearOfStudy.isEmpty()) {
            throw new NotFoundException("Year of study", "id", yearOfStudyId.toString());
        }

        return yearOfStudy.get();

    }
}
