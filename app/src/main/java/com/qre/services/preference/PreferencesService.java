package com.qre.services.preference;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

public abstract class PreferencesService {

	protected final SharedPreferences sharedPreferences;
	private final Gson gson;

	public PreferencesService(@NonNull final Application application, @NonNull final Gson gson) {
		this.sharedPreferences = initPreferences(application);
		this.gson = gson;
	}

	protected abstract SharedPreferences initPreferences(@NonNull final Application application);

	protected void putBoolean(@NonNull final String key, @NonNull final boolean value) {
		sharedPreferences.edit().putBoolean(key, value).apply();
	}

	protected boolean getBoolean(@NonNull final String key, @NonNull final boolean defaultValue) {
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	protected void putInt(@NonNull final String key, @NonNull final int value) {
		sharedPreferences.edit().putInt(key, value).apply();
	}

	protected int getInt(@NonNull final String key, @NonNull final int defaultValue) {
		return sharedPreferences.getInt(key, defaultValue);
	}

	protected void putString(@NonNull final String key, @NonNull final String value) {
		sharedPreferences.edit().putString(key, value).apply();
	}

	protected String getString(@NonNull final String key, @NonNull final String defaultValue) {
		return sharedPreferences.getString(key, defaultValue);
	}

	protected <T> void putObject(@NonNull final String key, @NonNull final T object) {
		final String json = gson.toJson(object);
		putString(key, json);
	}

	protected <T> T getObject(@NonNull final String key, @NonNull final Class<T> clazz) {
		final String json = getString(key, null);
		if (json == null) {
			return null;
		}
		return gson.fromJson(json, clazz);
	}
}
