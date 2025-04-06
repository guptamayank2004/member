package com.mongodb.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.model.MemberModel;

@Repository
public interface MemberRepository extends MongoRepository<MemberModel, String> {

	MemberModel findByName(String name);

	Optional<MemberModel> findById(String id);

	Optional<MemberModel> findByEmail(String email);

	@Query(value = "{}")
	List<MemberModel> findAll();

	List<MemberModel> findAllByOrderByNameAsc();

	public long count();
}
