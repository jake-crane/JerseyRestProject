package main.java.com.jakecrane.database;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.mindrot.jbcrypt.BCrypt;

import main.java.com.jakecrane.jerseyProject.Role;
import main.java.com.jakecrane.jerseyProject.User;

public class Database {

	private static String DRIVER;
	private static String CONNECTION;
	private static String USERNAME;
	private static String PASSWORD;

	public static Database instance;

	private Database() {
		Properties properties = new Properties();
		String propFileName = "database.properties";
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName)) {
			if (inputStream != null) {
				properties.load(inputStream);
				DRIVER = properties.getProperty("DATABASE_DRIVER");
				CONNECTION = properties.getProperty("DATABASE_CONNECTION");
				USERNAME = properties.getProperty("DATABASE_USERNAME");
				PASSWORD = properties.getProperty("DATABASE_PASSWORD");
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}

	public Connection getConnection() {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			System.err.println("Missing JDBC Driver?");
			e.printStackTrace();
			return null;
		}

		try {
			return DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public ArrayList<User> getUsers() throws SQLException {
		ArrayList<User> users = new ArrayList<User>();
		try (Connection connection = getInstance().getConnection()) {
			PreparedStatement statment = connection.prepareStatement(
					"SELECT user_id, username, first_name, middle_name, last_name, address,"
					+ " apt_suite_other, city, state_code, zip_code, phone_number, email_address, birth_date"
					+ " FROM user NATURAL JOIN user_login");
			ResultSet rs = statment.executeQuery();
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
		return users;
	}
	
	public User getUser(String username) throws SQLException {
		User returnUser = null;
		try (Connection connection = getInstance().getConnection()) {
			PreparedStatement statment = connection.prepareStatement(
					"SELECT user_id, username, first_name, middle_name, last_name, address,"
					+ " apt_suite_other, city, state_code, zip_code, phone_number, email_address, birth_date"
					+ " FROM user NATURAL JOIN user_login WHERE username = ?");
			statment.setString(1, username);
			ResultSet rs = statment.executeQuery();
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
		return returnUser;
	}
	
	//TODO update method
	public boolean insertUser(User user) throws SQLException {
		boolean success = false;
		try (Connection connection = getInstance().getConnection()) {
			connection.setAutoCommit(false);
			PreparedStatement userStatment = connection.prepareStatement(
					"INSERT INTO user"
					+ " (first_name, middle_name, last_name, address, apt_suite_other, city, state_code, zip_code, phone_number, email_address, birth_date)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
				ResultSet rs = userStatment.getGeneratedKeys();
				int userId = -1;
				if (rs.next()) {//TODO  else break?
					userId = rs.getInt(1);
				}
				PreparedStatement userLoginStatement = connection.prepareStatement("INSERT INTO user_login VALUES (?, ?, ?, ?)");
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
		return success;
	}
	
	public int getUserId(String username, Connection connection) throws SQLException {
		int userId = -1;
		PreparedStatement selectStatment = connection.prepareStatement(
				"SELECT user_id FROM user NATURAL JOIN user_login WHERE username = ?");
		selectStatment.setString(1, username);
		ResultSet rs = selectStatment.executeQuery();
		if (rs.next()) {
			userId = rs.getInt(1);
		}
		return userId;
	}
	
	public boolean deleteUser(String username) throws SQLException {
		boolean success = false;
		try (Connection connection = getInstance().getConnection()) {
			connection.setAutoCommit(false);
			
			int userId = getUserId(username, connection);
			
			PreparedStatement userLoginStatment = connection.prepareStatement(
					"DELETE FROM user_login WHERE user_id = ?");
			userLoginStatment.setInt(1, userId);
			
			PreparedStatement userStatment = connection.prepareStatement(
					"DELETE FROM user WHERE user_id = ?");
			userStatment.setInt(1, userId);
			
			if (userLoginStatment.executeUpdate() > 0 && userStatment.executeUpdate() > 0) {
				connection.commit();
				success = true;
			}
		}
		return success;
	}
	
	//TODO add update method
	
	public boolean credentialsAreValid(User user) throws SQLException {
		boolean success = false;
		try (Connection connection = getInstance().getConnection()) {
			PreparedStatement statment = connection.prepareStatement(
					"SELECT password FROM user_login WHERE username = ?");
			statment.setString(1, user.getUsername());
			ResultSet rs = statment.executeQuery();
			if (rs.next()) {
				String hashedPassword = rs.getString("password");
				success = BCrypt.checkpw(user.getPassword(), hashedPassword);
			}
		}
		return success;
	}
	
	public Role getRole(String username) throws SQLException {
		Role role = null;
		try (Connection connection = getInstance().getConnection()) {
			PreparedStatement statment = connection.prepareStatement(
					"SELECT role_id FROM user_login WHERE username = ?");
			statment.setString(1, username);
			ResultSet rs = statment.executeQuery();
			if (rs.next()) {
				int roleId = rs.getInt("role_id");
				role = Role.getRole(roleId);
			}
		}
		return role;
	}

}