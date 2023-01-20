package com.andreitudose.progwebjava.services;

import com.andreitudose.progwebjava.dtos.CourseRequestDto;
import com.andreitudose.progwebjava.dtos.CourseResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.*;
import com.andreitudose.progwebjava.repositories.CourseRepository;
import com.andreitudose.progwebjava.repositories.CourseTypeRepository;
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
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseTypeRepository courseTypeRepository;
    private final StudentRepository studentRepository;
    private final Validator validator;

    public CourseService(CourseRepository courseRepository,
                         CourseTypeRepository courseTypeRepository,
                         StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.courseTypeRepository = courseTypeRepository;
        this.studentRepository = studentRepository;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public List<CourseResponseDto> getAll(Integer studentId,
                                          Integer programmeId,
                                          Integer yearOfStudyId,
                                          Integer semesterId)
            throws NotFoundException {

        var semester = getSemesterIfExists(studentId, programmeId, yearOfStudyId, semesterId);

        return semester.getCourses().stream()
                .map(x -> new CourseResponseDto().fromCourse(x))
                .collect(Collectors.toList());
    }

    public CourseResponseDto getById(Integer studentId,
                                     Integer programmeId,
                                     Integer yearOfStudyId,
                                     Integer semesterId,
                                     Integer id)
            throws NotFoundException {

        var semester = getSemesterIfExists(studentId, programmeId, yearOfStudyId, semesterId);

        var course = semester.getCourses().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(course.isEmpty()) {
            throw new NotFoundException("Course", "id", id.toString());
        }

        return new CourseResponseDto().fromCourse(course.get());
    }

    public CourseResponseDto create(Integer studentId,
                                    Integer programmeId,
                                    Integer yearOfStudyId,
                                    Integer semesterId,
                                    CourseRequestDto request)
            throws NotFoundException, BadRequestException {

        Set<ConstraintViolation<CourseRequestDto>> validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var semester = getSemesterIfExists(studentId, programmeId, yearOfStudyId, semesterId);

        var courseType = courseTypeRepository.findById(request.getCourseTypeId());

        if(courseType.isEmpty()) {
            throw new NotFoundException("Course type", "id", request.getCourseTypeId().toString());
        }

        Course course = request.toCourse(new Course());

        course.setSemester(semester);
        course.setCourseType(courseType.get());

        var createdCourse = courseRepository.save(course);

        return new CourseResponseDto().fromCourse(createdCourse);
    }

    public CourseResponseDto update(Integer studentId,
                                    Integer programmeId,
                                    Integer yearOfStudyId,
                                    Integer semesterId,
                                    Integer id,
                                    CourseRequestDto request)
            throws NotFoundException, BadRequestException {

        Set<ConstraintViolation<CourseRequestDto>> validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var semester = getSemesterIfExists(studentId, programmeId, yearOfStudyId, semesterId);

        var course = semester.getCourses().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(course.isEmpty()) {
            throw new NotFoundException("Course", "id", id.toString());
        }

        var updatedCourse = courseRepository.save(request.toCourse(course.get()));

        return new CourseResponseDto().fromCourse(updatedCourse);
    }

    public void delete(Integer studentId, Integer programmeId, Integer yearOfStudyId, Integer semesterId, Integer id)
            throws NotFoundException, CannotDeleteException {
        var semester = getSemesterIfExists(studentId, programmeId, yearOfStudyId, semesterId);

        var course = semester.getCourses().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(course.isEmpty()) {
            throw new NotFoundException("Course", "id", id.toString());
        }

        courseRepository.deleteById(id);
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

    private Semester getSemesterIfExists(Integer studentId,
                                         Integer programmeId,
                                         Integer yearOfStudyId,
                                         Integer semesterId)
            throws NotFoundException {

        var yearOfStudy = getYearOfStudyIfExists(studentId, programmeId, yearOfStudyId);

        var semester = yearOfStudy.getSemesters().stream().filter(x -> x.getId().equals(semesterId)).findFirst();

        if(semester.isEmpty()) {
            throw new NotFoundException("Semester", "id", studentId.toString());
        }

        return semester.get();

    }
}
