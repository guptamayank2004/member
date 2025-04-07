package unit.com.mongodb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
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
	public void testGetMembers() {
		when(memberRepository.findAll(Sort.by("name").ascending())).thenReturn(Collections.singletonList(memberModel));

		Optional<List<MemberDto>> members = memberService.getMembers(Collections.emptyMap());
		assertEquals(1, members.get().size());
		assertEquals("John Doe", members.get().get(0).getName());
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
}
