package main.java.com.jakecrane.jerseyProject;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = -767315337878132495L;
	
	private String message;
	private boolean redirect;
	private String redirectURL;
	
	public Message(String message) {
		this.message = message;
	}

	public Message(String message, boolean redirect, String redirectURL) {
		this.message = message;
		this.redirect = redirect;
		this.redirectURL = redirectURL;
	}

	public String getMessage() {
		return message;
	}

	public boolean isRedirect() {
		return redirect;
	}
	
	public String getRedirectURL() {
		return redirectURL;
	}
	
}
