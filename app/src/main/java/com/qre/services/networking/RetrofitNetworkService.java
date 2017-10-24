package com.qre.services.networking;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qre.client.ApiClient;
import com.qre.client.api.EmergencyDataControllerApi;
import com.qre.client.api.TempCodeControllerApi;
import com.qre.client.api.UserFrontControllerApi;
import com.qre.client.auth.OAuth;
import com.qre.models.ApiError;
import com.qre.models.LoginUserDTO;
import com.qre.models.PublicKeyDTO;
import com.qre.models.VerificationDTO;
import com.qre.services.preference.impl.UserPreferenceService;
import com.qre.utils.Constants;

import javax.inject.Inject;

import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.aaronhe.threetengson.ThreeTenGsonAdapter.registerAll;

public class RetrofitNetworkService implements NetworkService {

    private final ApiClient apiClient;
    private final UserPreferenceService userPreferenceService;

    private static final String TAG = RetrofitNetworkService.class.getSimpleName();

    public RetrofitNetworkService(final ApiClient apiClient, final UserPreferenceService userPreferenceService) {
        this.apiClient = apiClient;
        this.userPreferenceService = userPreferenceService;
    }



    @Override
    public void login(final String username, final String password,
                      final NetCallback<LoginUserDTO> callback) {

        apiClient.getTokenEndPoint().setUsername(username).setPassword(password);
        final Call<LoginUserDTO> call = getApi(UserFrontControllerApi.class)
                .loginUsingPOST(username, password, "");

        NetCallback<LoginUserDTO> loginCallback = callback;
        if (userPreferenceService.getAccessToken() == null) {
            loginCallback = new NetCallback<LoginUserDTO>() {

                @Override
                public void onSuccess(LoginUserDTO response) {
                    final OAuth oauth = (OAuth) apiClient.getApiAuthorizations().get(Constants.API_AUTHORIZATION);
                    userPreferenceService.putAccessToken(oauth.getAccessToken());
                    callback.onSuccess(response);
                }

                @Override
                public void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            };
        }

        enqueue(call, loginCallback);
    }

    private <T> T getApi(Class<T> service) {
        return apiClient.createService(service);
    }

    @Override
    public void getPublicEmergencyData(final String uuid, final NetCallback<String> callback) {
        final Call<String> call = getApi(EmergencyDataControllerApi.class)
                .getEmergencyDataByUuidUsingGET(uuid);
        enqueue(call, callback);
    }

    @Override
    public void getPublicKey(final String user, final NetCallback<VerificationDTO> callback) {
        final Call<VerificationDTO> call = getApi(TempCodeControllerApi.class)
                .getPublicKeyUsingGET(user);
        enqueue(call, callback);
    }

    @Override
    public void uploadPublicKey(final byte[] pk, final NetCallback<Void> callback) {
        final PublicKeyDTO publicKeyDTO = new PublicKeyDTO().publicKey(Base64.encodeToString(pk, Base64.DEFAULT));
        final Call<Void> call = getApi(TempCodeControllerApi.class).uploadPublicKeyUsingPUT(publicKeyDTO);
        enqueue(call, callback);
    }

    @Override
    public void getVerificationCode(final String text, final NetCallback<Integer> callback) {
        final Call<Integer> call = getApi(TempCodeControllerApi.class).createTempCodeUsingPUT(text);
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
                    callback.onFailure(new Exception("Error de red. No se pudo contactar al servidor."));
                }
            }
        });
    }
}
