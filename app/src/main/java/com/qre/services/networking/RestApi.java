package com.qre.services.networking;

import com.qre.models.networking.LoginRequest;
import com.qre.models.networking.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestApi {

	@POST("/login")
	Call<LoginResponse> login(@Body LoginRequest request);

}