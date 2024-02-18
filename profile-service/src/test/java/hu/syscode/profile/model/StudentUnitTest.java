package hu.syscode.profile.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class StudentUnitTest {

	public static final String INVALID_EMAIL_ADDRESS = "invalid.email";

	private static final String DEFAULT_FULL_NAME = "John Doe";

	private static final String DEFAULT_STUDENT_EMAIL = "john.doe@example.com";

	private static final String UPDATED_FULL_NAME = "Jane Doe";

	private static final String UPDATED_EMAIL_ADDRESS = "jane.doe@example.com";

	@Test
	void givenValidStudent_whenUpdate_thenStudentIsUpdated() {
		final var id = UUID.randomUUID();
		final var student = new Student(id, DEFAULT_FULL_NAME, DEFAULT_STUDENT_EMAIL);

		student.update(UPDATED_FULL_NAME, UPDATED_EMAIL_ADDRESS);

		assertEquals(id, student.getId());
		assertEquals(UPDATED_FULL_NAME, student.getFullName(), "fullName has not been updated correctly");
		assertEquals(UPDATED_EMAIL_ADDRESS, student.getEmailAddress(), "emailAddress has not been updated correctly");
	}

	@Test
	void givenSameData_whenUpdate_thenNoChange() {
		final var id = UUID.randomUUID();
		final var student = new Student(id, DEFAULT_FULL_NAME, DEFAULT_STUDENT_EMAIL);

		student.update(DEFAULT_FULL_NAME, DEFAULT_STUDENT_EMAIL);

		assertEquals(id, student.getId());
		assertEquals(DEFAULT_FULL_NAME, student.getFullName(), "fullName has not been updated correctly");
		assertEquals(DEFAULT_STUDENT_EMAIL, student.getEmailAddress(), "emailAddress has not been updated correctly");
	}

	@Test
	void givenInvalidEmailAddress_whenUpdate_thenIllegalArgumentExceptionIsThrown() {
		final var student = new Student(UUID.randomUUID(), DEFAULT_FULL_NAME, DEFAULT_STUDENT_EMAIL);

		assertThrows(IllegalArgumentException.class, () -> student.update(DEFAULT_FULL_NAME, INVALID_EMAIL_ADDRESS),
				"Invalid email should result in an exception");
	}

	@Test
	void givenNullFullName_whenUpdate_thenIllegalArgumentExceptionIsThrown() {
		final var student = new Student(UUID.randomUUID(), DEFAULT_FULL_NAME, DEFAULT_STUDENT_EMAIL);

		assertThrows(IllegalArgumentException.class, () -> student.update(null, DEFAULT_STUDENT_EMAIL),
				"Null fullName should result in an exception");
	}

	@Test
	void givenNullEmailAddress_whenUpdate_thenIllegalArgumentExceptionIsThrown() {
		final var student = new Student(UUID.randomUUID(), DEFAULT_FULL_NAME, DEFAULT_STUDENT_EMAIL);

		assertThrows(IllegalArgumentException.class, () -> student.update(DEFAULT_FULL_NAME, null),
				"Null emailAddress should result in an exception");
	}

}
