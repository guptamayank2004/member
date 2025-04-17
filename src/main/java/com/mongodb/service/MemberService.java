package com.mongodb.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mongodb.dao.MemberRepository;
import com.mongodb.dto.MemberDto;
import com.mongodb.model.MemberModel;
import com.mongodb.validation.MemberValidator;

@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberValidator memberValidator;
	private String EMAIL_ERR = "Email already exists";

	public String saveMember(MemberDto member) {
		memberValidator.validateMember(member);
		if (emailAlreadyExists(member.getEmail())) {
			throw new IllegalArgumentException(EMAIL_ERR);
		}
		MemberModel memberModel = MemberModel.builder().build();
		BeanUtils.copyProperties(member, memberModel);
		MemberModel savedMember = memberRepository.save(memberModel);
		return savedMember.getId();
	}

	public Optional<List<MemberDto>> getMembers(Map<String, String> requestParams) {
		List<MemberModel> memberModels;
		if (requestParams.isEmpty()) {
			memberModels = memberRepository.findAll(Sort.by("name").ascending());
		} else {
			MemberModel memberModelExample = MemberModel.builder().build();
			if (requestParams.containsKey("name")) {
				memberModelExample.setName(requestParams.get("name"));
				memberModels = memberRepository.findAll(Example.of(memberModelExample), Sort.by("name").ascending());
			} else {
				memberModels = memberRepository.findAll(Sort.by("name").ascending());
			}
		}

		List<MemberDto> memberDtos = memberModels.stream().map(this::convertToDto).collect(Collectors.toList());
		return Optional.ofNullable(memberDtos);
	}

	public Optional<MemberDto> findById(String id) {
		return Optional.ofNullable(memberRepository.findById(id)).map(memberModel -> {
			MemberDto memberDto = MemberDto.builder().build();
			BeanUtils.copyProperties(memberModel, memberDto);
			return memberDto;
		});
	}

	public boolean emailAlreadyExists(String email) {
		return memberRepository.exists(Example.of(MemberModel.builder().email(email).build()));
	}

	private MemberDto convertToDto(MemberModel memberModel) {
		MemberDto memberDto = MemberDto.builder().build();
		BeanUtils.copyProperties(memberModel, memberDto);
		return memberDto;
	}
}