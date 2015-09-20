package com.jakecrane.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;

import main.java.com.jakecrane.database.Database;
import main.java.com.jakecrane.database.UserDAO;
import main.java.com.jakecrane.jerseyProject.User;

public class UserDAOTest {

	@Test
	public void testGetConnection() {
		Database.getInstance().getConnection();
	}
	
	@Test
	public void testGetUsers() throws SQLException {
		assertTrue(UserDAO.getUsers().size() > 1);
	}

	@Test
	public void testGetUser() throws SQLException {
		User newUser = new User(-1, "test", "test", "test", "test", "test", "test", "test", "test", "MO", "00000", "test", "test", new Date());
		assertTrue(UserDAO.insertUser(newUser));
		
		User selectedUser = UserDAO.getUser("test");
		assertNotNull(selectedUser);
		
		User user = new User(-1, "test", "test", "test", "test", "test", "test", "test", "test", "MO", "00000", "test", "test", new Date());
		assertTrue(UserDAO.deleteUser(user.getUsername()));
	}

}
