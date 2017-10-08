package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.EmergencyDataDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
<<<<<<< HEAD
import com.qre.ui.adapters.EmergencyDataAdapter;
import com.qre.ui.components.DetailValueView;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
=======
import com.qre.utils.CryptoUtils;

import org.aaronhe.threetengson.ThreeTenGsonAdapter;

import java.io.InputStream;
>>>>>>> encripcion de la web

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.aaronhe.threetengson.ThreeTenGsonAdapter.*;

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

        networkService.getPublicEmergencyData(getIntent().getStringExtra("uuid"), new NetCallback<String>() {
            @Override
            public void onSuccess(String response) {

                vLoader.setVisibility(View.GONE);

                final InputStream key = getResources().openRawResource(R.raw.privatekey);
                try {

                    String decrypted = new String(CryptoUtils.decryptText(response, key),"ISO-8859-1").replaceAll("00:00:00","00:00:00Z");
                    Gson gson = registerAll((new GsonBuilder())).create();
                    EmergencyDataDTO emergencyDataDTO = gson.fromJson(decrypted,EmergencyDataDTO.class);

                vLastMedicalCheck.setValue(emergencyDataDTO.getLastMedicalCheck().format(DATE_FORMATTER));
                vBloodType.setValue(emergencyDataDTO.getGeneral().getBloodType());
                vOrganDonor.setValue(emergencyDataDTO.getGeneral().isOrganDonor() ? getString(R.string.yes) : getString(R.string.no));

                List<Object> collection = new ArrayList<>();

                if (!emergencyDataDTO.getGeneral().getAllergies().isEmpty()) {
                    collection.add(R.string.allergies);
                    collection.addAll(emergencyDataDTO.getGeneral().getAllergies());
                }

                if (!emergencyDataDTO.getSurgeries().isEmpty()) {
                    collection.add(R.string.surgeries);
                    collection.addAll(emergencyDataDTO.getSurgeries());
                }

                if (!emergencyDataDTO.getHospitalizations().isEmpty()) {
                    collection.add(R.string.hospitalizations);
                    collection.addAll(emergencyDataDTO.getHospitalizations());
                }

                if (!emergencyDataDTO.getPathologies().isEmpty()) {
                    collection.add(R.string.pathologies);
                    collection.addAll(emergencyDataDTO.getPathologies());
                }

                if (!emergencyDataDTO.getMedications().isEmpty()) {
                    collection.add(R.string.medications);
                    collection.addAll(emergencyDataDTO.getMedications());
                }

                vCollection.setLayoutManager(new LinearLayoutManager(SeeMoreActivity.this));
                vCollection.setAdapter(new EmergencyDataAdapter(SeeMoreActivity.this, collection));
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(vCollection.getContext(), DividerItemDecoration.VERTICAL);
                vCollection.addItemDecoration(dividerItemDecoration);

                getIntent().putExtra("response", emergencyDataDTO.toString());

                Log.d(TAG, "JSON: " + emergencyDataDTO.toString() );

                } catch (final Exception e) {
                    Log.e(TAG, "ERROR:  Error al desencriptar contenido web", e);
                }
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