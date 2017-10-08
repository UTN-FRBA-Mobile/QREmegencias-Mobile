package com.qre.services.networking;

import com.qre.models.EmergencyDataDTO;
import com.qre.models.LoginUserDTO;
import com.qre.models.networking.LoginRequest;
import com.qre.models.networking.LoginResponse;

public interface NetworkService {

	void login(final String username, final String password, final NetCallback<LoginUserDTO> callback);
	void getPublicEmergencyData(final String uuid, final NetCallback<String> callback);

}