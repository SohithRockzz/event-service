package com.ent.qbthon.event.exception;

public class QbthonEventServiceException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public QbthonEventServiceException(String errorMessage, Throwable t) {
		super(errorMessage, t);
	}

	public QbthonEventServiceException(String errorMessage) {
		super(errorMessage);
	}
	

}
