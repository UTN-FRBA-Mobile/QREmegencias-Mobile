package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    @Inject
    NetworkService networkService;

    @BindView(R.id.input_email)
    EditText vEmail;

    public static Intent getIntent(final Context context) {
        return new Intent(context, ForgotPasswordActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        ButterKnife.bind(this);
        Injector.getServiceComponent().inject(this);
    }

    @OnClick(R.id.btn_send_forgot_password)
    public void sendForgotPassword() {

        if (!Patterns.EMAIL_ADDRESS.matcher(vEmail.getText().toString()).matches()) {
            vEmail.setError(getString(R.string.required_mail));
        } else {
            networkService.forgotPassword(vEmail.getText().toString(), new NetCallback<Void>() {
                @Override
                public void onSuccess(Void response) {
                    Toast.makeText(ForgotPasswordActivity.this, "Éxito! Revise su cuenta de email", Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onFailure(Throwable exception) {
                    Log.e(TAG, "Error al enviar forgot password", exception);
                    Toast.makeText(ForgotPasswordActivity.this, "Error al enviar email. Intente nuevamente", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}