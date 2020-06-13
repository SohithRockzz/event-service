package com.ent.qbthon.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ent.qbthon.event.entity.Event;
import com.ent.qbthon.event.model.EventUserDetails;

@Component
@Mapper(componentModel = "spring")
public interface EventUserDetailsMapper {
	
	@Mappings({
		@Mapping(target = "slot", source = "eventList.slot"),
		@Mapping(target = "date", source = "eventList.date"),
		@Mapping(target = "skills", source = "eventList.skills")
	})
	EventUserDetails  map(Event eventList);
	
	 
}