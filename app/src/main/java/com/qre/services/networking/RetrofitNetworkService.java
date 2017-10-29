package com.qre.services.networking;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qre.client.ApiClient;
import com.qre.client.api.MobileRestControllerApi;
import com.qre.models.ApiError;
import com.qre.models.LoginUserDTO;
import com.qre.models.PublicKeyDTO;
import com.qre.models.VerificationDTO;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;

import java.security.PrivateKey;
import java.security.PublicKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.aaronhe.threetengson.ThreeTenGsonAdapter.registerAll;

public class RetrofitNetworkService implements NetworkService {

    private final ApiClient apiClient;

    private static final String TAG = RetrofitNetworkService.class.getSimpleName();

    public RetrofitNetworkService(final ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void login(final String username, final String password,
                      final NetCallback<LoginUserDTO> callback) {
        apiClient.getTokenEndPoint().setUsername(username).setPassword(password);
        final Call<LoginUserDTO> call = getApi(MobileRestControllerApi.class).getUserInfoUsingGET();
        enqueue(call, callback);
    }

    @Override
    public void logout() {
        apiClient.setAccessToken(null);
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
                    callback.onFailure(new Exception(message));
                }
            }
        });
    }
}
