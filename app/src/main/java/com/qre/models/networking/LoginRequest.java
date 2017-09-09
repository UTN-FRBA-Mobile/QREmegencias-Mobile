package com.qre.models.networking;

public class LoginRequest {

	private final String username;
	private final String password;

	public LoginRequest(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

}