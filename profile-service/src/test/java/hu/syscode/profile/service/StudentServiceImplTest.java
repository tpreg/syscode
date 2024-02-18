package hu.syscode.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import hu.syscode.profile.StudentRepository;
import hu.syscode.profile.dto.AddressDTO;
import hu.syscode.profile.dto.StudentDTO;
import hu.syscode.profile.exception.ResourceNotFoundException;
import hu.syscode.profile.mapper.StudentMapper;
import hu.syscode.profile.model.Student;
import reactor.core.publisher.Mono;

@SpringBootTest
class StudentServiceImplTest {

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private StudentMapper studentMapper;

	@Mock
	private WebClient webClient;

	@InjectMocks
	private StudentServiceImpl studentService;

	@Test
	void givenValidStudentDto_whenSaveStudentCalled_thenStudentIsSaved() {
		final var id = UUID.randomUUID();
		final var studentDTO = new StudentDTO(null, "Suresh Wei", "swei@yahoo.com");
		final var student = new Student(id, "Suresh Wei", "swei@yahoo.com");
		final var convertedStudentDTO = new StudentDTO(id, "Suresh Wei", "swei@yahoo.com");

		when(this.studentRepository.save(any(Student.class))).thenReturn(student);
		when(this.studentMapper.convertToEntityFromDto(any(StudentDTO.class))).thenReturn(student);
		when(this.studentMapper.convertToDtoFromEntity(any(Student.class))).thenReturn(convertedStudentDTO);

		final var created = this.studentService.saveStudent(studentDTO);

		assertEquals(studentDTO.fullName(), created.fullName());
		assertEquals(studentDTO.emailAddress(), created.emailAddress());
	}

	@Test
	void givenValidStudentId_whenDeleteStudentByIdCalled_thenStudentIsDeleted() {
		final var id = UUID.randomUUID();

		doNothing().when(this.studentRepository).deleteById(id);

		this.studentService.deleteStudentById(id);

		verify(this.studentRepository).deleteById(id);
	}

	@Test
	void givenValidStudentIdAndDto_whenUpdateStudentByIdCalled_thenStudentIsUpdated() {
		final var id = UUID.randomUUID();
		final var updateStudentDTO = new StudentDTO(id, "John Updated", "jupdated@example.com");
		final var existingStudent = new Student(id, "John Doe", "johndoe@example.com");
		final var updatedStudent = new Student(id, "John Updated", "jupdated@example.com");

		when(this.studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));
		when(this.studentMapper.convertToDtoFromEntity(any(Student.class))).thenReturn(updateStudentDTO);
		when(this.studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

		final var result = this.studentService.updateStudentById(id, updateStudentDTO);

		assertNotNull(result);
		assertEquals(updateStudentDTO.id(), result.id());
		assertEquals(updateStudentDTO.fullName(), result.fullName());
		assertEquals(updateStudentDTO.emailAddress(), result.emailAddress());
	}

	@Test
	void givenStudentDtoWithNulls_whenUpdateStudentByIdCalled_thenIllegalArgumentExceptionIsThrown() {
		final var id = UUID.randomUUID();
		final var updateStudentDTO = new StudentDTO(id, null, null);
		final var existingStudent = new Student(id, "John Doe", "johndoe@example.com");

		final var expectedErrorMessage = "fullName is null";

		when(this.studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));

		final var illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> this.studentService.updateStudentById(id, updateStudentDTO));

		final var actualMessage = illegalArgumentException.getMessage();

		assertTrue(actualMessage.contains(expectedErrorMessage));
	}

	@Test
	void givenStudentDtoWithNullFullName_whenUpdateStudentByIdCalled_thenIllegalArgumentExceptionIsThrown() {
		final var id = UUID.randomUUID();
		final var updateStudentDTO = new StudentDTO(id, null, "jupdated@example.com");
		final var existingStudent = new Student(id, "John Doe", "johndoe@example.com");

		final var expectedErrorMessage = "fullName is null";

		when(this.studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));

		final var illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> this.studentService.updateStudentById(id, updateStudentDTO));

		final var actualMessage = illegalArgumentException.getMessage();

		assertTrue(actualMessage.contains(expectedErrorMessage));
	}

	@Test
	void givenStudentDtoWithNullEmailAddress_whenUpdateStudentByIdCalled_thenIllegalArgumentExceptionIsThrown() {
		final var id = UUID.randomUUID();
		final var updateStudentDTO = new StudentDTO(id, "John Updated", null);
		final var existingStudent = new Student(id, "John Doe", "johndoe@example.com");

		final var expectedErrorMessage = "emailAddress is null";

		when(this.studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));

		final var illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> this.studentService.updateStudentById(id, updateStudentDTO));

		final var actualMessage = illegalArgumentException.getMessage();

		assertTrue(actualMessage.contains(expectedErrorMessage));
	}

	@Test
	void givenStudentDtoWithInvalidEmailAddress_whenUpdateStudentByIdCalled_thenIllegalArgumentExceptionIsThrown() {
		final var id = UUID.randomUUID();
		final var updateStudentDTO = new StudentDTO(id, "John Updated", "@.@.com");
		final var existingStudent = new Student(id, "John Doe", "johndoe@example.com");

		final var expectedErrorMessage = "Invalid email address";

		when(this.studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));

		final var illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> this.studentService.updateStudentById(id, updateStudentDTO));

		final var actualMessage = illegalArgumentException.getMessage();

		assertTrue(actualMessage.contains(expectedErrorMessage));
	}

	@Test
	void givenSameStudentDto_whenUpdateStudentByIdCalled_thenDoesNothing() {
		final var id = UUID.randomUUID();
		final var updateStudentDTO = new StudentDTO(id, "John Doe", "johndoe@example.com");
		final var existingStudent = new Student(id, "John Doe", "johndoe@example.com");

		when(this.studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));
		when(this.studentMapper.convertToDtoFromEntity(any(Student.class))).thenReturn(updateStudentDTO);
		when(this.studentRepository.save(any(Student.class))).thenReturn(existingStudent);

		final var result = this.studentService.updateStudentById(id, updateStudentDTO);

		assertNotNull(result);
		assertEquals(updateStudentDTO.id(), result.id());
		assertEquals(updateStudentDTO.fullName(), result.fullName());
		assertEquals(updateStudentDTO.emailAddress(), result.emailAddress());
	}

	@Test
	void givenInvalidStudentId_whenUpdateStudentByIdCalled_thenResourceNotFoundExceptionIsThrown() {
		final var id = UUID.randomUUID();
		final var updateStudentDTO = new StudentDTO(id, "John Updated", "jupdated@example.com");

		final var expectedErrorMessage = "Student does not exist with id: %s".formatted(id);

		when(this.studentRepository.findById(id)).thenReturn(Optional.empty());

		final var resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
				() -> this.studentService.updateStudentById(id, updateStudentDTO));

		final var actualMessage = resourceNotFoundException.getMessage();

		assertTrue(actualMessage.contains(expectedErrorMessage));

	}

	@Test
	void givenStudentsExist_whenGetAllStudentsCalled_thenStudentsAreReturned() {
		final var studentModel1 = new Student(UUID.randomUUID(), "John Doe", "jdoe@example.com");
		final var studentModel2 = new Student(UUID.randomUUID(), "Jane Doe", "jadoe@example.com");
		final var students = Arrays.asList(studentModel1, studentModel2);

		final var studentDto1 = new StudentDTO(studentModel1.getId(), studentModel1.getFullName(), studentModel1.getEmailAddress());
		final var studentDto2 = new StudentDTO(studentModel2.getId(), studentModel2.getFullName(), studentModel2.getEmailAddress());

		final var addressId = UUID.randomUUID();
		final var addressDTO = new AddressDTO(addressId, "Random Street 123");

		final var requestHeadersUriMock = mock(RequestHeadersUriSpec.class);
		final var requestHeadersMock = mock(RequestHeadersSpec.class);
		final var responseMock = mock(ResponseSpec.class);

		when(this.studentRepository.findAll()).thenReturn(students);
		when(this.studentMapper.convertToDtoFromEntity(studentModel1)).thenReturn(studentDto1);
		when(this.studentMapper.convertToDtoFromEntity(studentModel2)).thenReturn(studentDto2);

		when(this.webClient.get()).thenReturn(requestHeadersUriMock);
		when(requestHeadersUriMock.uri(anyString())).thenReturn(requestHeadersMock);
		when(requestHeadersMock.retrieve()).thenReturn(responseMock);
		when(responseMock.bodyToMono(AddressDTO.class)).thenAnswer(invocation -> Mono.just(addressDTO));

		final var result = this.studentService.getAllStudents();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("John Doe", result.get(0).studentDTO().fullName());
		assertEquals("jdoe@example.com", result.get(0).studentDTO().emailAddress());
		assertEquals(addressId, result.get(0).addressDTO().id());
		assertEquals("Random Street 123", result.get(0).addressDTO().address());
		assertEquals("Jane Doe", result.get(1).studentDTO().fullName());
		assertEquals("jadoe@example.com", result.get(1).studentDTO().emailAddress());
		assertEquals(addressId, result.get(1).addressDTO().id());
		assertEquals("Random Street 123", result.get(1).addressDTO().address());
	}

}
