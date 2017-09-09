package com.qre.services.networking;

import com.qre.models.networking.LoginRequest;
import com.qre.models.networking.LoginResponse;

public interface NetworkService {

	void login(final LoginRequest request, final NetCallback<LoginResponse> callback);

}