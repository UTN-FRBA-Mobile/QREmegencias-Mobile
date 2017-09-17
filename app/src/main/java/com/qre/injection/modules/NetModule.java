package com.qre.injection.modules;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qre.client.ApiClient;
import com.qre.client.api.UserFrontControllerApi;
import com.qre.services.networking.NetworkService;
import com.qre.services.networking.RetrofitNetworkService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class NetModule {

    private final String mBaseUrl;

    public NetModule(final String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
    }

    @Provides
    @Singleton
    Cache provideHttpCache(final Application application) {
        final int cacheSize = 10 * 1024 * 1024;
        final Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        //Deserializer & Serializer
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkhttpClient(final Cache cache) {
        final OkHttpClient.Builder client = new OkHttpClient.Builder();
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.cache(cache);
        client.addInterceptor(logging);
        return client.build();
    }

    @Provides
    ApiClient provideApiClient(final OkHttpClient okHttpClient) {
        final ApiClient apiClient = new ApiClient();
        apiClient.configureFromOkclient(okHttpClient);
        return apiClient;
    }

    @Provides
    UserFrontControllerApi provideUserFrontControllerApi(final ApiClient apiClient) {
        return apiClient.createService(UserFrontControllerApi.class);
    }

    @Provides
    @Singleton
    NetworkService provideNetworkService(final UserFrontControllerApi restApi) {
        return new RetrofitNetworkService(restApi);
    }
}