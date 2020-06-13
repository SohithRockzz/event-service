package com.ent.qbthon.event.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ent.qbthon.event.entity.Event;
import com.ent.qbthon.event.entity.User;
import com.ent.qbthon.event.entity.UserEvent;
import com.ent.qbthon.event.exception.QbthonEventServiceException;
import com.ent.qbthon.event.mapper.EventDetailsMapper;
import com.ent.qbthon.event.mapper.EventUserDetailsMapper;
import com.ent.qbthon.event.mapper.UserEventListMapper;
import com.ent.qbthon.event.model.EventDetails;
import com.ent.qbthon.event.model.EventUserDetails;
import com.ent.qbthon.event.repository.EventRepository;
import com.ent.qbthon.event.repository.UserEventRepository;
import com.ent.qbthon.event.repository.UserRepository;
import com.ent.qbthon.event.rest.EmailRestTemplate;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

	@Autowired
	EventRepository eventRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserEventRepository userEventRepository;

	@Autowired
	UserEventListMapper userEventListMapper;

	@Autowired
	EventDetailsMapper eventDetailsMapper;

	@Autowired
	EventUserDetailsMapper evenUsertDetailsMapper;

	@Autowired
	EmailRestTemplate emailRestTemplate;

	public User validateUser(User user) {
		log.info("Validating User::::");
		try {
			User validatedUser = userRepository.findByIdAndPassword(user.getId(), user.getPassword());
			if (validatedUser != null) {
				return validatedUser;
			} else {
				throw new QbthonEventServiceException("Unauthenticated Credentials. Provide a valid One...");
			}
		} catch (Exception e) {
			log.error("Error in Validating User Credentials:::", e);
			throw new QbthonEventServiceException("Error in Validating User Credentials:::", e);
		}

	}

	public User getUserDetails(String userId) {
		log.info("Inside getUserDetails method Event Service--->");
		try {
			User user = userRepository.findById(userId).orElse(null);
			if (user != null) {
				return user;
			} else {
				throw new QbthonEventServiceException("Unauthenticated Credentials. Provide a valid One...");
			}
		} catch (QbthonEventServiceException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error in getUserDetails", e);
			throw new QbthonEventServiceException("Something Went Wrong", e);
		}

	}

	public Event saveEvent(String event, MultipartFile userExcelDataFile, MultipartFile smeExcelDataFile) {
		log.info("Inside saveEvent method saveEvent Service--->");
		boolean remainderFlag = false;
		boolean isNominated = false;
		Event newEvent = new Event();
		try {
			Gson gson = new Gson();
			newEvent = gson.fromJson(event, Event.class);
			List<Event> eventList = eventRepository.findAllByDate(newEvent.getDate());
			if (eventList.size() < 2) {
				if (eventList.size() == 1) {
					if (eventList.get(0).getSlot().equals(newEvent.getSlot())) {
						throw new QbthonEventServiceException("Slot Is Occupied. Try Another Slot..");
					}
				}
			} else {
				log.error("Max No Of Event Created For That Day-->");
				throw new QbthonEventServiceException("Max No Of Event Created For That Day");
			}
			CompletableFuture<List<String>> userListFuture = CompletableFuture.supplyAsync(() -> {
				List<String> userList = new ArrayList<>();
				try {
					log.debug("Inside userListFuture of saveEvent method saveEvent Service--->");
					userList = getIdFromExcel(userExcelDataFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error("Error in saveEvent inside completableFuture-->", e);
					throw new QbthonEventServiceException("Something Wrong In User File", e);
				}
				return userList;
			});

			CompletableFuture<List<String>> smeListFurture = CompletableFuture.supplyAsync(() -> {
				List<String> smeList = new ArrayList<>();
				try {
					log.debug("Inside smeListFurture of saveEvent method saveEvent Service--->");
					smeList = getIdFromExcel(smeExcelDataFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error("Error in saveEvent inside completableFuture", e);
					throw new QbthonEventServiceException("Something Wrong In Sme File", e);
				}
				return smeList;
			});

			CompletableFuture.allOf(userListFuture, smeListFurture).join();
			List<String> userDistinctlist = userListFuture.join().stream().distinct().collect(Collectors.toList());
			List<String> smeDistinctlist = smeListFurture.join().stream().distinct().collect(Collectors.toList());
			if (!CollectionUtils.containsAny(userDistinctlist, smeDistinctlist)) {
				Event savedEvent = eventRepository.save(newEvent);
				List<UserEvent> userEventList = userEventListMapper.map(userDistinctlist, newEvent, "USER",
						remainderFlag, isNominated);
				List<UserEvent> smeEventList = userEventListMapper.map(smeDistinctlist, newEvent, "SME", remainderFlag,
						isNominated);
				userEventList.addAll(smeEventList);
				if (!CollectionUtils.isEmpty(smeEventList)) {
					Iterable<UserEvent> userEventSavedList = userEventRepository.saveAll(userEventList);
					if (!CollectionUtils.isEmpty((Collection<?>) userEventSavedList)) {
						CompletableFuture.runAsync(() -> {
							EventUserDetails eventUserDetails = evenUsertDetailsMapper.map(savedEvent);
							eventUserDetails.setUsersList(userListFuture.join());
							emailRestTemplate.sendEventInvitationMail(eventUserDetails);
						});
					}
				}
			} else {
				throw new QbthonEventServiceException("Same Associate Id Present In Both The File");
			}

		} catch (QbthonEventServiceException e) {
			log.error("Error in saveEvent", e);
			throw e;
		} catch (Exception e) {
			log.error("Error in saveEvent", e);
			throw new QbthonEventServiceException("Something Went Wrong", e);
		}

		return newEvent;
	}

	public List<EventDetails> getEventDetails() {
		List<Event> eventList = new ArrayList<>();
		List<EventDetails> eventDetailList = new ArrayList<>();
		try {
			eventList = (List<Event>) eventRepository.findAll();
			eventDetailList = eventDetailsMapper.map(eventList);
		} catch (QbthonEventServiceException e) {
			log.error("Error in saveEvent", e);
			throw e;
		} catch (Exception e) {
			log.error("Error in getEventDetails", e);
			throw new QbthonEventServiceException("Something Went Wrong", e);
		}
		return eventDetailList;
	}

	public String nominateEvent(String userId, String eventId) {
		log.info("Inside nominateEvent method saveEvent Service--->");
		UserEvent updateEvent = new UserEvent();
		try {
			UserEvent userEventDetail = userEventRepository.findByEventIdAndUserId(eventId, userId);
			Event event = eventRepository.findById(userEventDetail.getEventId()).orElse(null);
			if (event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now())) {
				log.debug("Inside LocalDate if statement of  nominateEvent method saveEvent Service--->");
				userEventDetail.setNominated(true);
				updateEvent = userEventRepository.save(userEventDetail);
				if (updateEvent.getId() != null) {
					return "Nominated to Event Successfully";
				} else {
					throw new QbthonEventServiceException("Error In Nominating In The Event");
				}
			} else {
				throw new QbthonEventServiceException("Sorry... This Event Got Expired!! ");
			}
		} catch (QbthonEventServiceException e) {
			log.error("Error in nominating in the Event", e);
			throw e;
		} catch (Exception e) {
			log.error("Error in nominateEvent", e);
			throw new QbthonEventServiceException("Something Went Wrong", e);
		}
	}

	private List<String> getIdFromExcel(MultipartFile excelDataFile) throws IOException {
		log.info("Inside getIdFromExcel method saveEvent Service--->");
		List<String> userIdList = new ArrayList<>();
		XSSFWorkbook workbook = new XSSFWorkbook(excelDataFile.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);

		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = worksheet.getRow(i);
			userIdList.add(String.valueOf((int) row.getCell(0).getNumericCellValue()));
		}
		return userIdList;
	}

	public User saveUser(User newEvent) {
		log.info("Inside saveUser method Event Service--->");
		User savedEvent = new User();
		try {
			savedEvent = userRepository.save(newEvent);
		} catch (Exception e) {
			log.error("Error in saveUser", e);
			throw new QbthonEventServiceException("Something Went Wrong", e);
		}
		return savedEvent;
	}

	public Map<String, List<Event>> getAllEvents() {
		Map<String, List<Event>> eventDetailsMap = new HashMap<String, List<Event>>();
		log.info("Getting Admin Events Details::::");
		List<Event> eventCompletedList = new ArrayList<>();
		List<Event> eventActiveList = new ArrayList<>();
		List<Event> eventUpcomingList = new ArrayList<>();
		try {
			List<Event> eventList = (List<Event>) eventRepository.findAll();
			eventList.parallelStream().forEach(event -> {
				if (event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now())) {
					eventUpcomingList.add(event);
				} else if (event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
						.isEqual(LocalDate.now())) {
					eventActiveList.add(event);
				} else if (event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
						.isBefore(LocalDate.now())) {
					eventCompletedList.add(event);
				}
			});
			eventDetailsMap.put("ACTIVE", eventActiveList);
			eventDetailsMap.put("COMPLETED", eventCompletedList);
			eventDetailsMap.put("UPCOMING", eventUpcomingList);
			eventDetailsMap.put("TOTAL", eventList);
		} catch (QbthonEventServiceException e) {
			log.error("Error in gettingEvents", e);
			throw e;
		} catch (Exception e) {
			log.error("Error in getEvents", e);
			throw new QbthonEventServiceException("Something Went Wrong", e);
		}
		return eventDetailsMap;
	}

	public Map<String, List<EventDetails>> getUserEvents(String userId) {
		log.info("Getting User Events Details::::");
		List<String> eventIds = new ArrayList<>();
		List<EventDetails> eventTotalList = new ArrayList<>();
		List<EventDetails> eventCompletedList = new ArrayList<>();
		List<EventDetails> eventActiveList = new ArrayList<>();
		List<EventDetails> eventUpcomingList = new ArrayList<>();
		Map<String, List<EventDetails>> eventDetailsMap = new HashMap<>();

		try {

			List<UserEvent> userEventList = userEventRepository.findByUserId(userId);
			userEventList.parallelStream().forEach(userEvent -> eventIds.add(userEvent.getEventId()));
			List<Event> eventList = (List<Event>) eventRepository.findAllById(eventIds);
			final List<UserEvent> userEventFinalList = userEventList;
			eventList.parallelStream().forEach(event -> {
				if (event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now())) {
					eventUpcomingList.add(eventDetailsMapper.map(event));
				} else if (event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
						.isEqual(LocalDate.now())) {
					eventActiveList.add(eventDetailsMapper.map(event));
				} else if (event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
						.isBefore(LocalDate.now())) {
					eventCompletedList.add(eventDetailsMapper.map(event));
				}
			});

			eventTotalList = eventDetailsMapper.map(eventList);

			eventDetailsMap.put("ACTIVE", setNominationStatusAndRole(eventActiveList, userEventFinalList));
			eventDetailsMap.put("COMPLETED", setNominationStatusAndRole(eventCompletedList, userEventFinalList));
			eventDetailsMap.put("UPCOMING", setNominationStatusAndRole(eventUpcomingList, userEventFinalList));
			eventDetailsMap.put("TOTAL", setNominationStatusAndRole(eventTotalList, userEventFinalList));
		} catch (QbthonEventServiceException e) {
			log.error("Error in getEventOfType", e);
			throw e;
		} catch (Exception e) {
			log.error("Error in getEventOfType", e);
			throw new QbthonEventServiceException("Something Went Wrong", e);
		}
		return eventDetailsMap;
	}

	private List<EventDetails> setNominationStatusAndRole(List<EventDetails> eventDetailsList,
			List<UserEvent> userEventFinalList) {
		// TODO Auto-generated method stub
		eventDetailsList.stream().forEach(event -> {
			userEventFinalList.parallelStream().filter(user -> user.getEventId().equals(event.getId()))
					.forEach(userEvent -> {
						// if(userEvent.getEventId().equals(event.getId())) {
						event.setNomination(userEvent.isNominated());
						event.setRole(userEvent.getRole());
						// }
					});
		});
		return eventDetailsList;
	}
}