package com.brick.sms.email;

import javax.mail.*;

public class MyAuthenticator extends Authenticator {
	private String username = "";
	private String password = "";

	public MyAuthenticator() {
	}

	public MyAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}

}
