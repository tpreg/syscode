package hu.syscode.profile.service;

import static hu.syscode.profile.mapper.MapStructConverter.MAPPER;
import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import hu.syscode.profile.StudentRepository;
import hu.syscode.profile.dto.AddressDTO;
import hu.syscode.profile.dto.StudentDTO;
import hu.syscode.profile.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

	private final WebClient webClient;

	private final StudentRepository studentRepository;

	public StudentServiceImpl(final WebClient webClient, final StudentRepository studentRepository) {
		notNull(webClient, "webClient is null");
		notNull(studentRepository, "webClient is null");
		this.webClient = webClient;
		this.studentRepository = studentRepository;
	}

	@Override
	public StudentDTO saveStudent(final StudentDTO studentDTO) {
		final var student = MAPPER.toEntity(studentDTO);
		final var savedStudent = this.studentRepository.save(student);
		return MAPPER.toDto(savedStudent);
	}

	@Override
	public StudentDTO getStudentById(final UUID studentId) {
		final var student = this.studentRepository.findById(studentId) //
				.orElseThrow(resourceNotFoundExceptionSupplier(studentId));
		return MAPPER.toDto(student);
	}

	private static Supplier<ResourceNotFoundException> resourceNotFoundExceptionSupplier(final UUID studentId) {
		return () -> new ResourceNotFoundException("Student does not exist with id: %s".formatted(studentId));
	}

	@Override
	public void deleteStudentById(final UUID studentId) {
		this.studentRepository.findById(studentId).ifPresent(this.studentRepository::delete);

	}

	@Override
	public StudentDTO updateStudentById(final UUID studentId, final StudentDTO studentDTO) {
		final var student = this.studentRepository.findById(studentId) //
				.orElseThrow(resourceNotFoundExceptionSupplier(studentId));
		return MAPPER.toDto(student);
	}

	@Override
	public List<StudentDTO> getStudents() {
		return this.studentRepository.findAll().stream().map(MAPPER::toDto).toList();
	}

	public Mono<AddressDTO> getAddress(final String addressId) {
		return this.webClient.get().uri("http://address-service/api/addresses/" + addressId).retrieve().bodyToMono(AddressDTO.class).;
	}

}
