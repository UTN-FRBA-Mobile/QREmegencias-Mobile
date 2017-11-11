package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;

import com.google.zxing.Result;

import static com.qre.utils.Constants.INTENT_EXTRA_TEMP_CODE;

public class ScanForCodeActivity extends ScanActivity {

    private static final String TAG = ScanForCodeActivity.class.getSimpleName();

    public static Intent getIntent(final Context context) {
        return new Intent(context, ScanForCodeActivity.class);
    }

    @Override
    public void handleResult(Result rawResult) {
        final Intent intent = VerifySignatureActivity.getIntent(ScanForCodeActivity.this);
        intent.putExtra(INTENT_EXTRA_TEMP_CODE, rawResult.getText());
        startActivity(intent);
    }
}