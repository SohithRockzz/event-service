package com.ent.qbthon.event.rest;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ent.qbthon.event.model.EventUserDetails;
@Component("emailRestTemplate")
public class EmailRestTemplate {
	 String fooResourceUrl
	    = "http://localhost:8070/email";
  public void sendEventInvitationMail(EventUserDetails eventUserDetails) {
	  RestTemplate restTemplate=new RestTemplate();
	   restTemplate.postForEntity(fooResourceUrl+ "/sendNominationMail", eventUserDetails, EventUserDetails.class).getBody();   	     
  }
}
