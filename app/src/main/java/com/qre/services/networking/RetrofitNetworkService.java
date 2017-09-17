package com.qre.services.networking;

import com.qre.client.api.UserFrontControllerApi;
import com.qre.models.LoginUserDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitNetworkService implements NetworkService {

	private final UserFrontControllerApi userFrontControllerApi;

	public RetrofitNetworkService(final UserFrontControllerApi userFrontControllerApi) {
		this.userFrontControllerApi = userFrontControllerApi;
	}

	@Override
	public void login(final String username, final String password,
					  final NetCallback<LoginUserDTO> callback) {
		final Call<LoginUserDTO> call = userFrontControllerApi.loginUsingPOST(username, password, "");
		enqueue(call, callback);
	}

	private <T> void enqueue(final Call<T> call, final NetCallback<T> callback) {
		call.enqueue(new Callback<T>() {
			@Override
			public void onResponse(final Call<T> call, final Response<T> response) {
				if (response.isSuccessful()) {
					if (callback != null) {
						callback.onSuccess(response.body());
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
