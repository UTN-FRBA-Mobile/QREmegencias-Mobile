package com.qre.injection.modules;

import android.app.Application;

import com.google.gson.Gson;
import com.qre.services.preference.PreferencesService;
import com.qre.services.preference.impl.DefaultPreferenceService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PreferencesModule {

    @Provides
    @Singleton
    PreferencesService providePreferencesService(final Application application, final Gson gson) {
        return new DefaultPreferenceService(application, gson);
    }
}