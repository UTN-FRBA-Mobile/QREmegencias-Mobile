package com.qre.services.networking;

import com.qre.models.LoginUserDTO;
import com.qre.models.VerificationDTO;

public interface NetworkService {

	void login(final String username, final String password, final NetCallback<LoginUserDTO> callback);
    void getVerificationCode(final String text, final NetCallback<Integer> callback);
	void getPublicEmergencyData(final String uuid, final NetCallback<String> callback);
	void getPublicKey(final String user, final NetCallback<VerificationDTO> callback);
    void uploadPublicKey(final byte[] pk, final NetCallback<Void> callback);
}