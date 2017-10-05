package com.qre.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.EmergencyDataDTO;
import com.qre.models.HospitalizationDTO;
import com.qre.models.MedicationDTO;
import com.qre.models.PathologyDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SeeMoreActivity extends AppCompatActivity {

    private static final String TAG = SeeMoreActivity.class.getSimpleName();

	public static Intent getIntent(final Context context) {
		return new Intent(context, SeeMoreActivity.class);
	}

    @BindView(R.id.textview_see_more)
    TextView textview_see_more;

    @Inject
    NetworkService networkService;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_seemore);
		ButterKnife.bind(this);

        Injector.getServiceComponent().inject(this);

        final ProgressDialog pdLoading = new ProgressDialog(SeeMoreActivity.this);

        pdLoading.setMessage("\tCargando...");
        pdLoading.setCancelable(true);
        pdLoading.show();

        networkService.getPublicEmergencyData(getIntent().getStringExtra("uuid"), new NetCallback<EmergencyDataDTO>() {
            @Override
            public void onSuccess(EmergencyDataDTO response) {

                pdLoading.dismiss();

                String text = "";

                text += "- Último chequeo médico : " + response.getLastMedicalCheck() + "\n\n";
                text += "- Tipo sangre : " + response.getGeneral().getBloodType() + "\n\n";
                text += "- Donante de órganos : " +
                        ((response.getGeneral().getOrganDonor()) ? "Sí" : "No") + "\n\n";

                if (!response.getGeneral().getAllergies().isEmpty()) {
                    text += "- Alergias: \n";
                    for (String allergy : response.getGeneral().getAllergies()) {
                        text += "\t + " + allergy + "\n";
                    }
                    text += "\n";
                }

                for(HospitalizationDTO surgery : response.getSurgeries()) {
                    text += "- Operacion : \n";
                    text += "\t" + "Institución : " + surgery.getInstitution() + "\n";
                    text += "\t" + "Tipo : " + surgery.getType() + "\n";
                    text += "\t" + "Fecha : " + surgery.getDate() + "\n";
                    text += "\t" + "Motivo : " + surgery.getReason() + "\n\n";
                }

                for(HospitalizationDTO hospitalization : response.getHospitalizations()) {
                    text += "- Operacion : \n";
                    text += "\t" + "Institución : " + hospitalization.getInstitution() + "\n";
                    text += "\t" + "Tipo : " + hospitalization.getType() + "\n";
                    text += "\t" + "Fecha : " + hospitalization.getDate() + "\n";
                    text += "\t" + "Motivo : " + hospitalization.getReason() + "\n\n";
                }

                for(PathologyDTO pathology : response.getPathologies()) {
                    text += "- Patología : \n";
                    text += "\t" + "Tipo : " + pathology.getType() + "\n";
                    text += "\t" + "Descripción : " + pathology.getDescription() + "\n";
                    text += "\t" + "Fecha : " + pathology.getDate() + "\n\n";
                }

                for(MedicationDTO medication : response.getMedications()) {

                    text += "- Medicación : \n";
                    text += "\t" + "Nombre : " + medication.getName() + "\n";
                    text += "\t" + "Descripcion : " + medication.getDescription() + "\n";
                    text += "\t" + "Cantidad : " + medication.getAmount() + "\n";
                    text += "\t" + "Período : " + medication.getPeriod() + "\n\n";
                }
                textview_see_more.setText(text);
                textview_see_more.setMovementMethod(new ScrollingMovementMethod());

                getIntent().putExtra("response", response.toString());
                Log.i(TAG, "JSON: " + response.toString() );
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.i(TAG, "ERROR:  " + exception );
            }
        });

	}

}

