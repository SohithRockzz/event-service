package com.ent.qbthon.event.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class EventDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private Date date;

	private String slot;

	private String skills;

	private boolean nomination;
	
	private String role;
	

}
