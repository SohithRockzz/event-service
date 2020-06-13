package com.ent.qbthon.event.repository;

import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.ent.qbthon.event.entity.User;

@EnableScan
public interface UserRepository extends CrudRepository<User, String> {
	
	User findByIdAndPassword(String id, String password); 
	
	Optional<User> findById(String id);
}
