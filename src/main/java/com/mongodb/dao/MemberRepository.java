package com.mongodb.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mongodb.model.MemberModel;

@Repository
public interface MemberRepository extends MongoRepository<MemberModel, String> {

	Optional<MemberModel> findById(String id);
}
