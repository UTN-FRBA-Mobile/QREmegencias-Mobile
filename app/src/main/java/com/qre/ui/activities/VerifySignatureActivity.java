package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.VerificationDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qre.utils.Constants.INTENT_EXTRA_TEMP_CODE;
import static com.qre.utils.Constants.INTENT_EXTRA_USER;

public class VerifySignatureActivity extends AppCompatActivity {

    private static final String TAG = VerifySignatureActivity.class.getSimpleName();

    public static Intent getIntent(final Context context) {
        return new Intent(context, VerifySignatureActivity.class);
    }

    @Inject
    NetworkService networkService;

    @BindView(R.id.text_verify_sign)
    TextView vVerifySign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifysignature);

        ButterKnife.bind(this);
        Injector.getServiceComponent().inject(this);

        final Intent intent = getIntent();
        final String qrContent = intent.getStringExtra(INTENT_EXTRA_TEMP_CODE);

        networkService.verifySignature(qrContent, new NetCallback<VerificationDTO>() {

            @Override
            public void onSuccess(VerificationDTO response) {
                if (response.getUser() != null) {
                    final Intent editUserIntent = EditPatientActivity
                            .getIntent(VerifySignatureActivity.this);
                    editUserIntent.putExtra(INTENT_EXTRA_USER, response.getUser());
                    startActivity(editUserIntent);
                } else {
                    vVerifySign.setText(response.getErrorMessage());
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                vVerifySign.setText(exception.getMessage());
            }
        });

    }

}