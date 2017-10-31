package com.qre.injection.modules;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qre.client.ApiClient;
import com.qre.client.auth.OAuth;
import com.qre.client.auth.OAuthFlow;
import com.qre.services.networking.NetworkService;
import com.qre.services.networking.RetrofitNetworkService;
import com.qre.services.preference.impl.UserPreferenceService;
import com.qre.utils.Constants;

import org.aaronhe.threetengson.ThreeTenGsonAdapter;
import org.apache.oltu.oauth2.common.token.BasicOAuthToken;

import java.text.DateFormat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {

    private final String mBaseUrl;
    private final String mClientId;
    private final String mClientSecret;
    private final String mScopes;

    public NetModule(final String mBaseUrl, final String mClientId, final String mClientSecret,
                     final String mScopes) {
        this.mBaseUrl = mBaseUrl;
        this.mClientId = mClientId;
        this.mClientSecret = mClientSecret;
        this.mScopes = mScopes;
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
        final GsonBuilder threeTenGsonBuilder = ThreeTenGsonAdapter.registerAll(gsonBuilder);
        return threeTenGsonBuilder.create();
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
    ApiClient provideApiClient(final OkHttpClient okHttpClient,
                               final UserPreferenceService userPreferenceService,
                               final Gson gson) {
        final OAuth read = new OAuth(OAuthFlow.password, mBaseUrl + "oauth/authorize",
                mBaseUrl + "oauth/token", mScopes);

        read.registerAccessTokenListener(new OAuth.AccessTokenListener() {
            @Override
            public void notify(BasicOAuthToken basicOAuthToken) {
                userPreferenceService.putAccessToken(basicOAuthToken.getAccessToken());
            }
        });

        final String accessToken = userPreferenceService.getAccessToken();
        if (accessToken != null) {
            read.setAccessToken(accessToken);
        }

        final ApiClient apiClient = new ApiClient();
        apiClient.addAuthorization(Constants.API_AUTHORIZATION, read);
        apiClient.configureAuthorizationFlow(mClientId, mClientSecret, "");
        apiClient.configureFromOkclient(okHttpClient);
        apiClient.getAdapterBuilder().baseUrl(mBaseUrl);
        apiClient.getAdapterBuilder().addConverterFactory(GsonConverterFactory.create(gson));
        return apiClient;
    }

    @Provides
    @Singleton
    NetworkService provideNetworkService(final ApiClient apiClient) {
        return new RetrofitNetworkService(apiClient);
    }
}