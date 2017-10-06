package com.qre.services.networking;

import com.qre.client.api.EmergencyDataControllerApi;
import com.qre.client.api.MobileTestControllerApi;
import com.qre.client.api.UserFrontControllerApi;
import com.qre.models.EmergencyDataDTO;
import com.qre.models.LoginUserDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitNetworkService implements NetworkService {

	private final UserFrontControllerApi userFrontControllerApi;
	private final EmergencyDataControllerApi emergencyDataControllerApi;
	private final MobileTestControllerApi mobileTestControllerApi;

	public RetrofitNetworkService(final UserFrontControllerApi userFrontControllerApi,
								  final EmergencyDataControllerApi emergencyDataControllerApi,
								  final MobileTestControllerApi mobileTestControllerApi) {
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
	public void getPublicEmergencyData(final String uuid, final NetCallback<EmergencyDataDTO> callback) {
		final Call<EmergencyDataDTO> call = emergencyDataControllerApi.getPublicEmergencyDataUsingGET(uuid);
		enqueue(call, callback);
	}

	@Override
	public void getVerificationCode(final String text, final NetCallback<Boolean> callback) {
		final Call<Boolean> call = mobileTestControllerApi.verifySignatureUsingPOST("PRUEBA", text);
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
						String message = (response.message() != null && !response.message().isEmpty()) ? response.message() : response.code() + " error executing a network call";
						callback.onFailure(new NetworkException(response.code(), message));
					}
				}
			}

			@Override
			public void onFailure(final Call<T> call, final Throwable t) {
				if (callback != null) {
					callback.onFailure(t);
				}
			}
		});
	}
}
