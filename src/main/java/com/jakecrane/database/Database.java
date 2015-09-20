package main.java.com.jakecrane.database;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
}
