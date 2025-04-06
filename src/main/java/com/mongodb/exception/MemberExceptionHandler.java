
package com.mongodb.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

@ControllerAdvice
public class MemberExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex, WebRequest request) {
		// Handle generic exceptions
		Map<String, String> responseObj = new HashMap<>();
		responseObj.put("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleAllExceptions(ConstraintViolationException ce,
			WebRequest request) {
		return createViolationResponse(ce.getConstraintViolations());
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<Map<String, String>> handleAllExceptions(ValidationException ex, WebRequest request) {
		// Handle the unique constrain violation
		Map<String, String> responseObj = new HashMap<>();
		responseObj.put("email", "Email taken");
		return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
	}

	private ResponseEntity<Map<String, String>> createViolationResponse(Set<ConstraintViolation<?>> violations) {
		Map<String, String> responseObj = new HashMap<>();
		for (ConstraintViolation<?> violation : violations) {
			responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
	}
}