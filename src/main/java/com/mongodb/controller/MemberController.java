package com.mongodb.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.dto.MemberDto;
import com.mongodb.service.MemberService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
		RequestMethod.OPTIONS,
		RequestMethod.HEAD }, allowCredentials = "true")
@RestController
@Validated
@RequestMapping("/members")
public class MemberController {

	@Autowired
	private MemberService memberService;

	// Get member by ID
	@GetMapping("/{id}")
	public ResponseEntity<MemberDto> getMemberById(@PathVariable("id") String id) {
		Optional<MemberDto> member = memberService.findById(id);
		if (member.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(member.get());
	}

	// Get all members
	@GetMapping
	public ResponseEntity<List<MemberDto>> getMembers(@RequestParam Map<String, String> requestParams) {
		Optional<List<MemberDto>> members = memberService.getMembers(requestParams);
		return members.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	// Create a new member
	@PostMapping(consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> createMember(@RequestBody @Valid MemberDto member) {
		String memberId = memberService.saveMember(member);
		return new ResponseEntity<>(memberId, HttpStatus.CREATED);
	}
}
