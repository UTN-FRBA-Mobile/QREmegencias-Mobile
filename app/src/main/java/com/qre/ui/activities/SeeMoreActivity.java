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
import android.widget.TextView;

import com.google.gson.Gson;
import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.EmergencyDataDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkException;
import com.qre.services.networking.NetworkService;
import com.qre.ui.adapters.EmergencyDataAdapter;
import com.qre.ui.components.DetailValueView;
import com.qre.utils.CryptoUtils;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qre.utils.Constants.INTENT_EXTRA_UUID;

public class SeeMoreActivity extends AppCompatActivity {

    private static final String TAG = SeeMoreActivity.class.getSimpleName();

    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd / MM / yyyy");

    public static Intent getIntent(final Context context) {
        return new Intent(context, SeeMoreActivity.class);
    }

    @Inject
    NetworkService networkService;

    @Inject
    Gson gson;

    @BindView(R.id.loader_seemore)
    View vLoader;

    @BindView(R.id.exception_frame_seemore)
    View vException;

    @BindView(R.id.exception_textview_seemore)
    TextView tException;

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

        networkService.getPublicEmergencyData(getIntent().getStringExtra(INTENT_EXTRA_UUID), new NetCallback<String>() {
            @Override
            public void onSuccess(String response) {

                vLoader.setVisibility(View.GONE);

                final InputStream key = getResources().openRawResource(R.raw.privatekey);
                try {

                    String decrypted = new String(CryptoUtils.decryptText(response, key), "ISO-8859-1");
                    EmergencyDataDTO emergencyDataDTO = gson.fromJson(decrypted, EmergencyDataDTO.class);

                    List<Object> collection = new ArrayList<>();

                    if (emergencyDataDTO.getGeneral() != null) {

                        final LocalDate lastMedicalCheck = emergencyDataDTO.getGeneral().getLastMedicalCheck();
                        if (lastMedicalCheck != null) {
                            vLastMedicalCheck.setValue(lastMedicalCheck.format(DATE_FORMATTER));
                        }
                        vBloodType.setValue(emergencyDataDTO.getGeneral().getBloodType());
                        vOrganDonor.setValue(emergencyDataDTO.getGeneral().isOrganDonor() ? getString(R.string.yes) : getString(R.string.no));

                        if (!emergencyDataDTO.getGeneral().getAllergies().isEmpty()) {
                            collection.add(R.string.allergies);
                            collection.addAll(emergencyDataDTO.getGeneral().getAllergies());
                        }
                    }

                    if (emergencyDataDTO.getSurgeries() != null && !emergencyDataDTO.getSurgeries().isEmpty()) {
                        collection.add(R.string.surgeries);
                        collection.addAll(emergencyDataDTO.getSurgeries());
                    }

                    if (emergencyDataDTO.getHospitalizations() != null && !emergencyDataDTO.getHospitalizations().isEmpty()) {
                        collection.add(R.string.hospitalizations);
                        collection.addAll(emergencyDataDTO.getHospitalizations());
                    }

                    if (emergencyDataDTO.getPathologies() != null && !emergencyDataDTO.getPathologies().isEmpty()) {
                        collection.add(R.string.pathologies);
                        collection.addAll(emergencyDataDTO.getPathologies());
                    }

                    if (emergencyDataDTO.getMedications() != null && !emergencyDataDTO.getMedications().isEmpty()) {
                        collection.add(R.string.medications);
                        collection.addAll(emergencyDataDTO.getMedications());
                    }

                    if (emergencyDataDTO.getContacts() != null && !emergencyDataDTO.getContacts().isEmpty()) {
                        collection.add(R.string.contacts);
                        collection.addAll(emergencyDataDTO.getContacts());
                    }

                    vCollection.setLayoutManager(new LinearLayoutManager(SeeMoreActivity.this));
                    vCollection.setAdapter(new EmergencyDataAdapter(SeeMoreActivity.this, collection));
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(vCollection.getContext(), DividerItemDecoration.VERTICAL);
                    vCollection.addItemDecoration(dividerItemDecoration);
                } catch (final Exception e) {
                    Log.e(TAG, "ERROR:  Error al desencriptar contenido web", e);
                    tException.setText("No se pudo cargar la información.\nAsegurate que el QR sea el actual.");
                    vLoader.setVisibility(View.GONE);
                    vException.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.i(TAG, "ERROR:  " + exception);
                if (exception instanceof NetworkException) {
                    tException.setText(exception.getMessage());
                } else {
                    tException.setText("No se pudo cargar la información.\nError al conectarse con el servidor.");
                }
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