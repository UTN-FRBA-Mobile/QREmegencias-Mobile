package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.ui.fragments.user.UserClinicalHistoryFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        String user = getIntent().getStringExtra("user");
        intent.putExtra("user", user);
        startActivity(intent);
    }

}