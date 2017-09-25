package com.qre.services.preference.impl;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.qre.services.preference.PreferencesService;

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

    private static final class Key {

        private static final String USER_USERNAME = "user.username";
        private static final String USER_ROLE = "user.role";

    }

}