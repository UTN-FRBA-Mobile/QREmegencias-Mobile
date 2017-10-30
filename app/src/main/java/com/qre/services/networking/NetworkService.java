package com.qre.services.networking;

import com.qre.models.EmergencyDataDTO;
import com.qre.models.LoginUserDTO;
import com.qre.models.UserProfileDTO;
import com.qre.models.VerificationDTO;

import java.security.PublicKey;

public interface NetworkService {

	void login(final String username, final String password, final NetCallback<LoginUserDTO> callback);
    void getVerificationCode(final String text, final NetCallback<Integer> callback);
	void getPublicEmergencyData(final String uuid, final NetCallback<String> callback);
    void getProfile(final NetCallback<UserProfileDTO> callback);
	void getPublicKey(final String user, final NetCallback<VerificationDTO> callback);
    void uploadPublicKey(final PublicKey pk, final NetCallback<Void> callback);
    void logout();
    void getEmergencyData(final NetCallback<EmergencyDataDTO> netCallback);
}