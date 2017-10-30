package com.qre.ui.fragments;

import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.UserProfileDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;

import org.threeten.bp.format.DateTimeFormatter;

import javax.inject.Inject;

import butterknife.BindView;

public class ProfileFragment extends BaseFragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd / MM / yyyy");

    @Inject
    NetworkService networkService;

    @BindView(R.id.input_name)
    EditText vName;

    @BindView(R.id.input_surname)
    EditText vSurname;

    @BindView(R.id.input_id)
    EditText vId;

    @BindView(R.id.input_birthday)
    EditText vBirthday;

    @BindView(R.id.male)
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

        Injector.getServiceComponent().inject(this);

        networkService.getProfile(new NetCallback<UserProfileDTO>() {

            @Override
            public void onSuccess(UserProfileDTO profile) {
                vName.setText(profile.getFirstName());
                vSurname.setText(profile.getLastName());
                vId.setText(profile.getIdNumber());
                vBirthday.setText(profile.getBirthDate().format(DATE_FORMATTER));
                switch (profile.getSex()) {
                    case "M": vMale.setSelected(true);
                    case "F": vFemale.setSelected(true);
                    default: vOther.setSelected(true);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "Cannot get profile", e);
            }
        });
    }

}