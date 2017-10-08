package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.Result;
import com.qre.injection.Injector;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;

import javax.inject.Inject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanForCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

	private ZXingScannerView mScannerView;

	private static final String TAG = ScanForCodeActivity.class.getSimpleName();

	public static Intent getIntent(final Context context) {
		return new Intent(context, ScanForCodeActivity.class);
	}

	@Inject
	NetworkService networkService;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		mScannerView = new ZXingScannerView(this);
		setContentView(mScannerView);
		Injector.getServiceComponent().inject(this);
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
		//mScannerView.resumeCameraPreview(this);
		networkService.getVerificationCode(rawResult.getText(), new NetCallback<Integer>() {
			@Override
			public void onSuccess(Integer response) {
				Log.v(TAG, response.toString());
			}

			@Override
			public void onFailure(Throwable exception) {
				Log.e(TAG, "ERROR", exception);
			}
		});
	}
}