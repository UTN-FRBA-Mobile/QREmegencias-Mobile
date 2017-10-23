package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;

import com.google.zxing.Result;

public class ScanForCodeActivity extends ScanActivity {

	private static final String TAG = ScanForCodeActivity.class.getSimpleName();

	public static Intent getIntent(final Context context) {
		return new Intent(context, ScanForCodeActivity.class);
	}

	@Override
	public void handleResult(Result rawResult) {
		final Intent intent = VerifySignatureActivity.getIntent(ScanForCodeActivity.this);
		intent.putExtra("tempCode", rawResult.getText());
		startActivity(intent);
	}
}