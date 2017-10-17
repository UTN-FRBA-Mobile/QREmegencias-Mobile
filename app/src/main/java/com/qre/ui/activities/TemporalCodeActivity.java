package com.qre.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qre.R;
import com.qre.exception.InvalidQRException;
import com.qre.injection.Injector;
import com.qre.models.EmergencyData;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.ui.components.TimerView;
import com.qre.utils.CryptoUtils;
import com.qre.utils.QRUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_SHORT;

public class TemporalCodeActivity extends AppCompatActivity {

    private static final String TAG = TemporalCodeActivity.class.getSimpleName();

    public static Intent getIntent(final Context context) {
        return new Intent(context, TemporalCodeActivity.class);
    }

    @Inject
    NetworkService networkService;

    @BindView(R.id.text_temp_code)
    TextView vTempCode;

    @BindView(R.id.timer)
    TimerView mTimerView;

    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporalcode);

        ButterKnife.bind(this);
        Injector.getServiceComponent().inject(this);

        final Intent intent = getIntent();
        final String qrContent = intent.getStringExtra("tempCode");

        try {
            final InputStream key = getResources().openRawResource(R.raw.privatekey);
            final byte[] bytes = CryptoUtils.decryptText(qrContent, key);
            final EmergencyData data = QRUtils.parseQR(bytes);
            this.uuid = data.getUUID();
        } catch (final GeneralSecurityException | InvalidQRException exception) {
            Log.e(TAG, "QR Invalido", exception);
            Toast.makeText(this, "El QR no pertenece a la aplicacion", Toast.LENGTH_LONG).show();
            finish();
        } catch (final Exception e) {
            Log.e(TAG, "Cannot read QR", e);
            Toast.makeText(this, "Error al leer QR", Toast.LENGTH_LONG).show();
            finish();
        }
        vTempCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("tempCode", vTempCode.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(TemporalCodeActivity.this, "Codigo copiado!", LENGTH_SHORT).show();
                return false;
            }
        });
        getTempCode();

    }

    private void getTempCode() {
        networkService.getVerificationCode(this.uuid, new NetCallback<Integer>() {
            @Override
            public void onSuccess(Integer response) {
                vTempCode.setText(response.toString());
                mTimerView.start(60, new TimerView.AnimationCallback() {
                    @Override
                    public void call() {
                        getTempCode();
                    }
                });
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.e(TAG, "ERROR", exception);
            }
        });
    }

}