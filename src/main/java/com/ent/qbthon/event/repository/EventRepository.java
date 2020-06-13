package com.ent.qbthon.event.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.ent.qbthon.event.entity.Event;

@EnableScan
public interface EventRepository extends CrudRepository<Event, String> {
	Optional<Event> findById(String id);
	//List<Event> findAllById(List<String> eventIds);

	List<Event> findAllByDate(Date date);
}
