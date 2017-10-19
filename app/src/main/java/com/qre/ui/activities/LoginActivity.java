package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.LoginUserDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkException;
import com.qre.services.networking.NetworkService;
import com.qre.services.preference.impl.UserPreferenceService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final String ROLE_PACIENTE = "ROLE_PACIENTE";

    @Inject
    NetworkService networkService;

    @Inject
    UserPreferenceService preferencesService;

    @BindView(R.id.input_email)
    EditText vEmail;

    @BindView(R.id.input_password)
    EditText vPassword;

    @BindView(R.id.btn_login)
    Button bLogin;


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
        bLogin.setEnabled(false);
        final Drawable bLoginBackground = bLogin.getBackground();
        bLogin.setBackgroundColor(Color.GRAY);
        Log.i(TAG, "Login with email " + username + " and password " + password);
        networkService.login(username, password, new NetCallback<LoginUserDTO>() {
            @Override
            public void onSuccess(LoginUserDTO response) {
                Log.i(TAG, "User " + response.getName() + " " + response.getLastName() + " logged successfully");
                preferencesService.putUsername(username);
                preferencesService.putRole(response.getRoles().iterator().next());
                if (preferencesService.getRole().equals(ROLE_PACIENTE)) {
                    Context context = getApplicationContext();
                    CharSequence text = "Login solo habilitado para médicos.";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    startActivity(HomeActivity.getIntent(LoginActivity.this));
                }
                bLogin.setEnabled(true);
                bLogin.setBackground(bLoginBackground);
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.e(TAG, "Cannot login with username " + vEmail.getText().toString(), exception);
                Context context = getApplicationContext();
                CharSequence text;

                if (exception instanceof NetworkException) {
                    text = exception.getMessage();
                } else {
                    text = "No se pudo cargar la información.\nError al conectarse con el servidor.";
                }

                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                bLogin.setEnabled(true);
                bLogin.setBackground(bLoginBackground);
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