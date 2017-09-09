package com.qre.services.preference.impl;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.qre.services.preference.PreferencesService;

public class DefaultPreferenceService extends PreferencesService {

	private static final String SHARED_PREFERENCES_NAME = "qre_preferences";

	public DefaultPreferenceService(@NonNull final Application application, @NonNull final Gson gson) {
		super(application, gson);
	}

	@Override
	protected SharedPreferences initPreferences(@NonNull final Application application) {
		return application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	private static final class Key {
	}
}
