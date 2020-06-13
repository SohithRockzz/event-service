package com.ent.qbthon.event.mapper;

import java.util.ArrayList;

import java.util.List;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.ent.qbthon.event.entity.Event;
import com.ent.qbthon.event.entity.UserEvent;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
@Mapper(componentModel = "spring")
public class  UserEventListMapper {
	

	 public List<UserEvent>  map(List<String> userList, Event event, String role, boolean reminderFlag, boolean isNominated){
		log.info("Inside UserEventListMapper--->");
		List<UserEvent> userEventList=new ArrayList<>();
		userList.parallelStream().forEach(userId->{
			UserEvent userEvent=new UserEvent();
			userEvent.setReminderFlag(reminderFlag);
			userEvent.setNominated(isNominated);
			userEvent.setRole(role);
			userEvent.setUserId(userId);
			userEvent.setEventId(event.getId());
			
			userEventList.add(userEvent);
			
		});
		return userEventList;
		
	}
}