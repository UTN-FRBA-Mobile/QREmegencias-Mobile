package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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
import com.qre.utils.CryptoUtils;

import java.security.KeyPair;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.qre.utils.Constants.ROLE_USER;

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
        final String password = vPassword.getText().toString();

        boolean ok = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            ok = false;
            vEmail.setError(getString(R.string.required_mail));
        }

        if (password.isEmpty()) {
            ok = false;
            vPassword.setError(getString(R.string.required_field));
        }

        if (ok) {
            bLogin.setEnabled(false);
            final Drawable bLoginBackground = bLogin.getBackground();
            bLogin.setBackgroundColor(Color.GRAY);
            Log.i(TAG, "Login with email " + username + " and password " + password);
            networkService.login(username, password, new NetCallback<LoginUserDTO>() {
                @Override
                public void onSuccess(LoginUserDTO response) {
                    Log.i(TAG, "User " + response.getName() + " " + response.getLastName() + " logged successfully");
                    preferencesService.putUsername(username);
                    preferencesService.putNameAndLastname(response.getName() + " " + response.getLastName());
                    final String role = response.getRoles().iterator().next();
                    preferencesService.putRole(role);

                    if (ROLE_USER.equals(role)) {
                        final KeyPair keyPair = CryptoUtils.generateKeyPair();
                        if (keyPair != null) {
                            networkService.uploadPublicKey(keyPair.getPublic(), new NetCallback<Void>() {
                                @Override
                                public void onSuccess(Void response) {
                                    preferencesService.putPrivateKey(keyPair.getPrivate(), LoginActivity.this);
                                    startActivity(HomeActivity.getIntent(LoginActivity.this));
                                    bLogin.setEnabled(true);
                                    bLogin.setBackground(bLoginBackground);
                                }

                                @Override
                                public void onFailure(Throwable exception) {
                                    Log.e(TAG, "Cannot upload key", exception);
                                    Context context = getApplicationContext();
                                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "Error al loguearse, intente nuevamente", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        startActivity(HomeActivity.getIntent(LoginActivity.this));
                        bLogin.setEnabled(true);
                        bLogin.setBackground(bLoginBackground);
                    }

                }

                @Override
                public void onFailure(Throwable exception) {
                    Log.e(TAG, "Cannot login with username " + vEmail.getText().toString(), exception);
                    Context context = getApplicationContext();
                    CharSequence text;
                    if (exception instanceof NetworkException) {
                        text = exception.getMessage();
                    } else {
                        text = getString(R.string.login_error);
                    }
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                    toast.show();
                    bLogin.setEnabled(true);
                    bLogin.setBackground(bLoginBackground);
                }
            });
        }
    }

    @OnClick(R.id.btn_scan)
    public void scan() {
        startActivity(ScanEmergencyDataActivity.getIntent(this));
    }

    @OnClick(R.id.link_forgot_password)
    public void forgotPassword() {
        startActivity(ForgotPasswordActivity.getIntent(this));
    }

}