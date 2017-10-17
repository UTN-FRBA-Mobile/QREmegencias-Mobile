package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.zxing.Result;

public class ScanEmergencyDataActivity extends ScanActivity {

	private static final String TAG = ScanEmergencyDataActivity.class.getSimpleName();

	public static Intent getIntent(final Context context) {
		return new Intent(context, ScanEmergencyDataActivity.class);
	}

	@Override
	public void handleResult(Result rawResult) {
		Log.v(TAG, rawResult.getText());
		Log.v(TAG, rawResult.getBarcodeFormat().toString());
		final Intent intent = EmergencyDataActivity.getIntent(this);
		intent.putExtra("result",rawResult.getText());
		startActivity(intent);
	}
}