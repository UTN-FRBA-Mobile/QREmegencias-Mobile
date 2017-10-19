package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.networking.NetworkService;
import com.qre.services.preference.impl.UserPreferenceService;
import com.qre.ui.fragments.ProfileFragment;
import com.qre.ui.fragments.medical.MedicalEditUserFragment;
import com.qre.ui.fragments.medical.MedicalEmergencyDataFragment;
import com.qre.ui.fragments.user.UserClinicalHistoryFragment;
import com.qre.ui.fragments.user.UserEmergencyDataFragment;
import com.qre.ui.fragments.user.UserManageQRFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final String ROLE_MEDICAL = "ROLE_MEDICO";
    private static final String ROLE_USER = "ROLE_PACIENTE";

    @Inject
    UserPreferenceService userPreferenceService;

    @Inject
    NetworkService networkService;

    @BindView(R.id.drawer_layout)
    DrawerLayout vDrawerLayout;

    @BindView(R.id.navigation)
    NavigationView vNavigationView;

    @BindView(R.id.appbar)
    Toolbar toolbar;

    public static Intent getIntent(final Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Injector.getServiceComponent().inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDrawer();
    }

    private void initDrawer() {

        String role = userPreferenceService.getRole();
        Log.i(TAG, "Prepare menu for user with role " + role);
        vNavigationView.getMenu().setGroupVisible(R.id.menu_medical, role.equals(ROLE_MEDICAL));
        vNavigationView.getMenu().setGroupVisible(R.id.menu_user, role.equals(ROLE_USER));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MedicalEmergencyDataFragment())
                .commit();
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_medical_emergency_data));

        vDrawerLayout.closeDrawers();

        vNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                        Fragment fragment = null;

                        switch (menuItem.getItemId()) {
//							case R.id.menu_medical_profile:
//								fragment = new ProfileFragment();
//								fragmentTransaction = true;
//								break;
                            case R.id.menu_medical_emergency_data:
                                fragment = new MedicalEmergencyDataFragment();
                                fragmentTransaction = true;
                                break;
							case R.id.menu_medical_edit_user:
								fragment = new MedicalEditUserFragment();
								fragmentTransaction = true;
								break;
                            case R.id.menu_user_profile:
                                fragment = new ProfileFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_user_clinical_history:
                                fragment = new UserClinicalHistoryFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_user_emergency_data:
                                fragment = new UserEmergencyDataFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_user_manage_qr:
                                fragment = new UserManageQRFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_logout:
                                userPreferenceService.delete();
                                startActivity(LoginActivity.getIntent(HomeActivity.this));
                                break;
                        }

                        if (fragmentTransaction) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();
                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        vDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                vDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}