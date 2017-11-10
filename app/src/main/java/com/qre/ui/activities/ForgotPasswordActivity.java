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
        networkService.forgotPassword(vEmail.getText().toString(), new NetCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                // TODO mejorar esto y navegar al login
                Toast.makeText(ForgotPasswordActivity.this, "OK", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable exception) {
                // TODO mejorar esto
                Log.e(TAG, "Error al enviar forgot password", exception);
                Toast.makeText(ForgotPasswordActivity.this, "NO OK", Toast.LENGTH_LONG).show();
            }
        });
    }

}