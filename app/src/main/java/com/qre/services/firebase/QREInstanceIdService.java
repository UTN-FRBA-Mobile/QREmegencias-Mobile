package com.qre.services.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.qre.injection.Injector;
import com.qre.models.LoginUserDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;

import javax.inject.Inject;

public class QREInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = QREInstanceIdService.class.getSimpleName();

    @Inject
    NetworkService networkService;

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.getServiceComponent().inject(this);
    }

    @Override
    public void onTokenRefresh() {
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        networkService.updateFirebaseToken(refreshedToken, new NetCallback<LoginUserDTO>() {
            @Override
            public void onSuccess(LoginUserDTO response) {
                Log.d(TAG, "Refreshed token: " + refreshedToken);
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.e(TAG, "Error al actualizar token de firebase", exception);
            }
        });
    }
}
