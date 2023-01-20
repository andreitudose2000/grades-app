package com.andreitudose.progwebjava.services;

import com.andreitudose.progwebjava.dtos.CourseTypeDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.CourseTypeRequestDto;
import com.andreitudose.progwebjava.dtos.CourseTypeResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.CourseType;
import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.Student;
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
public class CourseTypeService {
    private final CourseTypeRepository courseTypeRepository;
    private final StudentRepository studentRepository;
    private final Validator validator;

    public CourseTypeService(CourseTypeRepository courseTypeRepository,
                             StudentRepository studentRepository) {
        this.courseTypeRepository = courseTypeRepository;
        this.studentRepository = studentRepository;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public List<CourseTypeResponseDto> getAll(Integer studentId, Integer programmeId) throws NotFoundException {

        var programme = getProgrammeIfExists(studentId, programmeId);

        return programme.getCourseTypes().stream()
                .map(x -> new CourseTypeResponseDto().fromCourseType(x))
                .collect(Collectors.toList());
    }

    public CourseTypeDetailedResponseDto getById(Integer studentId, Integer programmeId, Integer id)
            throws NotFoundException {

        var programme = getProgrammeIfExists(studentId, programmeId);

        var courseType = programme.getCourseTypes().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(courseType.isEmpty()) {
            throw new NotFoundException("Course type", "id", id.toString());
        }

        return new CourseTypeDetailedResponseDto().fromCourseType(courseType.get());
    }

    public CourseTypeResponseDto create(Integer studentId, Integer programmeId, CourseTypeRequestDto request)
            throws NotFoundException, BadRequestException, DuplicateItemException {

        Set<ConstraintViolation<CourseTypeRequestDto>> validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var programme = getProgrammeIfExists(studentId, programmeId);

        if(programme.getCourseTypes().stream().anyMatch(x -> x.getName().equals(request.getName()))) {
            throw new DuplicateItemException("Course type", "name", request.getName());
        }

        CourseType courseType = request.toCourseType(new CourseType());

        courseType.setProgramme(programme);

        var createdCourseType = courseTypeRepository.save(courseType);

        return new CourseTypeResponseDto().fromCourseType(createdCourseType);
    }

    public CourseTypeResponseDto update(Integer studentId, Integer programmeId, Integer id, CourseTypeRequestDto request)
            throws NotFoundException, DuplicateItemException, BadRequestException {

        Set<ConstraintViolation<CourseTypeRequestDto>> validationResult = validator.validate(request);

        if(validationResult.size() > 0) {
            throw new BadRequestException(ValidationUtils.getErrors(validationResult));
        }

        var programme = getProgrammeIfExists(studentId, programmeId);

        if(programme.getCourseTypes().stream().anyMatch(x ->
                !x.getId().equals(id) && x.getName().equals(request.getName()))) {
            throw new DuplicateItemException("Course type", "name", request.getName());
        }

        var courseType = programme.getCourseTypes().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(courseType.isEmpty()) {
            throw new NotFoundException("Course type", "id", id.toString());
        }

        var updatedCourseType = courseTypeRepository.save(request.toCourseType(courseType.get()));

        return new CourseTypeResponseDto().fromCourseType(updatedCourseType);
    }

    public void delete(Integer studentId, Integer programmeId, Integer id)
            throws NotFoundException, CannotDeleteException {
        var programme = getProgrammeIfExists(studentId, programmeId);

        var courseType = programme.getCourseTypes().stream().filter(x -> x.getId().equals(id)).findFirst();

        if(courseType.isEmpty()) {
            throw new NotFoundException("Course type", "id", id.toString());
        }

        if(courseType.get().getCourses().size() > 0) {
            throw new CannotDeleteException("Course Type", "id", id.toString(), "courses");
        }

        courseTypeRepository.deleteById(id);
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
