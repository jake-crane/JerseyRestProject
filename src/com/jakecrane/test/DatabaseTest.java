package com.jakecrane.test;

import org.junit.Test;

import main.java.com.jakecrane.database.Database;

public class DatabaseTest {

	@Test
	public void testGetConnection() {
		Database.getInstance().getConnection();
	}
	
}
