package com.ent.qbthon.event.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ent.qbthon.event.entity.Event;
import com.ent.qbthon.event.entity.User;
import com.ent.qbthon.event.model.EventDetails;
import com.ent.qbthon.event.service.EventService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/event")
public class EventController {
	@Autowired
	EventService eventService;

	@PostMapping(value = "/create", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Event> createEvent(@RequestParam("assocFile") MultipartFile userExcelDataFile,
			@RequestParam("smeFile") MultipartFile smeExcelDataFile, @RequestParam("event") String newEvent) {
		log.info("Inside createEvent Controller --->");
		return new ResponseEntity<>(eventService.saveEvent(newEvent, userExcelDataFile, smeExcelDataFile),
				HttpStatus.OK);
	}

	@GetMapping(value = "/getUserEvents", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, List<EventDetails>>> getUserEvents(@RequestParam("userId") String userId) {
		log.info("Inside getEventOfType Controller --->");
		return new ResponseEntity<>(eventService.getUserEvents(userId), HttpStatus.OK);

	}

	@GetMapping(value = "/getAllEvents", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Map<String, List<Event>>> getAllEvents() {
		log.info("Getting Events for Admin::::");
		return new ResponseEntity<Map<String, List<Event>>>(eventService.getAllEvents(), HttpStatus.OK);

	}

	@GetMapping(value = "/details", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<EventDetails>> getEventDetails() {
		log.info("Inside getEventOfType Controller --->");
		return new ResponseEntity<>(eventService.getEventDetails(), HttpStatus.OK);

	}

	@GetMapping(value = "/nominate", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> nominateEvent(@RequestParam("userId") String userId,
			@RequestParam("eventId") String eventId) throws IOException {
		log.info("Inside nominateEvent Controller --->");
		return new ResponseEntity<>(eventService.nominateEvent(userId, eventId), HttpStatus.OK);
	}

	@PostMapping(value = "/createUser", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<User> creatUser(@RequestBody(required = true) User newUser) {
		log.info("Inside creatUser Controller --->");
		return new ResponseEntity<>(eventService.saveUser(newUser), HttpStatus.OK);
	}

	@PostMapping(value = "/validateUser", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<User> validateUser(@RequestBody(required = true) User newUser) {
		log.info("Inside Validate User Controller --->");
		return new ResponseEntity<>(eventService.validateUser(newUser), HttpStatus.OK);
	}

}