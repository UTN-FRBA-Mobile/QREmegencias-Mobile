package com.qre.services.preference.impl;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.qre.services.preference.PreferencesService;
import com.qre.utils.CryptoUtils;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;

public class UserPreferenceService extends PreferencesService {

    private static final String SHARED_PREFERENCES_NAME = "qre_user_preferences";

    public UserPreferenceService(@NonNull final Application application, @NonNull final Gson gson) {
        super(application, gson);
    }

    @Override
    protected SharedPreferences initPreferences(@NonNull final Application application) {
        return application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void putUsername(String username) {
        putString(Key.USER_USERNAME, username);
    }

    public String getUsername() {
        return getString(Key.USER_USERNAME, null);
    }

    public void putRole(String role) {
        putString(Key.USER_ROLE, role);
    }

    public String getRole() {
        return getString(Key.USER_ROLE, null);
    }

    public void delete() {
        sharedPreferences.edit().clear().apply();
    }

    public String getAccessToken() {
        return getString(Key.USER_ACCESS_TOKEN, null);
    }

    public void putAccessToken(String accessToken) {
        putString(Key.USER_ACCESS_TOKEN, accessToken);
    }

    public PrivateKey getPrivateKey() {
        try {
            byte[] bytes = getString(Key.USER_PRIVATE_KEY, "").getBytes("ISO-8859-1");
            return CryptoUtils.getPrivateKey(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error leyendo private key", e);
        }
    }

    public void putPrivateKey(final PrivateKey privateKey) {
        try {
            putString(Key.USER_PRIVATE_KEY, new String(privateKey.getEncoded(), "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error guardando private key", e);
        }
    }

    private static final class Key {

        private static final String USER_USERNAME = "user.username";
        private static final String USER_ROLE = "user.role";
        private static final String USER_ACCESS_TOKEN = "user.accessToken";
        private static final String USER_PRIVATE_KEY = "user.privateKey";

    }

}