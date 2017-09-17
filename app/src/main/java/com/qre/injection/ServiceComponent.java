package com.qre.injection;

import com.qre.injection.modules.AppModule;
import com.qre.injection.modules.NetModule;
import com.qre.injection.modules.PreferencesModule;
import com.qre.services.networking.RetrofitNetworkService;
import com.qre.ui.activities.HomeActivity;
import com.qre.ui.activities.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, NetModule.class, PreferencesModule.class})
public interface ServiceComponent {

    void inject(final RetrofitNetworkService retrofitNetworkService);

	void inject(final HomeActivity homeActivity);

    void inject(final LoginActivity loginActivity);

}