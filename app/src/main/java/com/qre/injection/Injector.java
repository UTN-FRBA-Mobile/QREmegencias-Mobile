package com.qre.injection;

import com.qre.QREApplication;
import com.qre.injection.modules.AppModule;
import com.qre.injection.modules.NetModule;
import com.qre.injection.modules.PreferencesModule;

import java.util.Objects;

public class Injector {

	private static ServiceComponent serviceComponent;

	private Injector() {}

	public static void initializeApplicationComponent(QREApplication application) {
		serviceComponent = DaggerServiceComponent.builder()
				.appModule(new AppModule(application))
				.preferencesModule(new PreferencesModule())
				.netModule(new NetModule("http://192.168.1.5:8082/qremergencias/"))
				.build();

	}

	public static ServiceComponent getServiceComponent() {
		Objects.requireNonNull(serviceComponent, "serviceComponent cannot be");
		return serviceComponent;
	}

}