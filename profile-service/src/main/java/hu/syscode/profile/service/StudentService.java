package hu.syscode.profile.service;

import java.util.List;
import java.util.UUID;

import hu.syscode.profile.dto.ResponseDTO;
import hu.syscode.profile.dto.StudentDTO;

public interface StudentService {

	StudentDTO saveStudent(StudentDTO studentDTO);

	void deleteStudentById(UUID studentId);

	StudentDTO updateStudentById(UUID studentId, StudentDTO studentDTO);

	List<ResponseDTO> getAllStudents();

}
