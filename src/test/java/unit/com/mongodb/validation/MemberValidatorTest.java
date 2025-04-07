package unit.com.mongodb.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.dto.MemberDto;
import com.mongodb.validation.MemberValidator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
public class MemberValidatorTest {

	@Mock
	private Validator validator;

	@InjectMocks
	private MemberValidator memberValidator;

	private MemberDto memberDto;

	public void setUp() {
		memberDto = new MemberDto();
		memberDto.setId("1");
		memberDto.setName("John Doe");
		memberDto.setEmail("john.doe@example.com");
	}

	@Test
	public void testValidateMember_NoViolations() {
		when(validator.validate(memberDto)).thenReturn(Set.of());

		memberValidator.validateMember(memberDto);
	}

	@Test
	public void testValidateMember_WithViolations() {
		ConstraintViolation<MemberDto> violation = mock(ConstraintViolation.class);
		when(violation.getMessage()).thenReturn("must not be null");
		Set<ConstraintViolation<MemberDto>> violations = new HashSet<>();
		violations.add(violation);

		when(validator.validate(memberDto)).thenReturn(violations);

		assertThrows(ConstraintViolationException.class, () -> {
			memberValidator.validateMember(memberDto);
		});
	}
}