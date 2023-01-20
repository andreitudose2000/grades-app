package com.andreitudose.progwebjava.services;

import com.andreitudose.progwebjava.dtos.YearOfStudyDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.YearOfStudyRequestDto;
import com.andreitudose.progwebjava.dtos.YearOfStudyResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.Course;
import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.Student;
import com.andreitudose.progwebjava.model.YearOfStudy;
import com.andreitudose.progwebjava.repositories.StudentRepository;
import com.andreitudose.progwebjava.repositories.YearOfStudyRepository;
import com.andreitudose.progwebjava.utils.ValidationUtils;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class YearOfStudyService {
    private final YearOfStudyRepository yearOfStudyRepository;
    private final StudentRepository studentRepository;
    private Validator validator;

    public YearOfStudyService(YearOfStudyRepository yearOfStudyRepository,
                              StudentRepository studentRepository) {
        this.yearOfStudyRepository = yearOfStudyRepository;
        this.studentRepository = studentRepository;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public List<YearOfStudyResponseDto> getAll(Integer studentId, Integer programmeId) throws NotFoundException {

        var programme = getProgrammeIfExists(studentId, programmeId);

        return programme.getYearsOfStudy().stream()
                .map(x -> new YearOfStudyResponseDto().fromYearOfStudy(x))
                .collect(Collectors.toList());
    }

    public YearOfStudyDetailedResponseDto getById(Integer studentId, Integer programmeId, Integer id)
            throws NotFoundException {

        var programme = getProgrammeIfExists(studentId, programmeId);

        var yearOfStudy = programme.getYearsOfStudy().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(yearOfStudy.isEmpty()) {
            throw new NotFoundException("Year of study", "id", id.toString());
        }

        return new YearOfStudyDetailedResponseDto().fromYearOfStudy(yearOfStudy.get());
    }

    public YearOfStudyResponseDto create(Integer studentId, Integer programmeId, YearOfStudyRequestDto request)
            throws NotFoundException, DuplicateItemException, BadRequestException {

        var validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var programme = getProgrammeIfExists(studentId, programmeId);

        if(programme.getYearsOfStudy().stream().anyMatch(x -> x.getNumber().equals(request.getNumber()))) {
            throw new DuplicateItemException("Year of study", "number", request.getNumber().toString());
        }

        YearOfStudy yearOfStudy = request.toYearOfStudy(new YearOfStudy());

        yearOfStudy.setProgramme(programme);

        var createdYearOfStudy = yearOfStudyRepository.save(yearOfStudy);

        return new YearOfStudyResponseDto().fromYearOfStudy(createdYearOfStudy);
    }

    public YearOfStudyResponseDto update(Integer studentId, Integer programmeId, Integer id, YearOfStudyRequestDto request)
            throws NotFoundException, DuplicateItemException, BadRequestException {

        var validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var programme = getProgrammeIfExists(studentId, programmeId);

        if(programme.getYearsOfStudy().stream().anyMatch(
                x -> !x.getId().equals(id) && x.getNumber().equals(request.getNumber()))) {
            throw new DuplicateItemException("Year of study", "number", request.getNumber().toString());
        }

        var yearOfStudy = programme.getYearsOfStudy().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(yearOfStudy.isEmpty()) {
            throw new NotFoundException("Year of study", "id", id.toString());
        }

        var updatedYearOfStudy = yearOfStudyRepository.save(request.toYearOfStudy(yearOfStudy.get()));

        return new YearOfStudyResponseDto().fromYearOfStudy(updatedYearOfStudy);
    }

    public void delete(Integer studentId, Integer programmeId, Integer id)
            throws NotFoundException, CannotDeleteException {
        var programme = getProgrammeIfExists(studentId, programmeId);

        var yearOfStudy = programme.getYearsOfStudy().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(yearOfStudy.isEmpty()) {
            throw new NotFoundException("Year of study", "id", id.toString());
        }

        if(yearOfStudy.get().getSemesters().size() > 0) {
            throw new CannotDeleteException("Year of study", "id", id.toString(), "semesters");
        }

        yearOfStudyRepository.deleteById(id);
    }

    public Double getGradeAverage(Integer id) throws NotFoundException {
        var yearOfStudy = yearOfStudyRepository.findById(id);

        if(yearOfStudy.isEmpty()) {
            throw new NotFoundException("YearOfStudy", "id", id.toString());
        }

        var semesters = yearOfStudy.get().getSemesters();

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
    }

    public Integer getTotalCredits(Integer id) throws NotFoundException {
        var yearOfStudy = yearOfStudyRepository.findById(id);

        if(yearOfStudy.isEmpty()) {
            throw new NotFoundException("YearOfStudy", "id", id.toString());
        }

        return yearOfStudy.get().getSemesters().stream()
                .map(semester -> semester.getCourses().stream()
                        .filter(x -> x.getGrade() >= 5)
                        .map(Course::getNumberOfCredits)
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

    private Programme getProgrammeIfExists(Integer studentId, Integer programmeId) throws NotFoundException {

        var student = getStudentIfExists(studentId);

        var programme = student.getProgrammes().stream().filter(x -> x.getId().equals(programmeId)).findFirst();

        if(programme.isEmpty()) {
            throw new NotFoundException("Programme", "id", programmeId.toString());
        }

        return programme.get();

    }
}
