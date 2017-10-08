package com.qre.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.EmergencyDataDTO;
import com.qre.models.HospitalizationDTO;
import com.qre.models.MedicationDTO;
import com.qre.models.PathologyDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.ui.adapters.EmergencyDataAdapter;
import com.qre.ui.adapters.EnumerationAdapter;
import com.qre.ui.components.DetailValueView;

import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SeeMoreActivity extends AppCompatActivity {

    private static final String TAG = SeeMoreActivity.class.getSimpleName();

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");

	public static Intent getIntent(final Context context) {
		return new Intent(context, SeeMoreActivity.class);
	}

    @Inject
    NetworkService networkService;

    @BindView(R.id.loader)
    View vLoader;

    @BindView(R.id.exception)
    View vException;

    @BindView(R.id.toolbar)
    Toolbar vToolbar;

    @BindView(R.id.last_medical_check)
    DetailValueView vLastMedicalCheck;

    @BindView(R.id.blood_type)
    DetailValueView vBloodType;

    @BindView(R.id.organ_donor)
    DetailValueView vOrganDonor;

    @BindView(R.id.collection)
    RecyclerView vCollection;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_seemore);
		ButterKnife.bind(this);

        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Injector.getServiceComponent().inject(this);

        vLoader.setVisibility(View.VISIBLE);

        networkService.getPublicEmergencyData(getIntent().getStringExtra("uuid"), new NetCallback<EmergencyDataDTO>() {

            @Override
            public void onSuccess(EmergencyDataDTO response) {

                vLoader.setVisibility(View.GONE);

                vLastMedicalCheck.setValue(response.getLastMedicalCheck().format(DATE_FORMATTER));
                vBloodType.setValue(response.getGeneral().getBloodType());
                vOrganDonor.setValue(response.getGeneral().getOrganDonor() ? getString(R.string.yes) : getString(R.string.no));

                List<Object> collection = new ArrayList<>();

                if (!response.getGeneral().getAllergies().isEmpty()) {
                    collection.add(R.string.allergies);
                    collection.addAll(response.getGeneral().getAllergies());
                }

                if (!response.getSurgeries().isEmpty()) {
                    collection.add(R.string.surgeries);
                    collection.addAll(response.getSurgeries());
                }

                if (!response.getHospitalizations().isEmpty()) {
                    collection.add(R.string.hospitalizations);
                    collection.addAll(response.getHospitalizations());
                }

                if (!response.getPathologies().isEmpty()) {
                    collection.add(R.string.pathologies);
                    collection.addAll(response.getPathologies());
                }

                if (!response.getMedications().isEmpty()) {
                    collection.add(R.string.medications);
                    collection.addAll(response.getMedications());
                }

                vCollection.setLayoutManager(new LinearLayoutManager(SeeMoreActivity.this));
                vCollection.setAdapter(new EmergencyDataAdapter(SeeMoreActivity.this, collection));
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(vCollection.getContext(), DividerItemDecoration.VERTICAL);
                vCollection.addItemDecoration(dividerItemDecoration);

                getIntent().putExtra("response", response.toString());

                Log.d(TAG, "JSON: " + response.toString() );
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.i(TAG, "ERROR:  " + exception );
                vLoader.setVisibility(View.GONE);
                vException.setVisibility(View.VISIBLE);
            }

        });
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}