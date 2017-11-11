package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qre.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.qre.utils.Constants.INTENT_EXTRA_USER;

public class EditPatientActivity extends AppCompatActivity {

    private static final String TAG = EditPatientActivity.class.getSimpleName();

    public static Intent getIntent(final Context context) {
        return new Intent(context, EditPatientActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_load_clinical_history)
    public void cargarClinicalHistory() {
        final Intent intent = MedicalClinicalHistoryActivity.getIntent(this);
        final String user = getIntent().getStringExtra(INTENT_EXTRA_USER);
        intent.putExtra(INTENT_EXTRA_USER, user);
        startActivity(intent);
    }

}