package com.qre.ui.fragments.medical;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.qre.R;
import com.qre.ui.activities.ScanEmergencyDataActivity;
import com.qre.ui.fragments.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MedicalEditUserFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_medical_edit_user;
    }

    @OnClick(R.id.btn_scan)
    public void scan() {
        startActivity(ScanEmergencyDataActivity.getIntent(getContext()));
    }

}