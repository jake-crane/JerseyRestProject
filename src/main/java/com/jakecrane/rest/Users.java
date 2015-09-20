package main.java.com.jakecrane.rest;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import main.java.com.jakecrane.database.UserDAO;
import main.java.com.jakecrane.jerseyProject.Message;
import main.java.com.jakecrane.jerseyProject.Role;
import main.java.com.jakecrane.jerseyProject.User;
 
@Path("/Users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Users {
	
	public Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
	
	@GET
	public Response getUsers(@Context HttpServletRequest req) {
		Response response = null;
		if (userIsAdmin(req)) {
			try {
				List<User> users = UserDAO.getUsers();
				String json = gson.toJson(users);
				response = Response.status(Status.OK).entity(json).build();
			} catch (SQLException e) {
				e.printStackTrace();
				Message m = new Message("Database Error.");
				String json = gson.toJson(m);
				response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(json).build();
			}
		} else {
			Message m = new Message("Permission denied.");
			String json = gson.toJson(m);
			response = Response.status(Status.UNAUTHORIZED).entity(json).build();
		}
		return response;
	}
	
	@GET
	@Path("/{param}")
	public Response getUser(@Context HttpServletRequest req, @PathParam("param") String username) {
		Response response = null;
		if (userIsAdmin(req)) {
			try {
				User user = UserDAO.getUser(username);
			if (user != null) {
				String json = gson.toJson(user);
				response = Response.status(Status.OK).entity(json).build();
			} else {
				Message m = new Message("User not found.");
				String json = gson.toJson(m);
				response = Response.status(Status.NOT_FOUND).entity(json).build();
			}
			} catch (SQLException e) {
				e.printStackTrace();
				Message m = new Message("Server Error.");
				String jsonResponse = gson.toJson(m);
				response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
			}
		} else {
			Message m = new Message("Permission denied.");
			String json = gson.toJson(m);
			response = Response.status(Status.UNAUTHORIZED).entity(json).build();
		}
		return response;
	}
	
	@PUT
	public Response createUser(@Context HttpServletRequest req) {
		Response response = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
			String jsonInput = br.readLine();
			User user = gson.fromJson(jsonInput, User.class);
			user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
			try {
				UserDAO.insertUser(user);
				Message m = new Message("User Created.");
				String json = gson.toJson(m);
				response = Response.status(Status.OK).entity(json).build();
			} catch (MySQLIntegrityConstraintViolationException e) {
				//e.printStackTrace();
				Message m = new Message("Invalid Username or other input.");
				String message = gson.toJson(m);
				response = Response.status(Status.CONFLICT).entity(message).build();
			} catch (SQLException e) {
				e.printStackTrace();
				Message m = new Message("Server Error.");
				String jsonResponse = gson.toJson(m);
				response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(jsonResponse).build();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			Message m = new Message("Invalid Input.");
			String json = gson.toJson(m);
			response = Response.status(Status.BAD_REQUEST).entity(json).build();
		}
		return response;
	}
	
	@DELETE
	@Path("/{param}")
	public Response deleteUser(@Context HttpServletRequest req, @PathParam("param") String username) {
		Response response = null;
		if (userIsAdmin(req)) {
			try {
				UserDAO.deleteUser(username);
				Message m = new Message("User deleted.");
				String json = gson.toJson(m);
				response = Response.status(Status.OK).entity(json).build();
			} catch (SQLException e) {
				e.printStackTrace();
				Message m = new Message("Server Error.");
				String json = gson.toJson(m);
				response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(json).build();
			}
		} else {
			Message m = new Message("Permission denied.");
			String json = gson.toJson(m);
			response = Response.status(Status.UNAUTHORIZED).entity(json).build();
		}
		return response;
	}

	//TODO add update method
	
	public boolean userIsAdmin(HttpServletRequest req) {
		String username = (String)req.getSession().getAttribute("username");
		Role role = null;
		try {
			role = UserDAO.getRole(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return role == Role.ADMIN;
	}
 
}
