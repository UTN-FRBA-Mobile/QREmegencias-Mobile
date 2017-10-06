package com.qre.services.networking;

import com.qre.models.EmergencyDataDTO;
import com.qre.models.LoginUserDTO;

public interface NetworkService {

	void login(final String username, final String password, final NetCallback<LoginUserDTO> callback);
	void getPublicEmergencyData(final String uuid, final NetCallback<EmergencyDataDTO> callback);
    void getVerificationCode(final String text, final NetCallback<Boolean> callback);
}