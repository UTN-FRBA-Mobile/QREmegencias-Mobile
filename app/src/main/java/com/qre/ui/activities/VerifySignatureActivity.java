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
import com.qre.utils.CryptoUtils;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        final String qrContent = intent.getStringExtra("tempCode");

        final Duration duration = Duration.between(getTimestamp(qrContent), Instant.now());

        if (duration.toHours() < 30) {
            networkService.getPublicKey(getUser(qrContent), new NetCallback<VerificationDTO>() {

                @Override
                public void onSuccess(VerificationDTO response) {
                    boolean verified = CryptoUtils.verifySignature(response.getPublicKey(),
                            getUser(qrContent), getSignature(qrContent));

                    if (verified) {
                        final Intent tempCodeIntent = TemporalCodeActivity
                                .getIntent(VerifySignatureActivity.this);
                        tempCodeIntent.putExtra("uuid", response.getUuid());
                        startActivity(tempCodeIntent);
                    } else {
                        vVerifySign.setText("No verificado");
                    }

                }

                @Override
                public void onFailure(Throwable exception) {
                    vVerifySign.setText(exception.getMessage());
                }
            });
        } else {
            vVerifySign.setText("QR Expirado");
        }

    }

    private Instant getTimestamp(final String qr) {
        final Long timestamp = Long.valueOf(getTimestampAndUser(qr)[0]);
        return Instant.ofEpochMilli(timestamp);
    }

    private String getUser(final String qr) {
        return getTimestampAndUser(qr)[1];
    }

    private byte[] getSignature(String qr) {
        try {
            return qr.substring(0, 128).getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String[] getTimestampAndUser(String qr) {
        return qr.substring(128).split(" ");
    }

}