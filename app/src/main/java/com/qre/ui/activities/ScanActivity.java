package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

	private ZXingScannerView mScannerView;

	private static final String TAG = ScanActivity.class.getSimpleName();

	public static Intent getIntent(final Context context) {
		return new Intent(context, ScanActivity.class);
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		mScannerView = new ZXingScannerView(this);
		mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
		setContentView(mScannerView);
	}

	@Override
	public void onResume() {
		super.onResume();
		mScannerView.setResultHandler(this);
		mScannerView.startCamera();
	}

	@Override
	public void onPause() {
		super.onPause();
		mScannerView.stopCamera();
	}

	@Override
	public void handleResult(Result rawResult) {
		Log.v(TAG, rawResult.getText());
		Log.v(TAG, rawResult.getBarcodeFormat().toString());
		mScannerView.resumeCameraPreview(this);
	}
}