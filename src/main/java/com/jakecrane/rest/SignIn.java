package main.java.com.jakecrane.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import main.java.com.jakecrane.database.Database;
import main.java.com.jakecrane.jerseyProject.Message;
import main.java.com.jakecrane.jerseyProject.Role;
import main.java.com.jakecrane.jerseyProject.User;

@Path("/SignIn")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SignIn {
	
	public Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

	@POST
	public Response login(@Context HttpServletRequest req, @Context HttpServletResponse res) throws IOException, SQLException, URISyntaxException {
		Response response = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
		String jsonIn = br.readLine();
		User user = gson.fromJson(jsonIn, User.class);
		if (Database.getInstance().credentialsAreValid(user)) {
			req.getSession().setAttribute("username", user.getUsername());
			Cookie usernameCookie = new Cookie("username", user.getUsername());
			usernameCookie.setHttpOnly(false);
			usernameCookie.setPath(req.getContextPath() + "/");
			res.addCookie(usernameCookie);
			if (Database.getInstance().getRole(user.getUsername()) == Role.ADMIN) {
				Message m = new Message("Login Successful.", true, "./Admin.html");
				String jsonOut = gson.toJson(m);
				response = Response.status(Status.OK).type("application/json").entity(jsonOut).build();
			} else {
				Message m = new Message("Login Successful.", true, "./User.html");
				String jsonOut = gson.toJson(m);
				response = Response.status(Status.OK).type("application/json").entity(jsonOut).build();
			}
		} else {
			Message m = new Message("Invalid Username / Password.");
			String jsonOut = gson.toJson(m);
			response = Response.status(Status.FORBIDDEN).entity(jsonOut).build();
		}
		return response;
	}
}
