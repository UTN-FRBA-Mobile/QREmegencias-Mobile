package com.qre.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public abstract class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

	private static final String TAG = ScanActivity.class.getSimpleName();

	private ZXingScannerView mScannerView;

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

	protected void resumeCameraPreview() {
		mScannerView.resumeCameraPreview(this);
	}
}