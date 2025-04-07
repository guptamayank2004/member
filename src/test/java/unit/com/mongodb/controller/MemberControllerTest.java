package unit.com.mongodb.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mongodb.controller.MemberController;
import com.mongodb.dto.MemberDto;
import com.mongodb.service.MemberService;

public class MemberControllerTest {

	@InjectMocks
	private MemberController memberController;

	@Mock
	private MemberService memberService;

	private MemberDto memberDto;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		memberDto = new MemberDto();
		memberDto.setId("1L");
		memberDto.setName("John Doe");
		memberDto.setEmail("john.doe@example.com");
	}

	@Test
	public void testGetMemberById() {
		when(memberService.findById("1")).thenReturn(Optional.of(memberDto));

		ResponseEntity<MemberDto> response = memberController.getMemberById("1");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("John Doe", response.getBody().getName());
	}

	@Test
	public void testGetMembers() {
		when(memberService.getMembers(Map.of())).thenReturn(Optional.of(Collections.singletonList(memberDto)));

		ResponseEntity<List<MemberDto>> response = memberController.getMembers(Map.of());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		assertEquals("John Doe", response.getBody().get(0).getName());
	}

	@Test
	public void testCreateMember() {
		when(memberService.saveMember(memberDto)).thenReturn("1");

		ResponseEntity<String> response = memberController.createMember(memberDto);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("1", response.getBody());
	}
}
