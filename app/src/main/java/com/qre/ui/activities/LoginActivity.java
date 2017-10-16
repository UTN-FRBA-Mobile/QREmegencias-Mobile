package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.LoginUserDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.services.preference.impl.UserPreferenceService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject
    NetworkService networkService;

    @Inject
    UserPreferenceService preferencesService;

    @BindView(R.id.input_email)
    EditText vEmail;

    @BindView(R.id.input_password)
    EditText vPassword;

    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        Injector.getServiceComponent().inject(this);

        if (preferencesService.getUsername() != null) {
            Log.i(TAG, "User " + preferencesService.getUsername() + " is already logged");
            startActivity(HomeActivity.getIntent(this));
        }
    }

    @OnClick(R.id.btn_login)
    public void login() {
        final String username = vEmail.getText().toString();
        String password = vPassword.getText().toString();
        Log.i(TAG, "Login with email " + username + " and password " + password);
        networkService.login(username, password, new NetCallback<LoginUserDTO>() {
            @Override
            public void onSuccess(LoginUserDTO response) {
                Log.i(TAG, "User " + response.getName() + " " + response.getLastName() + " logged successfully");
                preferencesService.putUsername(username);
                preferencesService.putRole(response.getRoles().iterator().next());
                startActivity(HomeActivity.getIntent(LoginActivity.this));
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.e(TAG, "Cannot login with username " + vEmail.getText().toString(), exception);
                Context context = getApplicationContext();
                CharSequence text = "Error al loguearse. No se pudo contactar al servidor.";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    @OnClick(R.id.btn_scan)
    public void scan() {
        startActivity(ScanEmergencyDataActivity.getIntent(this));
    }

    @OnClick(R.id.link_forgot_password)
    public void forgotPassword() {
        Log.i(TAG, "Recover password for email " + vEmail.getText().toString());
    }

}