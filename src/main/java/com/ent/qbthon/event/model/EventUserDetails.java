package com.ent.qbthon.event.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class EventUserDetails implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotNull(message="date must not be null")
	Date date;
	@NotNull(message="slot must not be null")
	String slot;
	@NotNull(message="skills must not be null")
	String skills;
	@NotNull(message="users must not be null")
	List<String> usersList;
}
