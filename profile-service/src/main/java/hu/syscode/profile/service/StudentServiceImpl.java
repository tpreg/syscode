package hu.syscode.profile.service;

import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import hu.syscode.profile.StudentRepository;
import hu.syscode.profile.dto.AddressDTO;
import hu.syscode.profile.dto.ResponseDTO;
import hu.syscode.profile.dto.StudentDTO;
import hu.syscode.profile.exception.ResourceNotFoundException;
import hu.syscode.profile.mapper.StudentMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

	private final WebClient webClient;

	private final StudentRepository studentRepository;

	private final StudentMapper studentMapper;

	public StudentServiceImpl(final WebClient webClient, final StudentRepository studentRepository, final StudentMapper studentMapper) {
		notNull(webClient, "webClient is null");
		notNull(studentRepository, "studentRepository is null");
		notNull(studentMapper, "studentRepository is null");
		this.webClient = webClient;
		this.studentRepository = studentRepository;
		this.studentMapper = studentMapper;
	}

	@Override
	public StudentDTO saveStudent(final StudentDTO studentDTO) {
		final var student = this.studentMapper.convertToEntityFromDto(studentDTO);
		final var savedStudent = this.studentRepository.save(student);
		log.info("Student saved with ID: {}", savedStudent.getId());
		return this.studentMapper.convertToDtoFromEntity(savedStudent);
	}

	@Override
	public void deleteStudentById(final UUID studentId) {
		this.studentRepository.deleteById(studentId);
		log.info("Student deleted with ID: {}", studentId);
	}

	@Override
	public StudentDTO updateStudentById(final UUID studentId, final StudentDTO studentDTO) {
		log.info("Attempting to update student with ID: {}", studentId);
		final var student = this.studentRepository.findById(studentId) //
				.orElseThrow(resourceNotFoundExceptionSupplier(studentId));
		log.info("Saving updated student with ID: {}", studentId);
		student.update(studentDTO.fullName(), studentDTO.emailAddress());
		this.studentRepository.save(student);
		log.info("Student with ID: {} successfully updated", studentId);
		return this.studentMapper.convertToDtoFromEntity(student);
	}

	private static Supplier<ResourceNotFoundException> resourceNotFoundExceptionSupplier(final UUID studentId) {
		return () -> {
			log.error("Student not found with ID: {}", studentId);
			return new ResourceNotFoundException("Student does not exist with id: %s".formatted(studentId));
		};
	}

	@Override
	public List<ResponseDTO> getAllStudents() {
		log.info("Retrieving all students");
		return this.studentRepository.findAll() //
				.stream() //
				.map(this.studentMapper::convertToDtoFromEntity) //
				.map(this::asResponseDTO) //
				.toList();
	}

	private ResponseDTO asResponseDTO(final StudentDTO studentDTO) {
		return new ResponseDTO(studentDTO, getAddress(UUID.randomUUID().toString()));
	}

	private AddressDTO getAddress(final String addressId) {
		return this.webClient.get() //
				.uri("http://localhost:8081/api/addresses/" + addressId) //
				.retrieve() //
				.bodyToMono(AddressDTO.class) //
				.block();
	}

}
