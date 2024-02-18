package hu.syscode.profile.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import hu.syscode.profile.dto.AddressDTO;
import hu.syscode.profile.dto.ResponseDTO;
import hu.syscode.profile.dto.StudentDTO;
import hu.syscode.profile.service.StudentService;

@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@WebMvcTest(StudentController.class)
public class StudentControllerTest {

	@Autowired
	private JacksonTester<StudentDTO> studentDtoJsonTester;

	@Autowired
	private JacksonTester<List<ResponseDTO>> responseDtoJsonTester;

	@MockBean
	private StudentService studentService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void whenGetAllStudents_thenReturnsOkStatusAndCorrectDetailsList() throws Exception {
		final var studentDTO = new StudentDTO(UUID.randomUUID(), "Ramon Gomes", "rgo@example.com");
		final var addressDTO = new AddressDTO(UUID.randomUUID(), "Random Street 123");
		final var responseDTO = new ResponseDTO(studentDTO, addressDTO);

		when(this.studentService.getAllStudents()).thenReturn(List.of(responseDTO));

		this.mockMvc.perform(get("/api/students/")) //
				.andExpect(status().isOk()) //
				.andExpect(content().contentType(APPLICATION_JSON)) //
				.andExpect(content().json(this.responseDtoJsonTester.write(List.of(responseDTO)).getJson()));

		verify(this.studentService, times(1)).getAllStudents();
	}

	@Test
	void givenValidStudentDto_whenSaveStudent_thenReturnsCreatedAndStudentDetails() throws Exception {
		final var id = UUID.randomUUID();
		final var inputDto = new StudentDTO(id, "Tatiana Castro", "tatiana.castro@yahoo.com");
		final var outputDto = new StudentDTO(id, "Tatiana Castro", "tatiana.castro@yahoo.com");
		given(this.studentService.saveStudent(ArgumentMatchers.any(StudentDTO.class))).willReturn(outputDto);
		this.mockMvc.perform(post("/api/students/") //
						.contentType(APPLICATION_JSON) //
						.content(this.studentDtoJsonTester.write(inputDto).getJson())) //
				.andExpect(status().isCreated()) //
				.andExpect(content().contentType(APPLICATION_JSON)) //
				.andExpect(content().json(this.studentDtoJsonTester.write(outputDto).getJson()));
	}

	@Test
	void givenValidIdAndStudent_whenUpdateStudent_thenReturnsOkStatusAndCorrectDetails() throws Exception {
		final var id = UUID.randomUUID();
		final var updatedStudentDTO = new StudentDTO(id, "Jane Doe", "jadoe@example.com");

		when(this.studentService.updateStudentById(id, updatedStudentDTO)).thenReturn(updatedStudentDTO);

		this.mockMvc.perform(put("/api/students/{id}", id) //
						.contentType(APPLICATION_JSON) //
						.content(this.studentDtoJsonTester.write(updatedStudentDTO).getJson())) //
				.andExpect(status().isOk()) //
				.andExpect(content().json(this.studentDtoJsonTester.write(updatedStudentDTO).getJson()));

		verify(this.studentService, times(1)).updateStudentById(id, updatedStudentDTO);
	}

	@Test
	void givenValidId_whenDeleteStudent_thenReturnsNoContentStatus() throws Exception {
		final var id = UUID.randomUUID();

		doNothing().when(this.studentService).deleteStudentById(id);

		this.mockMvc.perform(delete("/api/students/{id}", id)).andExpect(status().isNoContent());

		verify(this.studentService, times(1)).deleteStudentById(id);
	}

}
