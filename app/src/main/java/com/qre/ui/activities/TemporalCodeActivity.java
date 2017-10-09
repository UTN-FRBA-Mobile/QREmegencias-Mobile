package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.EmergencyData;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.utils.CryptoUtils;
import com.qre.utils.QRUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TemporalCodeActivity extends AppCompatActivity {

	private static final String TAG = TemporalCodeActivity.class.getSimpleName();

	public static Intent getIntent(final Context context) {
		return new Intent(context, TemporalCodeActivity.class);
	}

	@Inject
	NetworkService networkService;

	@BindView(R.id.text_temp_code)
	TextView vTempCode;

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
			networkService.getVerificationCode(data.getUUID(), new NetCallback<Integer>() {
				@Override
				public void onSuccess(Integer response) {
					vTempCode.setText(response.toString());
				}

				@Override
				public void onFailure(Throwable exception) {
					Log.e(TAG, "ERROR", exception);
				}
			});
		} catch (InvalidKeyException | IOException | BadPaddingException |
				IllegalBlockSizeException | InvalidAlgorithmParameterException |
				NoSuchAlgorithmException e) {
			Log.e(TAG, "Error parsing QR");
		}


	}

}