package com.qre.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.networking.NetworkService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();

	@Inject
	NetworkService networkService;

	@BindView(R.id.input_email)
	EditText vEmail;

	@BindView(R.id.input_password)
	EditText vPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        Injector.getServiceComponent().inject(this);
    }

	@OnClick(R.id.btn_login)
	public void login() {
		Log.i(TAG, "Login with email " + vEmail.getText().toString() + " and password " + vPassword.getText().toString());
	}

	@OnClick(R.id.link_forgot_password)
	public void forgotPassword() {
		Log.i(TAG, "Recover password for email " + vEmail.getText().toString());
	}

}