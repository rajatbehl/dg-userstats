/*
 * Copyright (c) 2021, Dangal Games and/or its affiliates. All rights reserved.
 * DANGAL GAMES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.dangalgames.userstats.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Ritika A.
 * @author rajat behl
 *
 */
@Service
public class JdbcTemplateProvider {
	
	@Autowired
	@Qualifier("write")
	private DataSource dataSource;
	
	@Autowired
	@Qualifier("read")
	private DataSource readDataSource;
	
	private JdbcTemplate writeTemplate;
	
	private JdbcTemplate readTemplate;
	
	@PostConstruct
	private void init() {
		this.writeTemplate = new JdbcTemplate(dataSource);
		this.readTemplate = new JdbcTemplate(readDataSource);
	}

	public JdbcTemplate getJdbcWriteTemplate() {
		return writeTemplate;
	}

	public JdbcTemplate getJdbcReadTemplate() {
		return readTemplate;
	}

}