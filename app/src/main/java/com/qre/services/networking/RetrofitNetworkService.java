package com.qre.services.networking;

import android.util.Base64;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qre.client.ApiClient;
import com.qre.client.api.MobileRestControllerApi;
import com.qre.models.ApiError;
import com.qre.models.EmergencyDataDTO;
import com.qre.models.LoginUserDTO;
import com.qre.models.PublicKeyDTO;
import com.qre.models.UserProfileDTO;
import com.qre.models.VerificationDTO;
import com.qre.services.preference.impl.UserPreferenceService;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;

import java.security.PublicKey;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.aaronhe.threetengson.ThreeTenGsonAdapter.registerAll;

public class RetrofitNetworkService implements NetworkService {

    private final ApiClient apiClient;
    private final UserPreferenceService preferencesService;

    private static final String TAG = RetrofitNetworkService.class.getSimpleName();

    public RetrofitNetworkService(final ApiClient apiClient, final UserPreferenceService preferencesService) {
        this.apiClient = apiClient;
        this.preferencesService = preferencesService;
    }

    @Override
    public void login(final String username, final String password,
                      final NetCallback<LoginUserDTO> callback) {
        apiClient.getTokenEndPoint().setUsername(username).setPassword(password);
        final String token = FirebaseInstanceId.getInstance().getToken();
        final Call<LoginUserDTO> call = getApi(MobileRestControllerApi.class).getUserInfoUsingGET(token);
        enqueue(call, callback);
    }

    @Override
    public void updateFirebaseToken(final String token, final NetCallback<LoginUserDTO> callback) {
        if (preferencesService.getUsername() != null) {
            Call<LoginUserDTO> call = getApi(MobileRestControllerApi.class).getUserInfoUsingGET(token);
            enqueue(call, callback);
        }
    }

    @Override
    public void logout() {
        apiClient.setAccessToken(null);
    }

    @Override
    public void getEmergencyData(final NetCallback<EmergencyDataDTO> callback) {
        final Call<EmergencyDataDTO> call = getApi(MobileRestControllerApi.class).getEmergencyDataUsingGET();
        enqueue(call, callback);
    }

    @Override
    public void getQR(final String username, final NetCallback<ResponseBody> callback) {
        final Call<ResponseBody> call = getApi(MobileRestControllerApi.class).getQRUsingGET(username);
        enqueue(call, callback);
    }

    @Override
    public void createQR(final NetCallback<Void> callback) {
        final Call<Void> call = getApi(MobileRestControllerApi.class).createQRUsingPOST();
        enqueue(call, callback);
    }

    @Override
    public void deleteQR(final NetCallback<Void> callback) {
        final Call<Void> call = getApi(MobileRestControllerApi.class).deleteQRUsingDELETE();
        enqueue(call, callback);
    }

    private <T> T getApi(Class<T> service) {
        return apiClient.createService(service);
    }

    @Override
    public void getPublicEmergencyData(final String uuid, final NetCallback<String> callback) {
        final Call<String> call = getApi(MobileRestControllerApi.class)
                .getEmergencyDataByUuidUsingGET(uuid, "yes");
        enqueue(call, callback);
    }

    @Override
    public void getProfile(NetCallback<UserProfileDTO> callback) {
        final Call<UserProfileDTO> call = getApi(MobileRestControllerApi.class)
                .getProfileUsingGET();
        enqueue(call, callback);
    }

    @Override
    public void updateProfile(UserProfileDTO profile, boolean qrUpdateRequired, NetCallback<Void> callback) {
        final Call<Void> call = getApi(MobileRestControllerApi.class)
                .updateProfileUsingPATCH(profile, qrUpdateRequired);
        enqueue(call, callback);
    }

    @Override
    public void getPublicKey(final String user, final NetCallback<VerificationDTO> callback) {
        final Call<VerificationDTO> call = getApi(MobileRestControllerApi.class)
                .verifyQRSignatureUsingGET(user);
        enqueue(call, callback);
    }

    @Override
    public void uploadPublicKey(final PublicKey pk, final NetCallback<Void> callback) {
        final PublicKeyDTO publicKeyDTO = new PublicKeyDTO().publicKey(Base64.encodeToString(pk.getEncoded(), Base64.NO_WRAP));
        final Call<Void> call = getApi(MobileRestControllerApi.class).uploadPublicKeyUsingPUT(publicKeyDTO);
        enqueue(call, callback);
    }

    @Override
    public void getVerificationCode(final String text, final NetCallback<Integer> callback) {
        final Call<Integer> call = getApi(MobileRestControllerApi.class).createTempCodeUsingPUT(text);
        enqueue(call, callback);
    }

    private <T> void enqueue(final Call<T> call, final NetCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(final Call<T> call, final Response<T> response) {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(response.body());
                    } else {
                        try {
                            Gson gson = registerAll((new GsonBuilder())).create();
                            String errorBody = response.errorBody().string();
                            ApiError apiError = gson.fromJson(errorBody.replaceAll(":([0-9]{2}).[0-9]{3}",":$1Z"), ApiError.class);
                            callback.onFailure(new NetworkException(response.code(), apiError.getMessage()));
                        } catch (final Exception e) {
                            Log.e(TAG, "ERROR:  Error al leer error web", e);
                            callback.onFailure(new NetworkException(response.code(), response.code() + " error executing a network call"));
                        }
                    }
                }
            }

            @Override
            public void onFailure(final Call<T> call, final Throwable t) {
                if (callback != null) {
                    // TODO Pasar a strings
                    final String message = t.getCause() != null && t.getCause() instanceof OAuthProblemException ?
                            "Usuario/Password incorrectos" :
                            "Error de red. No se pudo contactar al servidor.";
                    callback.onFailure(t);
                }
            }
        });
    }
}
