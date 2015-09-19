package com.jakecrane.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;

import main.java.com.jakecrane.database.Database;
import main.java.com.jakecrane.jerseyProject.User;

public class DatabaseTest {

	@Test
	public void testGetConnection() {
		Database.getInstance().getConnection();
	}
	
	@Test
	public void testGetUsers() throws SQLException {
		assertTrue(Database.getInstance().getUsers().size() > 1);
	}

	@Test
	public void testGetUser() throws SQLException {
		User newUser = new User(-1, "test", "test", "test", "test", "test", "test", "test", "test", "MO", "00000", "test", "test", new Date());
		assertTrue(Database.getInstance().insertUser(newUser));
		
		User selectedUser = Database.getInstance().getUser("test");
		assertNotNull(selectedUser);
		
		User user = new User(-1, "test", "test", "test", "test", "test", "test", "test", "test", "MO", "00000", "test", "test", new Date());
		assertTrue(Database.getInstance().deleteUser(user.getUsername()));
	}

}
