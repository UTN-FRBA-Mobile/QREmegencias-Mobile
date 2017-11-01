package com.qre.injection;

import com.qre.injection.modules.AppModule;
import com.qre.injection.modules.NetModule;
import com.qre.injection.modules.PreferencesModule;
import com.qre.services.firebase.QREInstanceIdService;
import com.qre.services.networking.RetrofitNetworkService;
import com.qre.ui.activities.HomeActivity;
import com.qre.ui.activities.LoginActivity;
import com.qre.ui.activities.SeeMoreActivity;
import com.qre.ui.activities.TemporalCodeActivity;
import com.qre.ui.activities.VerifySignatureActivity;
import com.qre.ui.fragments.user.UserEmergencyDataFragment;
import com.qre.ui.fragments.ProfileFragment;
import com.qre.ui.fragments.user.UserManageQRFragment;
import com.qre.ui.fragments.user.UserSignedQRFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={AppModule.class, NetModule.class, PreferencesModule.class})
public interface ServiceComponent {

    void inject(final RetrofitNetworkService retrofitNetworkService);

	void inject(final HomeActivity homeActivity);

    void inject(final LoginActivity loginActivity);

    void inject(final SeeMoreActivity seeMoreActivity);

    void inject(final TemporalCodeActivity temporalCodeActivity);

    void inject(final VerifySignatureActivity verifySignatureActivity);

    void inject(final UserManageQRFragment userManageQRFragment);

    void inject(final UserEmergencyDataFragment userEmergencyDataFragment);

    void inject(final UserSignedQRFragment userSignedQRFragment);

    void inject(final ProfileFragment profileFragment);

    void inject(final QREInstanceIdService qreInstanceIdService);

}