
package component.com.mongodb;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.dto.MemberDto;
import com.mongodb.service.MemberService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = "com.mongodb.controller")
public class MemberControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	private MemberDto memberDto;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		memberDto = new MemberDto();
		memberDto.setName("John Doe");
		memberDto.setEmail("john.doe@example.com");
		memberDto.setPhoneNumber("123456789");
	}

	@Test
	public void testGetMemberById() throws Exception {
		Mockito.when(memberService.findById("1")).thenReturn(Optional.of(memberDto));

		mockMvc.perform(get("/members/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("John Doe"));
	}

	@Test
	public void testGetMembers() throws Exception {
		Mockito.when(memberService.getMembers(Collections.emptyMap()))
				.thenReturn(Optional.of(Collections.singletonList(memberDto)));

		mockMvc.perform(get("/members")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("John Doe"));
	}

	@Test
	public void testCreateMember() throws Exception {
		Mockito.when(memberService.saveMember(Mockito.any(MemberDto.class))).thenReturn("1");

		mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberDto))).andExpect(status().isCreated());
	}

	@Configuration
	static class TestConfig {
		@Bean
		public ObjectMapper objectMapper() {
			return new ObjectMapper();
		}
	}
}