package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.qre.R;
import com.qre.utils.CryptoUtils;
import com.qre.utils.QRUtils;

import java.io.InputStream;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmergencyDataActivity extends AppCompatActivity {

	private static final String TAG = EmergencyDataActivity.class.getSimpleName();

	@BindView(R.id.emergency_data_blood_type)
	TextView emergency_data_blood_type;

	@BindView(R.id.emergency_data_age)
	TextView emergency_data_age;

	@BindView(R.id.emergency_data_sex)
	TextView emergency_data_sex;

	@BindView(R.id.emergency_data_allergies)
	TextView emergency_data_allergies;

	@BindView(R.id.emergency_data_pathologies)
	TextView emergency_data_pathologies;

	@BindView(R.id.emergency_data_contacts)
	TextView emergency_data_contacts;

	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_emergencydata);
		ButterKnife.bind(this);

		final Intent intent = getIntent();
		final String result = intent.getStringExtra("result");
		final InputStream key = getResources().openRawResource(R.raw.privatekey);
		try {
			final byte[] bytes = CryptoUtils.decryptText(result, key);
			final QRUtils.DTO dto = QRUtils.parseQR(bytes);
			this.url = dto.getUrl();
			emergency_data_blood_type.setText(dto.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.btn_see_more)
	public void seeMore() {
		Log.v(TAG, "Querés ver más, lo sabemos..");
		final Intent intent = SeeMoreActivity.getIntent(this);
		intent.putExtra("url", this.url);
		startActivity(intent);

	}

	public static Intent getIntent(final Context context) {
		return new Intent(context, EmergencyDataActivity.class);
	}

}