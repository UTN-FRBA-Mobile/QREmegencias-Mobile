package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

		try {
			final Field f = mScannerView.getClass().getDeclaredField("mMultiFormatReader");
			f.setAccessible(true);
			MultiFormatReader mMultiFormatReader = (MultiFormatReader) f.get(mScannerView);
			Map<DecodeHintType, Object> hints = new HashMap<>();
			hints.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE));
			hints.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1");
			mMultiFormatReader.setHints(hints);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
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
		final Intent intent = EmergencyDataActivity.getIntent(this);
		intent.putExtra("result",rawResult.getText());
		startActivity(intent);
	}
}