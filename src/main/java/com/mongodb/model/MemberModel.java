package com.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Builder;
import lombok.Data;

@Document(collection = "MEMBERS")
@Data
@Builder
public class MemberModel {

	@Id
	private String id;
	private String name;
	private String email;
	@Field(name = "phone_number")
	private String phoneNumber;
}
