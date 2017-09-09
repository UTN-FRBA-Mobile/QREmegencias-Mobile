package com.qre.services.networking;

import com.qre.models.networking.LoginRequest;
import com.qre.models.networking.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitNetworkService implements NetworkService {

	private final RestApi restApi;

	public RetrofitNetworkService(final RestApi restApi) {
		this.restApi = restApi;
	}

	@Override
	public void login(final LoginRequest request, final NetCallback<LoginResponse> callback) {
		final Call<LoginResponse> call = restApi.login(request);
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
