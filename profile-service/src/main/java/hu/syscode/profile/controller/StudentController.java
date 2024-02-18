package hu.syscode.profile.controller;

import static org.springframework.http.HttpStatus.CREATED;

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
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/students")
public class StudentController {

	private final StudentService studentService;

	@Autowired
	public StudentController(final StudentService studentService) {
		this.studentService = studentService;
	}

	@GetMapping("/")
	public ResponseEntity<List<StudentDTO>> listStudents() {
		return ResponseEntity.ok(this.studentService.getStudents());
	}

	@PostMapping("/")
	public ResponseEntity<StudentDTO> saveStudent(@RequestBody final StudentDTO studentDTO) {
		return new ResponseEntity<>(this.studentService.saveStudent(studentDTO), CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<StudentDTO> getStudent(@PathVariable final UUID id) {
		return ResponseEntity.ok(this.studentService.getStudentById(id));
	}

	@GetMapping("/{id}/address")
	public Mono<ResponseEntity<StudentDTO>> getStudentWithAddress(@PathVariable final UUID id) {
		return this.studentService.getStudentById(id) //
				.flatMap(studentDTO -> this.studentService.getAddress(UUID.randomUUID().toString()) //
						.map(addressDTO -> {
							final ResponseDTO responseDTO = new ResponseDTO(studentDTO, addressDTO);
							return ResponseEntity.ok(responseDTO);
						})) //
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<StudentDTO> updateStudent(@PathVariable final UUID id, @RequestBody final StudentDTO studentDTO) {
		return ResponseEntity.ok(this.studentService.updateStudentById(id, studentDTO));
	}

	@DeleteMapping("/{id}")
	public void deleteStudent(@PathVariable final UUID id) {
		this.studentService.deleteStudentById(id);
	}

}
