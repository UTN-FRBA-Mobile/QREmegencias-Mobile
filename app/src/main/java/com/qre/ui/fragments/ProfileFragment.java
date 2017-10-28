package com.qre.ui.fragments;

import android.widget.EditText;
import android.widget.RadioButton;

import com.qre.R;

import butterknife.BindView;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.name)
    EditText vName;

    @BindView(R.id.name)
    EditText vSurname;

    @BindView(R.id.name)
    EditText vId;

    @BindView(R.id.name)
    EditText vBirthday;

    @BindView(R.id.name)
    RadioButton vMale;

    @BindView(R.id.female)
    RadioButton vFemale;

    @BindView(R.id.other)
    RadioButton vOther;

    @Override
    protected int getLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void initializeViews() {

        vName.setText("Cosme");
        vSurname.setText("Fulanito");
        vId.setText("22.33.48");
        vMale.setSelected(true);

        
    }

}