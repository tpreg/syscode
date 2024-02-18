package hu.syscode.profile.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import hu.syscode.profile.dto.StudentDTO;
import reactor.core.publisher.Mono;

public interface StudentService {

	StudentDTO saveStudent(StudentDTO studentDTO);

	StudentDTO getStudentById(UUID studentId);

	void deleteStudentById(UUID studentId);

	StudentDTO updateStudentById(UUID studentId, StudentDTO studentDTO);

	List<StudentDTO> getStudents();

	Mono<ResponseEntity<StudentDTO>> getStudentByIdWithAddress(UUID id);

}
