package main.java.com.jakecrane.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

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

import main.java.com.jakecrane.jerseyProject.Message;

import javax.servlet.http.Cookie;

@Path("/SignOut")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SignOut {
	
	public Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

	@POST
	public Response login(@Context HttpServletRequest req, @Context HttpServletResponse res) throws IOException, SQLException, URISyntaxException {
		removeAllCookies(req, res);
		req.getSession().invalidate();
		Message m = new Message("Logout Successful.");
		String jsonOut = gson.toJson(m);
		return Response.status(Status.OK).entity(jsonOut).build();
	}
	
	public void removeAllCookies(HttpServletRequest req, HttpServletResponse res) {
		for (Cookie cookie : req.getCookies()) {
			cookie.setMaxAge(0);
			cookie.setValue(null);
			cookie.setPath(req.getContextPath() + "/");
			res.addCookie(cookie);
		}
	}
	
}
