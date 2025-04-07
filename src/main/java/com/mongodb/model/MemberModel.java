package com.mongodb.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "MEMBERS")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberModel {

	@Id
	private String id;
	private String name;
	private String email;
	@Field(name = "phone_number")
	private String phoneNumber;
}
