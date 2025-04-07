package com.mongodb.validation;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.dto.MemberDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Component
public class MemberValidator {

	@Autowired
	private Validator validator;

	public void validateMember(MemberDto member) throws ConstraintViolationException {
		// Create a bean validator and check for issues.
		Set<ConstraintViolation<MemberDto>> violations = validator.validate(member);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<>(violations));
		}
	}
}
