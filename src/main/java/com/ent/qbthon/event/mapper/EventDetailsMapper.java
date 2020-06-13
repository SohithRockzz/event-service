package com.ent.qbthon.event.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ent.qbthon.event.entity.Event;
import com.ent.qbthon.event.model.EventDetails;
import com.ent.qbthon.event.model.EventUserDetails;

@Component
@Mapper(componentModel = "spring")
public interface EventDetailsMapper {
	
	@Mappings({
		@Mapping(target = "id", source = "eventList.id"),
		@Mapping(target = "slot", source = "eventList.slot"),
		@Mapping(target = "date", source = "eventList.date"),
		@Mapping(target = "skills", source = "eventList.skills")
	})
	 EventDetails  map(Event eventList);
	 List<EventDetails>  map(List<Event> eventList);
	 
}
