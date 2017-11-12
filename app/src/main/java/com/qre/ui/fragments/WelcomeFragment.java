package com.qre.ui.fragments;

import android.view.View;
import android.widget.TextView;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.preference.impl.UserPreferenceService;

import javax.inject.Inject;

import butterknife.BindView;

public class WelcomeFragment extends BaseFragment {

    private static final String TAG = WelcomeFragment.class.getSimpleName();

    @BindView(R.id.text_welcome)
    TextView vWelcomeText;

    @Inject
    UserPreferenceService userPreferenceService;

    @Override
    protected int getLayout() {
        return R.layout.fragment_home_page_welcome;
    }

    @Override
    protected void initializeViews() {

        Injector.getServiceComponent().inject(this);

        final String fullName = userPreferenceService.getNameAndLastname();

        vWelcomeText.setText("¡Bienvenido, " + fullName + "!");

    }

}