package com.qre.ui.fragments.medical;

import com.qre.R;
import com.qre.ui.activities.ScanForCodeActivity;
import com.qre.ui.fragments.BaseFragment;

import butterknife.OnClick;

public class MedicalEmergencyDataFragment extends BaseFragment {

    private static final String TAG = MedicalEmergencyDataFragment.class.getSimpleName();

    @Override
    protected int getLayout() {
        return R.layout.fragment_medical_emergency_data;
    }

    @OnClick(R.id.btn_scan_code)
    public void scanForCode() {
        startActivity(ScanForCodeActivity.getIntent(this.getContext()));
    }
}