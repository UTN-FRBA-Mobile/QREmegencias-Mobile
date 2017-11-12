package com.qre.injection.modules;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qre.client.ApiClient;
import com.qre.client.api.MobileRestControllerApi;
import com.qre.client.auth.OAuth;
import com.qre.client.auth.OAuthFlow;
import com.qre.services.networking.NetworkService;
import com.qre.services.networking.RetrofitNetworkService;
import com.qre.services.preference.impl.UserPreferenceService;
import com.qre.utils.Constants;

import org.aaronhe.threetengson.ThreeTenGsonAdapter;
import org.apache.oltu.oauth2.common.token.BasicOAuthToken;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.temporal.ChronoField;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
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
    @Named("withoutOAuth")
    OkHttpClient provideOkhttpClient(final Cache cache) {
        final OkHttpClient.Builder client = new OkHttpClient.Builder();
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.cache(cache);
        client.addInterceptor(logging);
        client.connectTimeout(30, TimeUnit.SECONDS);
        client.readTimeout(30, TimeUnit.SECONDS);
        client.writeTimeout(30, TimeUnit.SECONDS);
        return client.build();
    }

    @Provides
    @Singleton
    @Named("withOAuth")
    OkHttpClient provideOkhttpClientWithOAuth(@Named("withoutOAuth") final OkHttpClient okHttpClient,
                                              final OAuth oAuth) {
        final OkHttpClient.Builder client = okHttpClient.newBuilder();
        client.addInterceptor(oAuth);
        return client.build();
    }

    @Provides
    ApiClient provideApiClient(@Named("withoutOAuth") final OkHttpClient okHttpClient,
                               final OAuth oAuth,
                               final Gson gson) {

        final ApiClient apiClient = new ApiClient();
        apiClient.addAuthorization(Constants.API_AUTHORIZATION, oAuth);
        apiClient.configureAuthorizationFlow(mClientId, mClientSecret, "");
        apiClient.configureFromOkclient(okHttpClient);
        apiClient.getAdapterBuilder().baseUrl(mBaseUrl);

        final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .optionalStart()
                .appendPattern(".SSS")
                .optionalEnd()
                .optionalStart()
                .appendZoneOrOffsetId()
                .optionalEnd()
                .optionalStart()
                .appendOffset("+HHMM", "0000")
                .optionalEnd()
                .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                .toFormatter();
        apiClient.setOffsetDateTimeFormat(dateTimeFormatter);
        apiClient.getAdapterBuilder().addConverterFactory(GsonConverterFactory.create(gson));
        return apiClient;
    }

    @Provides
    @Singleton
    public OAuth provideOAuth(final UserPreferenceService userPreferenceService) {
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
        return read;
    }

    @Provides
    @Singleton
    NetworkService provideNetworkService(final ApiClient apiClient, final UserPreferenceService userPreferenceService,
                                         final MobileRestControllerApi api) {
        return new RetrofitNetworkService(apiClient, userPreferenceService, api);
    }

    @Provides
    @Singleton
    MobileRestControllerApi provideRestApi(final ApiClient apiClient) {
        return apiClient.createService(MobileRestControllerApi.class);
    }

}