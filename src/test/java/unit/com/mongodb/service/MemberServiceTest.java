package unit.com.mongodb.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.dao.MemberRepository;
import com.mongodb.dto.MemberDto;
import com.mongodb.model.MemberModel;
import com.mongodb.service.MemberService;
import com.mongodb.validation.MemberValidator;

@ExtendWith(SpringExtension.class)
public class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MemberValidator memberValidator;

	@InjectMocks
	private MemberService memberService;

	private MemberDto memberDto;
	private MemberModel memberModel;

	@BeforeEach
	public void setUp() {
		memberDto = new MemberDto();
		memberDto.setId("1");
		memberDto.setName("John Doe");
		memberDto.setEmail("john.doe@example.com");

		memberModel = new MemberModel();
		BeanUtils.copyProperties(memberDto, memberModel);
	}

	@Test
	public void testSaveMember() {
		when(memberRepository.save(any(MemberModel.class))).thenReturn(memberModel);
		when(memberRepository.exists(any(Example.class))).thenReturn(false);

		String memberId = memberService.saveMember(memberDto);
		assertEquals("1", memberId);
	}

	@Test
	public void testSaveMember_EmailAlreadyExists() {
		when(memberRepository.exists(any(Example.class))).thenReturn(true);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			memberService.saveMember(memberDto);
		});

		assertEquals("Email already exists", exception.getMessage());
	}

	@Test
	void testGetMembers_EmptyRequestParams() {
		List<MemberModel> mockMembers = Arrays.asList(MemberModel.builder().id("1").name("John").build(),
				MemberModel.builder().id("2").name("Jane").build());
		when(memberRepository.findAll(Sort.by("name").ascending())).thenReturn(mockMembers);

		Optional<List<MemberDto>> result = memberService.getMembers(Collections.emptyMap());

		assertTrue(result.isPresent());
		assertEquals(2, result.get().size());
		assertEquals("John", result.get().get(0).getName());
		assertEquals("Jane", result.get().get(1).getName());
	}

	@Test
	void testGetMembers_WithNameFilter() {
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("name", "John");

		List<MemberModel> mockMembers = Collections.singletonList(MemberModel.builder().id("1").name("John").build());
		when(memberRepository.findAll(eq(Example.of(MemberModel.builder().name("John").build())), any(Sort.class)))
				.thenReturn(mockMembers);

		Optional<List<MemberDto>> result = memberService.getMembers(requestParams);

		assertTrue(result.isPresent());
		assertEquals(1, result.get().size());
		assertEquals("John", result.get().get(0).getName());
	}

	@Test
	void testGetMembers_WithOtherFilter() {
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("phone", "123456789");

		List<MemberModel> mockMembers = Arrays.asList(MemberModel.builder().id("1").name("John").build(),
				MemberModel.builder().id("2").name("Jane").build());
		when(memberRepository.findAll(Sort.by("name").ascending())).thenReturn(mockMembers);

		Optional<List<MemberDto>> result = memberService.getMembers(requestParams);

		assertTrue(result.isPresent());
		assertEquals(2, result.get().size());
	}

	@Test
	void testGetMembers_NoMembersFound() {
		when(memberRepository.findAll(Sort.by("name").ascending())).thenReturn(Collections.emptyList());

		Optional<List<MemberDto>> result = memberService.getMembers(Collections.emptyMap());

		assertTrue(result.isPresent());
		assertTrue(result.get().isEmpty());
	}

	@Test
	public void testFindById() {
		when(memberRepository.findById("1")).thenReturn(Optional.of(memberModel));

		Optional<MemberDto> foundMember = memberService.findById("1");
		assertEquals(null, foundMember.get().getName());
	}

	@Test
	public void testEmailAlreadyExists() {
		when(memberRepository.exists(any(Example.class))).thenReturn(true);

		boolean exists = memberService.emailAlreadyExists("john.doe@example.com");
		assertEquals(true, exists);
	}

	@Test
	public void testSaveMember_RepositoryFailure() {
		when(memberRepository.save(any(MemberModel.class))).thenThrow(new RuntimeException("Database error"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			memberService.saveMember(memberDto);
		});
		assertEquals("Database error", exception.getMessage());
	}

	@Test
	public void testGetMembers_EmptyResult() {
		when(memberRepository.findAll(Sort.by("name").ascending())).thenReturn(Collections.emptyList());

		Optional<List<MemberDto>> members = memberService.getMembers(Collections.emptyMap());
		assertEquals(true, members.get().isEmpty());
	}

	@Test
	public void testFindById_NotFound() {
		when(memberRepository.findById("1")).thenReturn(Optional.empty());

		Optional<MemberDto> foundMember = memberService.findById("1");
		assertEquals(null, foundMember.get().getId());
	}

	@Test
	public void testEmailAlreadyExists_NullEmail() {
		assertDoesNotThrow(() -> {
			memberService.emailAlreadyExists(null);
		});
	}

}
