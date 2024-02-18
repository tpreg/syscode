package hu.syscode.profile.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

import hu.syscode.profile.dto.AddressDTO;
import hu.syscode.profile.dto.ResponseDTO;
import hu.syscode.profile.dto.StudentDTO;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class StudentControllerIntegrationTest {

	private static final String STUDENT_API_PATH = "/api/students/";

	@LocalServerPort
	private int port;

	@Value("${my.security.auth-details}")
	private String authDetails;

	@Autowired
	private TestRestTemplate testRestTemplate;

	private WireMockServer wireMockServer;

	@BeforeEach
	void initializeWireMockServer() {
		this.wireMockServer = new WireMockServer(options().port(8081));
		this.wireMockServer.start();
		configureFor("localhost", this.wireMockServer.port());
	}

	@AfterEach
	void stopWireMockServer() {
		this.wireMockServer.stop();
	}

	@Test
	void givenValidStudent_whenSaveStudent_thenReturnsCreatedStatusAndCorrectDetails() {
		final var studentResponse = createStudent("John Doe", "jdoe@gmail.com");

		assertNotNull(studentResponse);
		assertEquals(CREATED, studentResponse.getStatusCode());

		final var studentDTO = studentResponse.getBody();
		assertNotNull(studentDTO);
		assertNotNull(studentDTO.id());
		assertEquals("John Doe", studentDTO.fullName());
		assertEquals("jdoe@gmail.com", studentDTO.emailAddress());
	}

	private ResponseEntity<StudentDTO> createStudent(final String fullName, final String emailAddress) {
		final var studentDtoForCreation = new StudentDTO(null, fullName, emailAddress);

		return this.testRestTemplate.postForEntity(createUrl(STUDENT_API_PATH), studentDtoForCreation, StudentDTO.class);
	}

	private String createUrl(final String apiPath) {
		return "http://localhost:" + this.port + apiPath;
	}

	@Test
	void givenStudentAdded_whenGetStudents_thenVerifiesAddedStudentAndAddress() throws JsonProcessingException {
		final var addressId = UUID.fromString("8b267eff-ee51-40b8-8b04-4dedd985c15d");
		final var addressDTO = new AddressDTO(addressId, "Portaelementum");

		setupMockServerStub(addressDTO);
		createStudent("Aisha Mehmood", "aisha.mehmood@gmail.com");

		final var responseEntity = getStudents();
		assertNotNull(responseEntity);

		final var responseBody = responseEntity.getBody();
		assertNotNull(responseBody);
		assertEquals(1, responseBody.size());

		final var responseDTO = responseBody.get(0);

		verify(getRequestedFor(urlMatching("/api/addresses/.*")));

		final var studentInResponse = responseDTO.studentDTO();
		assertEquals("Aisha Mehmood", studentInResponse.fullName());
		assertEquals("aisha.mehmood@gmail.com", studentInResponse.emailAddress());

		final var addressInResponse = responseDTO.addressDTO();
		assertEquals(addressId, addressInResponse.id());
		assertEquals("Portaelementum", addressInResponse.address());
	}

	private void setupMockServerStub(final AddressDTO addressDTO) throws JsonProcessingException {
		final var responseMapper = new ObjectMapper();

		this.wireMockServer.stubFor(get(urlMatching("/api/addresses/.*")).willReturn(aResponse().withHeader(CONTENT_TYPE,
				APPLICATION_JSON_VALUE).withBody(responseMapper.writeValueAsString(addressDTO)).withStatus(200)));
	}

	private ResponseEntity<List<ResponseDTO>> getStudents() {
		return this.testRestTemplate.exchange(createUrl(STUDENT_API_PATH), GET, basicAuth(), new ParameterizedTypeReference<>() {
		});
	}

	private HttpEntity<String> basicAuth() {
		final var base64Credentials = encodeToBase64(this.authDetails);
		final var headers = new HttpHeaders();

		headers.add("Authorization", "Basic " + base64Credentials);

		return new HttpEntity<>(headers);
	}

	private static String encodeToBase64(final String plainCredentials) {
		return Base64.getEncoder().encodeToString(plainCredentials.getBytes());
	}

	@Test
	void givenValidIdAndStudent_whenUpdateStudent_thenReturnsOkStatusAndCorrectDetails() {
		final var studentResponse = createStudent("John Doe", "john.doe@example.com");
		final var newStudentId = studentResponse.getBody().id();
		final var studentDTO = new StudentDTO(newStudentId, "John Doe", "john.doe@example.com");

		final var responseEntity = updateStudent(newStudentId, studentDTO);

		assertNotNull(responseEntity);
		assertEquals(OK, responseEntity.getStatusCode());
		final var responseEntityBody = responseEntity.getBody();
		assertNotNull(responseEntityBody);
		assertEquals(newStudentId, responseEntityBody.id());
		assertEquals(studentDTO.fullName(), responseEntityBody.fullName());
		assertEquals(studentDTO.emailAddress(), responseEntityBody.emailAddress());
	}

	private ResponseEntity<StudentDTO> updateStudent(final UUID id, final StudentDTO studentDTO) {
		final var headers = new HttpHeaders();
		headers.setContentType(APPLICATION_JSON);
		headers.setAccept(List.of(APPLICATION_JSON));

		final var entity = new HttpEntity<>(studentDTO, headers);

		return this.testRestTemplate.exchange(createUrl(STUDENT_API_PATH + id), PUT, entity, StudentDTO.class);
	}

	@Test
	void givenValidId_whenDeleteStudent_thenReturnsNoContentStatus() {
		final var id = UUID.randomUUID();

		final var responseEntity = deleteStudent(id);

		assertNotNull(responseEntity);
		assertEquals(NO_CONTENT, responseEntity.getStatusCode());
	}

	private ResponseEntity<Void> deleteStudent(final UUID id) {
		final var headers = new HttpHeaders();
		headers.setAccept(List.of(APPLICATION_JSON));

		final var entity = new HttpEntity<Void>(headers);

		return this.testRestTemplate.exchange(createUrl(STUDENT_API_PATH + id), DELETE, entity, Void.class);
	}

}
