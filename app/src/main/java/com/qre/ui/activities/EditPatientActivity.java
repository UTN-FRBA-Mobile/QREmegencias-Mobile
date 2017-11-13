package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.VerificationDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.qre.utils.Constants.INTENT_EXTRA_TEMP_CODE;
import static com.qre.utils.Constants.INTENT_EXTRA_USER;

public class EditPatientActivity extends AppCompatActivity {

    private static final String TAG = EditPatientActivity.class.getSimpleName();

    private String user;

    @Inject
    NetworkService networkService;

    @BindView(R.id.text_verify_sign)
    TextView vVerifySign;

    @BindView(R.id.toolbar)
    Toolbar vToolbar;

    @BindView(R.id.btn_load_clinical_history)
    Button vButtonLoadClinicalHistory;

    @BindView(R.id.btn_view_clinical_history)
    Button vButtonViewClinicalHistory;

    @BindView(R.id.btn_edit_emergency_data)
    Button vButtonEditEmergencyData;

    public static Intent getIntent(final Context context) {
        return new Intent(context, EditPatientActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);

        ButterKnife.bind(this);
        Injector.getServiceComponent().inject(this);

        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        final String qrContent = intent.getStringExtra(INTENT_EXTRA_TEMP_CODE);

        networkService.verifySignature(qrContent, new NetCallback<VerificationDTO>() {

            @Override
            public void onSuccess(VerificationDTO response) {
                if (response.getUser() != null) {
                    user = response.getUser();
                    vButtonEditEmergencyData.setVisibility(View.VISIBLE);
                    vButtonLoadClinicalHistory.setVisibility(View.VISIBLE);
                    vButtonViewClinicalHistory.setVisibility(View.VISIBLE);
                    vVerifySign.setVisibility(View.GONE);
                } else {
                    vVerifySign.setText(response.getErrorMessage());
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                vVerifySign.setText(exception.getMessage());
            }
        });

    }

    @OnClick(R.id.btn_edit_emergency_data)
    public void editEmergencyData() {
        final Intent intent = EditEmergencyDataActivity.getIntent(this);
        intent.putExtra(INTENT_EXTRA_USER, user);
        startActivity(intent);
    }

    @OnClick(R.id.btn_load_clinical_history)
    public void loadClinicalHistory() {
        final Intent intent = MedicalClinicalHistoryActivity.getIntent(this);
        intent.putExtra(INTENT_EXTRA_USER, user);
        startActivity(intent);
    }

    @OnClick(R.id.btn_view_clinical_history)
    public void viewClinicalHistory() {
        final Intent intent = MedicalClinicalHistoryViewActivity.getIntent(this);
        intent.putExtra(INTENT_EXTRA_USER, user);
        startActivity(intent);
    }

    @OnClick(R.id.btn_back_to_home)
    public void backToHome() {
        startActivity(HomeActivity.getIntent(this));
    }

}