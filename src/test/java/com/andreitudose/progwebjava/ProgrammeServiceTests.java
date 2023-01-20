package com.andreitudose.progwebjava;

import com.andreitudose.progwebjava.dtos.*;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.Student;
import com.andreitudose.progwebjava.model.YearOfStudy;
import com.andreitudose.progwebjava.repositories.ProgrammeRepository;
import com.andreitudose.progwebjava.repositories.StudentRepository;
import com.andreitudose.progwebjava.services.ProgrammeService;
import com.andreitudose.progwebjava.services.StudentService;
import com.andreitudose.progwebjava.utils.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProgrammeServiceTests {

    private StudentRepository studentRepositoryMock;
    private ProgrammeRepository programmeRepositoryMock;
    private ProgrammeService programmeService;

    @BeforeEach
    void init() {
        studentRepositoryMock = mock(StudentRepository.class);
        programmeRepositoryMock = mock(ProgrammeRepository.class);
        programmeService = new ProgrammeService(programmeRepositoryMock, studentRepositoryMock);
    }

    @Test
    void programmesGetAll() throws NotFoundException {

        Student student = new Student() {{
            setId(1);
            setFirstName("FirstName1");
            setLastName("LastName1");
            setEmail("Email1");
            setProgrammes(new HashSet<>(){{
                add(new Programme() {{
                    setId(1);
                    setName("Programme1");
                }});
                add(new Programme() {{
                    setId(2);
                    setName("Programme2");
                }});
            }});
        }};

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(student));

        List<ProgrammeResponseDto> response = programmeService.getAll(1);

        List<ProgrammeResponseDto> expected = student.getProgrammes().stream()
                .map(x -> new ProgrammeResponseDto().fromProgramme(x))
                .toList();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void programmesGetById() throws NotFoundException {

        var programme = new Programme(){{
            setId(1);
            setName("P1");
            setYearsOfStudy(new HashSet<>(){{
                add(new YearOfStudy() {{
                    setId(1);
                    setNumber(1);
                }});
            }});
        }};

        Student student = new Student() {{
            setId(2);
            setFirstName("FirstName2");
            setLastName("LastName2");
            setEmail("Email2");
            setProgrammes(new HashSet<>() {{
                add(programme);
            }});
        }};

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(student));

        ProgrammeDetailedResponseDto response = programmeService.getById(1, 1);

        ProgrammeDetailedResponseDto expected = new ProgrammeDetailedResponseDto()
                .fromProgramme(programme);

        assertNotNull(response);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void programmesGetByIdNotFound() throws NotFoundException {

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(new Student()));

        var exception = assertThrows(NotFoundException.class, () -> {
            programmeService.getById(1, 1);
        });

        var expectedMessage = String.format("%s with %s = %s not found",
                "Programme", "id", 1);
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void programmeCreate() throws BadRequestException, NotFoundException, DuplicateItemException {

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(new Student()));
        ProgrammeRequestDto request = new ProgrammeRequestDto() {{
            setName("FirstName!");
        }};

        var createdProgramme = new Programme(){{
            setId(1);
            setName(request.getName());
        }};

        when(programmeRepositoryMock.save(any(Programme.class))).thenReturn(createdProgramme);

        ProgrammeResponseDto response = programmeService.create(1, request);

        ProgrammeResponseDto expected = new ProgrammeResponseDto().fromProgramme(createdProgramme);

        assertNotNull(response);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);

        verify(programmeRepositoryMock, times(1)).save(any(Programme.class));
    }

    @Test
    void programmeCreateBadRequest() throws BadRequestException {
        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(new Student()));

        ProgrammeRequestDto request = new ProgrammeRequestDto() {{
            setName(null);
        }};

        var exception = assertThrows(BadRequestException.class, () -> {
            programmeService.create(1, request);
        });

        var expectedMessage = SerializationUtils.serialize(new HashMap<String, String>() {{
            put("name", "must not be blank");
        }});
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    void programmeUpdate() throws NotFoundException, BadRequestException, DuplicateItemException {

        Programme programme = new Programme() {{
            setId(2);
            setName("Programme2");
        }};

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(new Student() {{
            setProgrammes(new HashSet<>() {{
                add(programme);
            }});
        }}));

        ProgrammeRequestDto request = new ProgrammeRequestDto() {{
            setName("Update!");
        }};

        var updatedProgramme = new Programme(){{
            setId(programme.getId());
            setName(request.getName());
        }};

        when(programmeRepositoryMock.save(any(Programme.class))).thenReturn(updatedProgramme);

        ProgrammeResponseDto response = programmeService.update(1, 2, request);

        ProgrammeResponseDto expected = new ProgrammeResponseDto().fromProgramme(updatedProgramme);

        assertNotNull(response);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);

        verify(programmeRepositoryMock, times(1)).save(any(Programme.class));

    }

    @Test
    void programmeUpdateNotFound() throws NotFoundException {

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(new Student()));

        ProgrammeRequestDto request = new ProgrammeRequestDto() {{
            setName("Update!");
        }};

        var exception = assertThrows(NotFoundException.class, () -> {
            programmeService.update(1, 1, request);
        });

        var expectedMessage = String.format("%s with %s = %s not found",
                "Programme", "id", 1);
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        verify(programmeRepositoryMock, times(0)).save(any(Programme.class));
    }

    @Test
    void programmesUpdateBadRequest() throws NotFoundException {
        ProgrammeRequestDto request = new ProgrammeRequestDto() {{
            setName(String.join("", Collections.nCopies(101, "q")));
        }};

        var exception = assertThrows(BadRequestException.class, () -> {
            programmeService.update(1,1, request);
        });

        var expectedMessage = SerializationUtils.serialize(new HashMap<String, String>() {{
            put("name", "size must be between 1 and 100");
        }});
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        verify(programmeRepositoryMock, times(0)).save(any(Programme.class));
    }

    @Test
    void programmesDelete() throws NotFoundException, CannotDeleteException {

        Programme programme = new Programme() {{
            setId(2);
            setName("name1");
        }};

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(new Student() {{
            setProgrammes(new HashSet<>(){{
                add(programme);
            }});
        }}));

        programmeService.delete(1, 2);

        verify(programmeRepositoryMock, times(1)).deleteById(2);
    }

    @Test
    void programmesDeleteNotFound() throws NotFoundException, CannotDeleteException {

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(new Student()));

        var exception = assertThrows(NotFoundException.class, () -> {
            programmeService.delete(1,3);
        });

        var expectedMessage = String.format("%s with %s = %s not found",
                "Programme", "id", 3);
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        verify(programmeRepositoryMock, times(0)).deleteById(3);
    }

    @Test
    void programmesDeleteCannotDelete() throws NotFoundException, CannotDeleteException {

        Student student = new Student() {{
            setProgrammes(new HashSet<>(){{
                setId(1);
                add(new Programme(){{
                    setId(3);
                    setYearsOfStudy(new HashSet<>(){{
                        add(new YearOfStudy());
                    }});
                }});
            }});
        }};

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(student));

        var exception = assertThrows(CannotDeleteException.class, () -> {
            programmeService.delete(1, 3);
        });

        var expectedMessage = String.format("%s with %s = %s can not be deleted because it has associated %s",
                "Programme", "id", 3, "years of study");
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        verify(studentRepositoryMock, times(0)).deleteById(2);
    }
}
