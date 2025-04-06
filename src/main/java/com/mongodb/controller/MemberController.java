package com.mongodb.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.dto.MemberDto;
import com.mongodb.service.MemberService;

@RestController
@RequestMapping("/members")
public class MemberController {

	@Autowired
	private MemberService memberService;

	// Get member by ID
	@GetMapping("/{id:[0-9]+}")
	public ResponseEntity<MemberDto> getMemberById(@PathVariable("id") String id) {
		Optional<MemberDto> member = memberService.findById(id);
		if (member.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(member.get());
	}

	// Get member by Name
	@GetMapping
	public ResponseEntity<MemberDto> getMemberByName(@RequestParam(name = "name") String name) {
		Optional<MemberDto> member = memberService.findByName(name);
		return member.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	// Get all members
	@GetMapping
	public ResponseEntity<List<MemberDto>> getAllMembers() {
		Optional<List<MemberDto>> members = memberService.getAllMembers();
		return members.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	// Create a new member
	@PostMapping
	public ResponseEntity<String> createMember(@RequestBody MemberDto member) {
		String memberId = memberService.saveMember(member);
		return new ResponseEntity<>(memberId, HttpStatus.CREATED);
	}
}
