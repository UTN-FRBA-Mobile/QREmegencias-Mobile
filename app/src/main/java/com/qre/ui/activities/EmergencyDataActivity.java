package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.qre.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmergencyDataActivity extends AppCompatActivity {

	private static final String TAG = EmergencyDataActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergencydata);

		ButterKnife.bind(this);
    }

	@OnClick(R.id.btn_see_more)
	public void seeMore() {
		Log.v(TAG, "Querés ver más, lo sabemos..");
	}


	public static Intent getIntent(final Context context) {
		return new Intent(context, EmergencyDataActivity.class);
	}

}