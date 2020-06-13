package com.ent.qbthon.event.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.ent.qbthon.event.repository")
public class DynamoDBConfig {

	@Value("${amazon.aws.accesskey}")
	private String awsAccessKey;

	@Value("${amazon.aws.secretkey}")
	private String awsSecretKey;
	
	@Value("${amazon.dynamodb.endpoint}")
    private String dynamoDbEndpoint;

	@Bean
	public AmazonDynamoDB amazonDynamoDB(AWSCredentials awsCredentials) {
		//@SuppressWarnings("deprecation")
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(awsCredentials);
		 if (!StringUtils.isEmpty(dynamoDbEndpoint)) {
	            amazonDynamoDB.setEndpoint(dynamoDbEndpoint);
	        }
		
		return amazonDynamoDB;
	}

	@Bean
	public AWSCredentials awsCredentials() {
		return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
	}
	
}
