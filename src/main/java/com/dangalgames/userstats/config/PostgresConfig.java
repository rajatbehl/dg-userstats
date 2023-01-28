/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * 
 * @author rajat behl
 *
 */
@Configuration
@Slf4j
public class PostgresConfig {
	
	@Autowired
	private Config config;
	
	@Autowired
	private ConsulConfig consul;
	
	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource")
	public DataSourceProperties dataSourceProperties() {
		String secretName = consul.getString("spring.aws.secretmanager.writeSecretName", config.getWriteSecretName());
		String dbDetails = getDbDetails(secretName);
		return getDbProperties(dbDetails);
	}

	@Bean(name = "write")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public HikariDataSource dataSource() {
		return dataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}


	@Bean
	@ConfigurationProperties("spring.read-datasource")
	public DataSourceProperties slaveDataSourceProperties() {
		String secretName = consul.getString("spring.aws.secretmanager.readSecretName", config.getReadSecretName());
		String dbDetails = getDbDetails(secretName);
		return getDbProperties(dbDetails);
	}


	@Bean(name = "read")
	@ConfigurationProperties(prefix = "spring.read-datasource")
	public HikariDataSource slaveDataSource() {
		return slaveDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	private DataSourceProperties getDbProperties(String dbDetails) {
		String[] dbDetailsArray = dbDetails.split(",");
		
		DataSourceProperties dbProperties = new DataSourceProperties();
		dbProperties.setUrl(dbDetailsArray[0] + "stats");
		dbProperties.setUsername(dbDetailsArray[1]);
		dbProperties.setPassword(dbDetailsArray[2]);
		return dbProperties;
	}
	
	private String getDbDetails(String secretName) {

		Region region = Region.of(consul.getString("spring.aws.secretmanager.region", config.getRegion()));
		SecretsManagerClient client = SecretsManagerClient.builder().region(region).build();

		GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(secretName).build();
		
		GetSecretValueResponse getSecretValueResponse = null;
		ObjectMapper objectMapper = new ObjectMapper();
	
		try {
			getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
			if (getSecretValueResponse.secretString() != null) {
				String secret = getSecretValueResponse.secretString();
				JsonNode secretsJson = objectMapper.readTree(secret);
				String url = secretsJson.get("url").textValue();
				String username = secretsJson.get("username").textValue();
				String password = secretsJson.get("password").textValue();

				StringBuilder builder = new StringBuilder();
				builder.append(url);
				builder.append(",");
				builder.append(username);
				builder.append(",");
				builder.append(password);

				return builder.toString();
			}
		} catch (Exception e) {
			log.error("Error while getting secret values {} ", e);
		}
		return "";
	}
	
	
}
