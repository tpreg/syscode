package hu.syscode.profile.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import hu.syscode.profile.dto.StudentDTO;
import hu.syscode.profile.model.Student;

@SpringBootTest
public class StudentMapperTest {

	@Autowired
	private StudentMapper studentMapper;

	@Test
	void givenStudentDTOWithNulls_whenMaps_thenCorrect() {
		final var dto = new StudentDTO(null, null, null);
		final var entity = this.studentMapper.convertToEntityFromDto(dto);
		assertNull(entity.getFullName());
		assertNull(entity.getEmailAddress());
	}

	@Test
	void givenStudentEntityWithNulls_whenMaps_thenCorrect() {
		final var entity = new Student(null, null, null);
		final var dto = this.studentMapper.convertToDtoFromEntity(entity);
		assertNull(dto.fullName());
		assertNull(dto.emailAddress());
	}

	@Test
	void givenStudentDTO_whenMaps_thenCorrect() {
		final var dto = new StudentDTO(null, "John Doe", "jdoe@gmail.com");
		final var entity = this.studentMapper.convertToEntityFromDto(dto);
		assertEquals(dto.fullName(), entity.getFullName());
		assertEquals(dto.emailAddress(), entity.getEmailAddress());
	}

	@Test
	void givenStudentEntity_whenMaps_thenCorrect() {
		final var entity = new Student(null, "John Doe", "jdoe@gmail.com");
		final var dto = this.studentMapper.convertToDtoFromEntity(entity);
		assertEquals(entity.getFullName(), dto.fullName());
		assertEquals(entity.getEmailAddress(), dto.emailAddress());
	}

	@Test
	void givenNullStudentDTO_whenMaps_thenCorrect() {
		final var entity = this.studentMapper.convertToEntityFromDto(null);
		assertNull(entity);
	}

	@Test
	void givenNullStudentEntity_whenMaps_thenCorrect() {
		final var dto = this.studentMapper.convertToDtoFromEntity(null);
		assertNull(dto);
	}

}
