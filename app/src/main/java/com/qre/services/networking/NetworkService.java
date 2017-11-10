package com.qre.services.networking;

import com.qre.models.EmergencyDataDTO;
import com.qre.models.LoginUserDTO;
import com.qre.models.PageOfMedicalRecordDTO;
import com.qre.models.UserProfileDTO;
import com.qre.models.VerificationDTO;

import org.threeten.bp.LocalDate;

import java.io.File;
import java.security.PublicKey;
import java.util.Map;

import okhttp3.ResponseBody;

public interface NetworkService {

	void login(final String username, final String password, final NetCallback<LoginUserDTO> callback);
    void getVerificationCode(final String text, final NetCallback<Integer> callback);
	void getPublicEmergencyData(final String uuid, final NetCallback<String> callback);
    void getProfile(final NetCallback<UserProfileDTO> callback);
    void updateProfile(final UserProfileDTO profile, final boolean qrUpdateRequired, final NetCallback<Void> callback);
	void verifySignature(final String user, final NetCallback<VerificationDTO> callback);
    void uploadPublicKey(final PublicKey pk, final NetCallback<Void> callback);
    void logout();
    void getEmergencyData(final NetCallback<EmergencyDataDTO> callback);
    void getQR(final String username, final NetCallback<ResponseBody> callback);
    void createQR(final NetCallback<Void> callback);
    void deleteQR(final NetCallback<Void> callback);
    void updateFirebaseToken(final String token, final NetCallback<LoginUserDTO> callback);
    void createMedicalRecord(final String name, final String text, final LocalDate performed, final String user, final File file, final NetCallback<Map<String, String>> callback);
    void getSelfMedicalRecords(final NetCallback<PageOfMedicalRecordDTO> netCallback);
    void forgotPassword(final String email, final NetCallback<Void> callback);
}