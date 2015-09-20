package com.jakecrane.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import main.java.com.jakecrane.database.Database;
import main.java.com.jakecrane.database.UserDAO;
import main.java.com.jakecrane.jerseyProject.Role;
import main.java.com.jakecrane.jerseyProject.User;

public class UserDAOTest {

	@Test
	public void testUserDAOGetUsers() throws SQLException {
		assertTrue(UserDAO.getUsers().size() > 1);
	}
	
	@Test
	public void testUserDAOGetUser() throws SQLException {
		User newUser = new User(-1, "test", "test", "test", "test", "test", "test", "test", "test", "MO", "00000", "test", "test", new Date());
		try {
		assertTrue(UserDAO.insertUser(newUser));
		
		User selectedUser = UserDAO.getUser("test");
		assertNotNull(selectedUser);
		} finally {
			assertTrue(UserDAO.deleteUser(newUser.getUsername()));
		}
	}

	@Test
	public void testUserDAOGetUserId() throws Exception {
		User newUser = new User(-1, "test", "test", "test", "test", "test", "test", "test", "test", "MO", "00000", "test", "test", new Date());
		assertTrue(UserDAO.insertUser(newUser));
		
		try (Connection connection = Database.getInstance().getConnection()) {
			int userId = UserDAO.getUserId("test", connection);
			assertNotEquals(-1, userId);
			assertEquals(newUser.getUserId(), userId);
		} finally {
			assertTrue(UserDAO.deleteUser(newUser.getUsername()));
		}
	}
	
	@Test
	public void testUserDAOCredentialsAreValid() throws Exception {
		User newUser = new User(-1, "test", BCrypt.hashpw("test", BCrypt.gensalt()), "test", "test", "test", "test", "test", "test", "MO", "00000", "test", "test", new Date());
		try {
			assertTrue(UserDAO.insertUser(newUser));
			newUser.setPassword("test");
			assertTrue(UserDAO.credentialsAreValid(newUser));
			newUser.setPassword("wrong password");
			assertFalse(UserDAO.credentialsAreValid(newUser));
		} finally {
			assertTrue(UserDAO.deleteUser(newUser.getUsername()));
		}
	}
	
	@Test
	public void testUserGetRole() throws Exception {
		User newUser = new User(-1, "test", "test", "test", "test", "test", "test", "test", "test", "MO", "00000", "test", "test", new Date());
		assertTrue(UserDAO.insertUser(newUser));
		
		assertEquals(Role.USER, UserDAO.getRole(newUser.getUsername()));
		assertEquals(Role.ADMIN, UserDAO.getRole("root"));
		
		assertTrue(UserDAO.deleteUser(newUser.getUsername()));
	}
}
