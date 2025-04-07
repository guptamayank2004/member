
package unit.com.mongodb.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import com.mongodb.exception.MemberExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.ValidationException;

public class MemberExceptionHandlerTest {

	private MemberExceptionHandler exceptionHandler;
	private WebRequest webRequest;

	@BeforeEach
	public void setUp() {
		exceptionHandler = new MemberExceptionHandler();
		webRequest = mock(WebRequest.class);
	}

	@Test
	public void testHandleAllExceptions_GenericException() {
		Exception ex = new Exception("Generic error");
		ResponseEntity<Map<String, String>> response = exceptionHandler.handleAllExceptions(ex, webRequest);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Generic error", response.getBody().get("error"));
	}

	@Test
	public void testHandleAllExceptions_ConstraintViolationException() {
		ConstraintViolation<?> violation = mock(ConstraintViolation.class);
		Path path = mock(Path.class);
		when(path.toString()).thenReturn("field");
		when(violation.getPropertyPath()).thenReturn(path);
		when(violation.getMessage()).thenReturn("must not be null");

		Set<ConstraintViolation<?>> violations = Set.of(violation);
		ConstraintViolationException ex = new ConstraintViolationException(violations);

		ResponseEntity<Map<String, String>> response = exceptionHandler.handleAllExceptions(ex, webRequest);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("must not be null", response.getBody().get("field"));
	}

	@Test
	public void testHandleAllExceptions_ValidationException() {
		ValidationException ex = new ValidationException("Email taken");
		ResponseEntity<Map<String, String>> response = exceptionHandler.handleAllExceptions(ex, webRequest);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals("Email taken", response.getBody().get("email"));
	}
}