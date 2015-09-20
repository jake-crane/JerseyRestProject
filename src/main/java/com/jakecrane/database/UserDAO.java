package main.java.com.jakecrane.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.mindrot.jbcrypt.BCrypt;

import main.java.com.jakecrane.jerseyProject.Role;
import main.java.com.jakecrane.jerseyProject.User;

public class UserDAO {
	
	public static ArrayList<User> getUsers() throws SQLException {
		ArrayList<User> users = new ArrayList<User>();
		try (Connection connection = Database.getInstance().getConnection()) {
			try (PreparedStatement statment = connection.prepareStatement(
					"SELECT user_id, username, first_name, middle_name, last_name, address,"
					+ " apt_suite_other, city, state_code, zip_code, phone_number, email_address, birth_date"
					+ " FROM user NATURAL JOIN user_login")) {
				try (ResultSet rs = statment.executeQuery()) {
					while (rs.next()) {
						User user = new User(
								rs.getInt("user_id"),
								rs.getString("username"),
								null,
								rs.getString("first_name"),
								rs.getString("middle_name"),
								rs.getString("last_name"),
								rs.getString("address"),
								rs.getString("apt_suite_other"),
								rs.getString("city"),
								rs.getString("state_code"),
								rs.getString("zip_code"),
								rs.getString("phone_number"),
								rs.getString("email_address"),
								rs.getDate("birth_date")
								);
						users.add(user);
					}
				}
			}
		}
		return users;
	}
	
	public static User getUser(String username) throws SQLException {
		User returnUser = null;
		try (Connection connection = Database.getInstance().getConnection()) {
			try (PreparedStatement statment = connection.prepareStatement(
					"SELECT user_id, username, first_name, middle_name, last_name, address,"
					+ " apt_suite_other, city, state_code, zip_code, phone_number, email_address, birth_date"
					+ " FROM user NATURAL JOIN user_login WHERE username = ?")) {
				statment.setString(1, username);
				try (ResultSet rs = statment.executeQuery()) {
					if (rs.next()) {
						returnUser = new User(
								rs.getInt("user_id"),
								rs.getString("username"),
								null,
								rs.getString("first_name"),
								rs.getString("middle_name"),
								rs.getString("last_name"),
								rs.getString("address"),
								rs.getString("apt_suite_other"),
								rs.getString("city"),
								rs.getString("state_code"),
								rs.getString("zip_code"),
								rs.getString("phone_number"),
								rs.getString("email_address"),
								rs.getDate("birth_date")
								);
					}
				}
			}
		}
		return returnUser;
	}
	
	//TODO update method
	public static boolean insertUser(User user) throws SQLException {
		boolean success = false;
		try (Connection connection = Database.getInstance().getConnection()) {
			connection.setAutoCommit(false);
			try (PreparedStatement userStatment = connection.prepareStatement(
					"INSERT INTO user"
					+ " (first_name, middle_name, last_name, address, apt_suite_other, city, state_code, zip_code, phone_number, email_address, birth_date)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
				userStatment.setString(1, user.getFirstName());
				userStatment.setString(2, user.getMiddleName().trim().isEmpty() ? null : user.getMiddleName());
				userStatment.setString(3, user.getLastName());
				userStatment.setString(4, user.getAddress());
				userStatment.setString(5, user.getAptSuiteOther().trim().isEmpty() ? null : user.getAptSuiteOther());
				userStatment.setString(6, user.getCity());
				userStatment.setString(7, user.getState());
				userStatment.setString(8, user.getZipCode());
				userStatment.setString(9, user.getPhoneNumber());
				userStatment.setString(10, user.getEmailAddress().trim().isEmpty() ? null : user.getEmailAddress());
				userStatment.setDate(11, new java.sql.Date(user.getBirthDate().getTime()));
				
				if (userStatment.executeUpdate() > 0) {
					try (ResultSet rs = userStatment.getGeneratedKeys()) {
						int userId = -1;
						if (rs.next()) {//TODO  else break?
							userId = rs.getInt(1);
						}
						try (PreparedStatement userLoginStatement = connection.prepareStatement("INSERT INTO user_login VALUES (?, ?, ?, ?)")) {
							userLoginStatement.setInt(1, userId);
							userLoginStatement.setString(2, user.getUsername());
							userLoginStatement.setString(3, user.getPassword());
							userLoginStatement.setInt(4, 2);
							if (userLoginStatement.executeUpdate() > 0) {
								connection.commit();
								success = true;
							}
						}
					}
				}
			}
		}
		return success;
	}
	
	public static int getUserId(String username, Connection connection) throws SQLException {
		int userId = -1;
		try (PreparedStatement selectStatment = connection.prepareStatement(
				"SELECT user_id FROM user NATURAL JOIN user_login WHERE username = ?")) {
			selectStatment.setString(1, username);
			try (ResultSet rs = selectStatment.executeQuery()) {
				if (rs.next()) {
					userId = rs.getInt(1);
				}
			}
		}
		return userId;
	}
	
	public static boolean deleteUser(String username) throws SQLException {
		boolean success = false;
		try (Connection connection = Database.getInstance().getConnection()) {
			connection.setAutoCommit(false);
			
			int userId = getUserId(username, connection);
			
			try (PreparedStatement userLoginStatment = connection.prepareStatement(
					"DELETE FROM user_login WHERE user_id = ?")) {
				userLoginStatment.setInt(1, userId);
				try (PreparedStatement userStatment = connection.prepareStatement(
						"DELETE FROM user WHERE user_id = ?")) {
					userStatment.setInt(1, userId);
					
					if (userLoginStatment.executeUpdate() > 0 && userStatment.executeUpdate() > 0) {
						connection.commit();
						success = true;
					}
				}
			}
		}
		return success;
	}
	
	//TODO add update method
	
	public static boolean credentialsAreValid(User user) throws SQLException {
		boolean success = false;
		try (Connection connection = Database.getInstance().getConnection()) {
			try (PreparedStatement statment = connection.prepareStatement(
					"SELECT password FROM user_login WHERE username = ?")) {
				statment.setString(1, user.getUsername());
				try (ResultSet rs = statment.executeQuery()) {
					if (rs.next()) {
						String hashedPassword = rs.getString("password");
						success = BCrypt.checkpw(user.getPassword(), hashedPassword);
					}
				}
			}
		}
		return success;
	}
	
	public static Role getRole(String username) throws SQLException {
		Role role = null;
		try (Connection connection = Database.getInstance().getConnection()) {
			try (PreparedStatement statment = connection.prepareStatement(
					"SELECT role_id FROM user_login WHERE username = ?")) {
				statment.setString(1, username);
				try (ResultSet rs = statment.executeQuery()) {
					if (rs.next()) {
						int roleId = rs.getInt("role_id");
						role = Role.getRole(roleId);
					}
				}
			}
		}
		return role;
	}
}
