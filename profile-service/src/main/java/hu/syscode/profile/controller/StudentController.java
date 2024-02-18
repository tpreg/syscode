package hu.syscode.profile.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.syscode.profile.dto.ResponseDTO;
import hu.syscode.profile.dto.StudentDTO;
import hu.syscode.profile.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/students")
@Tag(name = "Student", description = "The Student API")
public class StudentController {

	private final StudentService studentService;

	@Autowired
	public StudentController(final StudentService studentService) {
		this.studentService = studentService;
	}

	@GetMapping("/")
	@Operation(summary = "Get a list of students", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the list", content = {
					@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDTO.class)) }) })
	public ResponseEntity<List<ResponseDTO>> listStudents() {
		final var students = this.studentService.getAllStudents();
		return ResponseEntity.ok(students);
	}

	@PostMapping("/")
	@Operation(summary = "Create a new student", responses = {
			@ApiResponse(responseCode = "201", description = "Successfully created a new student", content = {
					@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StudentDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid data supplied", content = @Content) })
	public ResponseEntity<StudentDTO> saveStudent(@Valid @RequestBody final StudentDTO studentDTO) {
		final var savedStudentDTO = this.studentService.saveStudent(studentDTO);
		return new ResponseEntity<>(savedStudentDTO, CREATED);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a student by its id", responses = {
			@ApiResponse(responseCode = "200", description = "Successfully updated a student", content = {
					@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StudentDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied; Invalid data supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Student not found", content = @Content) })
	public ResponseEntity<StudentDTO> updateStudent(
			@Parameter(name = "id", description = "id of the student to be updated") @PathVariable final UUID id,
			@Valid @RequestBody final StudentDTO studentDTO) {
		final var updatedStudentDTO = this.studentService.updateStudentById(id, studentDTO);
		return ResponseEntity.ok(updatedStudentDTO);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a student by its id", responses = {
			@ApiResponse(responseCode = "204", description = "Successfully deleted a student", content = { @Content(mediaType = APPLICATION_JSON_VALUE) }) })
	public ResponseEntity<Void> deleteStudent(
			@Parameter(name = "id", description = "id of the student to be deleted") @PathVariable final UUID id) {
		this.studentService.deleteStudentById(id);
		return ResponseEntity.noContent().build();
	}

}
