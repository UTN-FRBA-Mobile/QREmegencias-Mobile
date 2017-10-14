package com.qre.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1000);
        } else {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
            setContentView(mScannerView);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView.setResultHandler(this);
                    mScannerView.startCamera();
                    setContentView(mScannerView);
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Se necesitan permisos de cámara para escanear el código QR";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

}