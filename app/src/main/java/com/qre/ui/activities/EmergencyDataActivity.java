package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.qre.R;
import com.qre.models.EmergencyData;
import com.qre.utils.CryptoUtils;
import com.qre.utils.QRUtils;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmergencyDataActivity extends AppCompatActivity {

	private static final String TAG = EmergencyDataActivity.class.getSimpleName();

	@BindView(R.id.emergency_data_blood_type)
	TextView emergencyDataBloodType;

	@BindView(R.id.emergency_data_age)
	TextView emergencyDataAge;

	@BindView(R.id.emergency_data_sex)
	TextView emergencyDataSex;

	@BindView(R.id.emergency_data_allergies)
	TextView emergencyDataAllergies;

	@BindView(R.id.emergency_data_pathologies)
	TextView emergencyDataPathologies;

	@BindView(R.id.emergency_data_contacts)
	TextView emergencyDataContacts;

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
			final EmergencyData emergencyData = QRUtils.parseQR(bytes);
			this.url = emergencyData.getUrl();
			emergencyDataAge.setText("Edad: " + String.valueOf(emergencyData.getAge()));
			emergencyDataAllergies.setText("Alergias: " + emergencyData.getAllergies().toString());
			emergencyDataPathologies.setText("Patologias: " + emergencyData.getPathologies().toString());
			emergencyDataBloodType.setText("Tipo de Sangre: " + emergencyData.getBloodType());
			emergencyDataSex.setText("Sexo: " + emergencyData.getSex());
			if (emergencyData.getContactName() != null) {
				emergencyDataContacts.setText("Contacto: " + emergencyData.getContactName() + " " + emergencyData.getContactPhone());
			}
		} catch (final Exception e) {
			Log.e(TAG, "Error leyendo QR", e);
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