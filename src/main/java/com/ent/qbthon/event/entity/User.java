package com.ent.qbthon.event.entity;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Data;

@DynamoDBTable(tableName = "QBTHON_USER")
@Data
public class User implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@DynamoDBHashKey(attributeName = "user_id")
	private String id;

	@DynamoDBAttribute(attributeName = "user_name")
	private String userName;
	
	@DynamoDBAttribute(attributeName = "password")
	private String password;

	@DynamoDBAttribute(attributeName = "bu_name")
	private String buName;

	@DynamoDBAttribute(attributeName = "account")
	private String account;

	@DynamoDBAttribute(attributeName = "admin_role")
	private boolean adminFlag;
}
