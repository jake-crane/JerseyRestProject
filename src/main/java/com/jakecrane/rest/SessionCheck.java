package main.java.com.jakecrane.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import main.java.com.jakecrane.database.UserDAO;
import main.java.com.jakecrane.jerseyProject.Message;
import main.java.com.jakecrane.jerseyProject.Role;

@Path("/SessionCheck")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionCheck {

	public Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

	@GET
	public Response checkSession(@Context HttpServletRequest req) throws IOException, SQLException, URISyntaxException {
		Response response = null;
		HttpSession session = req.getSession(false);
		if (session != null) {
			String username = (String)session.getAttribute("username");
			if (username != null) {
				Role role = UserDAO.getRole(username);
				if (role == Role.ADMIN) {
					Message m = new Message("Your role is ADMIN.", true, "./Admin.html", true);
					String jsonOut = gson.toJson(m);
					response = Response.status(Status.OK).entity(jsonOut).build();
				} else if (role == Role.USER) {
					Message m = new Message("Your role is USER.", true, "./User.html", true);
					String jsonOut = gson.toJson(m);
					response = Response.status(Status.OK).entity(jsonOut).build();
				}
			} else {
				Message m = new Message("Session does not contain a username.", true, "./rest/SignOut/", true);
				String jsonOut = gson.toJson(m);
				response = Response.status(Status.OK).entity(jsonOut).build();
			}
		} else {
			Message m = new Message("Session is Invalid.", true, "./", false);
			String jsonOut = gson.toJson(m);
			response = Response.status(Status.OK).entity(jsonOut).build();
		}
		return response;
	}
	
	@GET
	@Path("/{param: Admin|User}")
	public Response checkSessionAndRoleType(@Context HttpServletRequest req, @PathParam("param") String role) throws IOException, SQLException, URISyntaxException {
		Role requestRole = Role.valueOf(role.toUpperCase());
		Response response = null;
		HttpSession session = req.getSession(false);
		if (session != null) {
			Role actualRole = UserDAO.getRole((String)session.getAttribute("username"));
			if (requestRole == actualRole) {
				Message m = new Message("Session is Valid and role is correct.");
				m.setSessionValid(true);
				String jsonOut = gson.toJson(m);
				response = Response.status(Status.OK).entity(jsonOut).build();
			} else if (requestRole == Role.USER) {
				Message m = new Message("Your role is not USER.", true, "./Admin.html", false);
				String jsonOut = gson.toJson(m);
				response = Response.status(Status.OK).entity(jsonOut).build();
			} else if (requestRole == Role.ADMIN) {
				Message m = new Message("Your role is not ADMIN.", true, "./User.html", false);
				String jsonOut = gson.toJson(m);
				response = Response.status(Status.OK).entity(jsonOut).build();
			}
		} else {
			Message m = new Message("Session is Invalid.", true, "./", false);
			String jsonOut = gson.toJson(m);
			response = Response.status(Status.OK).entity(jsonOut).build();
		}
		return response;
	}

}
