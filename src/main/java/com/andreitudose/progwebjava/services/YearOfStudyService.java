package com.andreitudose.progwebjava.services;

import com.andreitudose.progwebjava.dtos.*;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.Student;
import com.andreitudose.progwebjava.model.YearOfStudy;
import com.andreitudose.progwebjava.repositories.ProgrammeRepository;
import com.andreitudose.progwebjava.repositories.StudentRepository;
import com.andreitudose.progwebjava.repositories.YearOfStudyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class YearOfStudyService {
    private final YearOfStudyRepository yearOfStudyRepository;
    private final ProgrammeRepository programmeRepository;
    private final StudentRepository studentRepository;

    public YearOfStudyService(ProgrammeRepository programmeRepository, YearOfStudyRepository yearOfStudyRepository,
                              StudentRepository studentRepository) {
        this.programmeRepository = programmeRepository;
        this.yearOfStudyRepository = yearOfStudyRepository;
        this.studentRepository = studentRepository;
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
            throw new NotFoundException("YearOfStudy", "id", id.toString());
        }

        return new YearOfStudyDetailedResponseDto().fromYearOfStudy(yearOfStudy.get());
    }

    public YearOfStudyResponseDto create(Integer studentId, Integer programmeId, YearOfStudyRequestDto request)
            throws NotFoundException {

        var programme = getProgrammeIfExists(studentId, programmeId);

        YearOfStudy yearOfStudy = request.toYearOfStudy(new YearOfStudy());

        yearOfStudy.setProgramme(programme);

        var createdYearOfStudy = yearOfStudyRepository.save(yearOfStudy);

        return new YearOfStudyResponseDto().fromYearOfStudy(createdYearOfStudy);
    }

    public YearOfStudyResponseDto update(Integer studentId, Integer programmeId, Integer id, YearOfStudyRequestDto request)
            throws NotFoundException {

        var programme = getProgrammeIfExists(studentId, programmeId);

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
            throw new NotFoundException("Programme", "id", id.toString());
        }

        if(yearOfStudy.get().getSemesters().size() > 0) {
            throw new CannotDeleteException("Year of study", "id", id.toString(), "semesters");
        }

        yearOfStudyRepository.deleteById(id);
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
            throw new NotFoundException("Programme", "id", studentId.toString());
        }

        return programme.get();

    }
}
