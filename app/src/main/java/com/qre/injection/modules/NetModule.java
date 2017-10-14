package com.qre.injection.modules;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qre.client.ApiClient;
import com.qre.client.api.EmergencyDataControllerApi;
import com.qre.client.api.TempCodeControllerApi;
import com.qre.client.api.UserFrontControllerApi;
import com.qre.services.networking.NetworkService;
import com.qre.services.networking.RetrofitNetworkService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
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
        return new Cache(application.getCacheDir(), cacheSize);
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
        client.cookieJar(new CookieJar() {
            private Cookie session = null;

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                for (Cookie cookie: cookies) {
                    if (cookie.name().equalsIgnoreCase("SESSION")) {
                        session = cookie;
                        return;
                    }
                }
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                return session != null ? Collections.singletonList(session) : new ArrayList<Cookie>();
            }
        });
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.cache(cache);
        client.addInterceptor(logging);
        return client.build();
    }

    @Provides
    ApiClient provideApiClient(final OkHttpClient okHttpClient) {
        final ApiClient apiClient = new ApiClient();
        apiClient.configureFromOkclient(okHttpClient);
        apiClient.getAdapterBuilder().baseUrl(mBaseUrl);
        return apiClient;
    }

    @Provides
    UserFrontControllerApi provideUserFrontControllerApi(final ApiClient apiClient) {
        return apiClient.createService(UserFrontControllerApi.class);
    }

    @Provides
    EmergencyDataControllerApi provideEmergencyDataControllerApi(final ApiClient apiClient) {
        return apiClient.createService(EmergencyDataControllerApi.class);
    }

    @Provides
    TempCodeControllerApi provideMobileTestControllerApi(final ApiClient apiClient) {
        return apiClient.createService(TempCodeControllerApi.class);
    }

    @Provides
    @Singleton
    NetworkService provideNetworkService(final UserFrontControllerApi restApi,
                                         final TempCodeControllerApi mobileTestControllerApi,
                                         final EmergencyDataControllerApi emergencyDataControllerApi) {
        return new RetrofitNetworkService(restApi, emergencyDataControllerApi, mobileTestControllerApi);
    }
}