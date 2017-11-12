package com.qre.ui.fragments.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.EmergencyDataDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkException;
import com.qre.services.networking.NetworkService;
import com.qre.ui.adapters.EmergencyDataAdapter;
import com.qre.ui.components.DetailValueView;
import com.qre.ui.fragments.BaseFragment;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static com.qre.ui.activities.SeeMoreActivity.DATE_FORMATTER;

public class UserEmergencyDataFragment extends BaseFragment {

    private static final String TAG = UserEmergencyDataFragment.class.getSimpleName();

    @Inject
    NetworkService networkService;

    @BindView(R.id.loader_seemore)
    View vLoader;

    @BindView(R.id.exception_frame_seemore)
    View vException;

    @BindView(R.id.exception_textview_seemore)
    TextView tException;

    @BindView(R.id.last_medical_check)
    DetailValueView vLastMedicalCheck;

    @BindView(R.id.blood_type)
    DetailValueView vBloodType;

    @BindView(R.id.organ_donor)
    DetailValueView vOrganDonor;

    @BindView(R.id.collection)
    RecyclerView vCollection;

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_emergency_data;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getServiceComponent().inject(this);
    }

    @Override
    protected void initializeViews() {
        vLoader.setVisibility(View.VISIBLE);

        networkService.getEmergencyData(new NetCallback<EmergencyDataDTO>() {
            @Override
            public void onSuccess(final EmergencyDataDTO emergencyDataDTO) {

                vLoader.setVisibility(View.GONE);

                try {

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

                    vCollection.setLayoutManager(new LinearLayoutManager(getContext()));
                    vCollection.setAdapter(new EmergencyDataAdapter(getContext(), collection));
                } catch (final Exception e) {
                    Log.e(TAG, "ERROR:  Error al obtener contenido web", e);
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
}