package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.networking.NetworkService;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

	@Inject
	NetworkService networkService;

	public static Intent getIntent(final Context context) {
		return new Intent(context, HomeActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

		Injector.getServiceComponent().inject(this);
	}
}
