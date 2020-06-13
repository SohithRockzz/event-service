package com.ent.qbthon.event.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ent.qbthon.event.entity.Event;
import com.ent.qbthon.event.entity.User;
import com.ent.qbthon.event.model.EventDetails;

public interface EventService {
	
	public Event saveEvent(String newEvent, MultipartFile userExcelDataFile, MultipartFile smeExcelDataFile);
	
	public String nominateEvent(String userId, String eventId);
	
	public User saveUser(User newEvent);
	
	public Map<String,List<EventDetails>> getUserEvents(String userId);
	
	public Map<String,List<Event>> getAllEvents();
	
	public List<EventDetails> getEventDetails();
	
	public User validateUser(User user);
}