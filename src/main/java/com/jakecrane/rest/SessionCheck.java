package main.java.com.jakecrane.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import main.java.com.jakecrane.database.Database;
import main.java.com.jakecrane.jerseyProject.Message;
import main.java.com.jakecrane.jerseyProject.Role;

@Path("/SessionCheck")
public class SessionCheck {
	
	public Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
	
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Response checkSession(@Context HttpServletRequest req) throws IOException, SQLException, URISyntaxException {
		Response response = null;
		HttpSession session = req.getSession(false);
		if (session != null) {
			if (Database.getInstance().getRole((String)session.getAttribute("username")) == Role.ADMIN) {
				Message m = new Message("Session is Valid.", true, "./Admin.html");
				String jsonOut = gson.toJson(m);
				response = Response.status(Response.Status.OK).type("application/json").entity(jsonOut).build();
			} else {
				Message m = new Message("Session is Valid.", true, "./User.html");
				String jsonOut = gson.toJson(m);
				response = Response.status(Response.Status.OK).type("application/json").entity(jsonOut).build();
			}
		} else {
			Message m = new Message("Session is Invalid.");
			String jsonOut = gson.toJson(m);
			response = Response.status(Response.Status.OK).type("application/json").entity(jsonOut).build();
		}
		return response;
	}
	
}
