package com.qre.services.networking;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qre.client.api.EmergencyDataControllerApi;
import com.qre.client.api.TempCodeControllerApi;
import com.qre.client.api.UserFrontControllerApi;
import com.qre.models.ApiError;
import com.qre.models.LoginUserDTO;
import com.qre.models.VerificationDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.aaronhe.threetengson.ThreeTenGsonAdapter.registerAll;

public class RetrofitNetworkService implements NetworkService {

    private final UserFrontControllerApi userFrontControllerApi;
    private final EmergencyDataControllerApi emergencyDataControllerApi;
    private final TempCodeControllerApi mobileTestControllerApi;

    private static final String TAG = RetrofitNetworkService.class.getSimpleName();

    public RetrofitNetworkService(final UserFrontControllerApi userFrontControllerApi,
                                  final EmergencyDataControllerApi emergencyDataControllerApi,
                                  final TempCodeControllerApi mobileTestControllerApi) {
        this.userFrontControllerApi = userFrontControllerApi;
        this.emergencyDataControllerApi = emergencyDataControllerApi;
        this.mobileTestControllerApi = mobileTestControllerApi;
    }


    @Override
    public void login(final String username, final String password,
                      final NetCallback<LoginUserDTO> callback) {
        final Call<LoginUserDTO> call = userFrontControllerApi.loginUsingPOST(username, password, "");
        enqueue(call, callback);
    }

    @Override
    public void getPublicEmergencyData(final String uuid, final NetCallback<String> callback) {
        final Call<String> call = emergencyDataControllerApi.getEmergencyDataByUuidUsingGET(uuid);
        enqueue(call, callback);
    }

    @Override
    public void getPublicKey(final String user, final NetCallback<VerificationDTO> callback) {
        final Call<VerificationDTO> call = mobileTestControllerApi.getPublicKeyUsingGET(user);
        enqueue(call, callback);
    }

    @Override
    public void getVerificationCode(final String text, final NetCallback<Integer> callback) {
        final Call<Integer> call = mobileTestControllerApi.createTempCodeUsingPUT(text);
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
