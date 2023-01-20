package com.andreitudose.progwebjava;

import com.andreitudose.progwebjava.dtos.StudentDetailedResponseDto;
import com.andreitudose.progwebjava.dtos.StudentRequestDto;
import com.andreitudose.progwebjava.dtos.StudentResponseDto;
import com.andreitudose.progwebjava.exceptions.BadRequestException;
import com.andreitudose.progwebjava.exceptions.CannotDeleteException;
import com.andreitudose.progwebjava.exceptions.DuplicateItemException;
import com.andreitudose.progwebjava.exceptions.NotFoundException;
import com.andreitudose.progwebjava.model.Programme;
import com.andreitudose.progwebjava.model.Student;
import com.andreitudose.progwebjava.repositories.StudentRepository;
import com.andreitudose.progwebjava.services.StudentService;
import com.andreitudose.progwebjava.utils.SerializationUtils;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StudentServiceTests {

    private StudentRepository studentRepositoryMock;
    private StudentService studentService;

    @BeforeEach
    void init() {
        studentRepositoryMock = mock(StudentRepository.class);
        studentService = new StudentService(studentRepositoryMock);
    }

    @Test
    void studentsGetAll() {

        List<Student> students = new ArrayList<>();

        students.add(new Student() {{
            setId(1);
            setFirstName("FirstName1");
            setLastName("LastName1");
            setEmail("Email1");
        }});
        students.add(new Student() {{
            setId(2);
            setFirstName("FirstName2");
            setLastName("LastName2");
            setEmail("Email2");
        }});

        when(studentRepositoryMock.findAll()).thenReturn(students);

        List<StudentResponseDto> response = studentService.getAll();

        List<StudentResponseDto> expected = students.stream()
                .map(x -> new StudentResponseDto().fromStudent(x))
                .toList();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void studentsGetById() throws NotFoundException {

        Student student = new Student() {{
            setId(2);
            setFirstName("FirstName2");
            setLastName("LastName2");
            setEmail("Email2");
            setProgrammes(new HashSet<>() {{
                add(new Programme(){{
                    setId(1);
                    setName("P1");
                }});
            }});
        }};

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(student));

        StudentDetailedResponseDto response = studentService.getById(2);

        StudentDetailedResponseDto expected = new StudentDetailedResponseDto().fromStudent(student);

        assertNotNull(response);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void studentsGetByIdNotFound() throws NotFoundException {

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> {
            studentService.getById(1);
        });

        var expectedMessage = String.format("%s with %s = %s not found",
                "Student", "id", 1);
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void studentsCreate() throws BadRequestException, DuplicateItemException {
        StudentRequestDto request = new StudentRequestDto() {{
            setFirstName("FirstName!");
            setLastName("LastName!");
            setEmail("Email!@email.com");
        }};

        var createdStudent = new Student(){{
            setId(1);
            setFirstName(request.getFirstName());
            setLastName(request.getLastName());
            setEmail(request.getEmail());
        }};

        when(studentRepositoryMock.save(any(Student.class))).thenReturn(createdStudent);

        StudentResponseDto response = studentService.create(request);

        StudentResponseDto expected = new StudentResponseDto().fromStudent(createdStudent);

        assertNotNull(response);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);

        verify(studentRepositoryMock, times(1)).save(any(Student.class));
    }

    @Test
    void studentsCreateBadRequest() throws BadRequestException {

        StudentRequestDto request = new StudentRequestDto() {{
            setFirstName(null);
            setLastName("LastName!");
            setEmail("Email!@email.com");
        }};

        var exception = assertThrows(BadRequestException.class, () -> {
            studentService.create(request);
        });

        var expectedMessage = SerializationUtils.serialize(new HashMap<String, String>() {{
            put("firstName", "must not be blank");
        }});
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void studentsCreateDuplicate() throws BadRequestException {

        when(studentRepositoryMock.findAll()).thenReturn(List.of(new Student() {{
            setEmail("email@email.com");
        }}));
        StudentRequestDto request = new StudentRequestDto() {{
            setFirstName("Abc");
            setLastName("LastName!");
            setEmail("email@email.com");
        }};

        var exception = assertThrows(DuplicateItemException.class, () -> {
            studentService.create(request);
        });

        var expectedMessage = String.format(
                "%s with %s = %s already exists",
                "Student", "email", request.getEmail()
                );
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        verify(studentRepositoryMock, times(0)).save(any(Student.class));
    }

    @Test
    void studentsUpdate() throws NotFoundException, BadRequestException, DuplicateItemException {

        Student student = new Student() {{
            setId(2);
            setFirstName("FirstName1");
            setLastName("LastName1");
            setEmail("Email1@email.com");
        }};

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(student));

        StudentRequestDto request = new StudentRequestDto() {{
            setFirstName("FirstName!");
            setLastName("LastName!");
            setEmail("Email!@email.com");
        }};

        var updatedStudent = new Student(){{
            setId(student.getId());
            setFirstName(request.getFirstName());
            setLastName(request.getLastName());
            setEmail(request.getEmail());
        }};

        when(studentRepositoryMock.save(any(Student.class))).thenReturn(updatedStudent);

        StudentResponseDto response = studentService.update(2, request);

        StudentResponseDto expected = new StudentResponseDto().fromStudent(updatedStudent);

        assertNotNull(response);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);

        verify(studentRepositoryMock, times(1)).save(any(Student.class));

    }

    @Test
    void studentsUpdateNotFound() throws NotFoundException {

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        StudentRequestDto request = new StudentRequestDto() {{
            setFirstName("FirstName!");
            setLastName("LastName!");
            setEmail("Email!@email.com");
        }};

        var exception = assertThrows(NotFoundException.class, () -> {
            studentService.update(1, request);
        });

        var expectedMessage = String.format("%s with %s = %s not found",
                "Student", "id", 1);
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void studentsUpdateBadRequest() throws NotFoundException {

        StudentRequestDto request = new StudentRequestDto() {{
            setFirstName(String.join("", Collections.nCopies(101, "x")));
            setLastName("LastName!");
            setEmail("Email!@email.com");
        }};

        var exception = assertThrows(BadRequestException.class, () -> {
            studentService.update(1, request);
        });

        var expectedMessage = SerializationUtils.serialize(new HashMap<String, String>() {{
            put("firstName", "size must be between 1 and 100");
        }});
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void studentsUpdateDuplicate() throws BadRequestException {
        when(studentRepositoryMock.findAll()).thenReturn(List.of(new Student() {{
            setId(7);
            setEmail("email@email.com");
        }}));
        StudentRequestDto request = new StudentRequestDto() {{
            setFirstName("Abc");
            setLastName("LastName!");
            setEmail("email@email.com");
        }};

        var exception = assertThrows(DuplicateItemException.class, () -> {
            studentService.update(2, request);
        });

        var expectedMessage = String.format(
                "%s with %s = %s already exists",
                "Student", "email", request.getEmail()
        );
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        verify(studentRepositoryMock, times(0)).save(any(Student.class));
    }

    @Test
    void studentsDelete() throws NotFoundException, CannotDeleteException {

        Student student = new Student() {{
            setId(2);
            setFirstName("FirstName1");
            setLastName("LastName1");
            setEmail("Email1@email.com");
        }};

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(student));

        studentService.delete(2);

        verify(studentRepositoryMock, times(1)).deleteById(2);
    }

    @Test
    void studentsDeleteNotFound() throws NotFoundException, CannotDeleteException {

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> {
            studentService.delete(3);
        });

        var expectedMessage = String.format("%s with %s = %s not found",
                "Student", "id", 3);
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        verify(studentRepositoryMock, times(0)).deleteById(3);
    }

    @Test
    void studentsDeleteCannotDelete() throws NotFoundException, CannotDeleteException {

        Student student = new Student() {{
            setId(2);
            setFirstName("FirstName1");
            setLastName("LastName1");
            setEmail("Email1@email.com");
            setProgrammes(new HashSet<>(){{
                add(new Programme());
            }});
        }};

        when(studentRepositoryMock.findById(any(Integer.class))).thenReturn(Optional.of(student));

        var exception = assertThrows(CannotDeleteException.class, () -> {
            studentService.delete(3);
        });

        var expectedMessage = String.format("%s with %s = %s can not be deleted because it has associated %s",
                "Student", "id", 3, "programmes");
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        verify(studentRepositoryMock, times(0)).deleteById(2);
    }
}
