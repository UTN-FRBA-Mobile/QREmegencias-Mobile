package com.qre.injection;

import com.qre.QREApplication;
import com.qre.injection.modules.AppModule;
import com.qre.injection.modules.NetModule;
import com.qre.injection.modules.PreferencesModule;

import java.util.Objects;

public class Injector {

    private static ServiceComponent serviceComponent;

    private Injector() {
    }

    public static void initializeApplicationComponent(QREApplication application) {
        serviceComponent = DaggerServiceComponent.builder()
                .appModule(new AppModule(application))
                .preferencesModule(new PreferencesModule())
                .netModule(new NetModule("https://qr.rrramundo.com.ar/qremergencias/",
                        "prueba", "prueba123", "read"))
                .build();

    }

    public static ServiceComponent getServiceComponent() {
        Objects.requireNonNull(serviceComponent, "serviceComponent cannot be");
        return serviceComponent;
    }

}