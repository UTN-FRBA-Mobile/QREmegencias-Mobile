package com.qre.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.EmergencyDataDTO;
import com.qre.models.HospitalizationDTO;
import com.qre.models.MedicationDTO;
import com.qre.models.PathologyDTO;
import com.qre.models.UserContactDTO;
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

public class EditEmergencyDataActivity extends AppCompatActivity implements EmergencyDataAdapter.Listener {

    private static final String TAG = EditEmergencyDataActivity.class.getSimpleName();

    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd / MM / yyyy");

    public static Intent getIntent(final Context context) {
        return new Intent(context, EditEmergencyDataActivity.class);
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

    private EmergencyDataDTO data;

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
            public void onSuccess(final String response) {
                vLoader.setVisibility(View.GONE);
                final InputStream key = getResources().openRawResource(R.raw.privatekey);
                try {
                    String decrypted = new String(CryptoUtils.decryptText(response, key), "ISO-8859-1");
                    EmergencyDataDTO emergencyDataDTO = gson.fromJson(decrypted, EmergencyDataDTO.class);
                    data = emergencyDataDTO;
                    initialize();
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

    public void initialize() {

        List<Object> collection = new ArrayList<>();

        if (data.getGeneral() != null) {

            final LocalDate lastMedicalCheck = data.getGeneral().getLastMedicalCheck();
            if (lastMedicalCheck != null) {
                vLastMedicalCheck.setValue(lastMedicalCheck.format(DATE_FORMATTER));
            }

            vBloodType.setValue(data.getGeneral().getBloodType());
            vOrganDonor.setValue(data.getGeneral().isOrganDonor() ? getString(R.string.yes) : getString(R.string.no));

            if (!data.getGeneral().getAllergies().isEmpty()) {
                collection.add(EmergencyDataAdapter.TYPE_ALLERGY);
                collection.addAll(data.getGeneral().getAllergies());
            }
        }

        if (data.getSurgeries() != null && !data.getSurgeries().isEmpty()) {
            collection.add(EmergencyDataAdapter.TYPE_SURGERY);
            collection.addAll(data.getSurgeries());
        }

        if (data.getHospitalizations() != null && !data.getHospitalizations().isEmpty()) {
            collection.add(EmergencyDataAdapter.TYPE_HOSPITALIZATION);
            collection.addAll(data.getHospitalizations());
        }

        if (data.getPathologies() != null && !data.getPathologies().isEmpty()) {
            collection.add(EmergencyDataAdapter.TYPE_PATHOLOGY);
            collection.addAll(data.getPathologies());
        }

        if (data.getMedications() != null && !data.getMedications().isEmpty()) {
            collection.add(EmergencyDataAdapter.TYPE_MEDICATION);
            collection.addAll(data.getMedications());
        }

        if (data.getContacts() != null && !data.getContacts().isEmpty()) {
            collection.add(EmergencyDataAdapter.TYPE_CONTACT);
            collection.addAll(data.getContacts());
        }

        EmergencyDataAdapter adapter = new EmergencyDataAdapter(this, collection, true);
        adapter.setListener(this);
        vCollection.setLayoutManager(new LinearLayoutManager(this));
        vCollection.setAdapter(adapter);
    }

    @Override
    public void onAddItem(final int type) {
        switch (type) {
            case EmergencyDataAdapter.TYPE_ALLERGY:
                openAllergyDialog(null, false);
                break;
            case EmergencyDataAdapter.TYPE_SURGERY:
                openSurgeryDialog(new HospitalizationDTO(), false);
                break;
            case EmergencyDataAdapter.TYPE_HOSPITALIZATION:
                openHospitalizationDialog(new HospitalizationDTO(), false);
                break;
            case EmergencyDataAdapter.TYPE_MEDICATION:
                openMedicationDialog(new MedicationDTO(), false);
                break;
            case EmergencyDataAdapter.TYPE_PATHOLOGY:
                openPathologyDialog(new PathologyDTO(), false);
                break;
            case EmergencyDataAdapter.TYPE_CONTACT:
                openContactDialog(new UserContactDTO(), false);
                break;
        }
    }

    @Override
    public void onEditItem(final int type, final Object item) {
        switch (type) {
            case EmergencyDataAdapter.TYPE_ALLERGY:
                openAllergyDialog((String) item, true);
                break;
            case EmergencyDataAdapter.TYPE_SURGERY:
                openSurgeryDialog((HospitalizationDTO) item, true);
                break;
            case EmergencyDataAdapter.TYPE_HOSPITALIZATION:
                openHospitalizationDialog((HospitalizationDTO) item, true);
                break;
            case EmergencyDataAdapter.TYPE_MEDICATION:
                openMedicationDialog((MedicationDTO) item, true);
                break;
            case EmergencyDataAdapter.TYPE_PATHOLOGY:
                openPathologyDialog((PathologyDTO) item, true);
                break;
            case EmergencyDataAdapter.TYPE_CONTACT:
                openContactDialog((UserContactDTO) item, true);
                break;
        }
    }

    @Override
    public void onRemoveItem(final int type, final Object item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirmation))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (type) {
                            case EmergencyDataAdapter.TYPE_ALLERGY:
                                data.getGeneral().getAllergies().remove(item);
                                break;
                            case EmergencyDataAdapter.TYPE_SURGERY:
                                data.getSurgeries().remove(item);
                                break;
                            case EmergencyDataAdapter.TYPE_HOSPITALIZATION:
                                data.getHospitalizations().remove(item);
                                break;
                            case EmergencyDataAdapter.TYPE_MEDICATION:
                                data.getMedications().remove(item);
                                break;
                            case EmergencyDataAdapter.TYPE_PATHOLOGY:
                                data.getPathologies().remove(item);
                                break;
                            case EmergencyDataAdapter.TYPE_CONTACT:
                                data.getContacts().remove(item);
                                break;
                        }
                        initialize();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void openAllergyDialog(final String allergy, final boolean existent) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_allergy, null);
        dialogBuilder.setView(dialogView);

        final EditText vDescription = (EditText) dialogView.findViewById(R.id.input_description);

        vDescription.setText(allergy);

        dialogBuilder.setTitle(getString(R.string.allergy));
        dialogBuilder.setPositiveButton(getString(R.string.accept), null);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog b = dialogBuilder.create();

        b.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        boolean ok = true;

                        if (vDescription.getText().toString().isEmpty()) {
                            ok = false;
                            vDescription.setError(getString(R.string.required_field));
                        }

                        if (ok) {

                            if (existent) {
                                data.getGeneral().getAllergies().remove(allergy);
                            }

                            data.getGeneral().addAllergiesItem(vDescription.getText().toString());

                            dialog.dismiss();

                            initialize();
                        }
                    }
                });
            }
        });

        b.show();
    }

    public void openSurgeryDialog(final HospitalizationDTO hospitalization, final boolean existent) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_hospitalization, null);
        dialogBuilder.setView(dialogView);

        final Spinner vType = (Spinner) dialogView.findViewById(R.id.input_type);
        final EditText vDate = (EditText) dialogView.findViewById(R.id.input_date);
        final EditText vDescription = (EditText) dialogView.findViewById(R.id.input_description);

        vDate.setText(hospitalization.getDate() == null ? LocalDate.now().format(DATE_FORMATTER) : hospitalization.getDate().format(DATE_FORMATTER));
        vDescription.setText(hospitalization.getReason());

        dialogBuilder.setTitle(getString(R.string.surgery));
        dialogBuilder.setPositiveButton(getString(R.string.accept), null);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog b = dialogBuilder.create();

        b.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        boolean ok = true;

                        if (vDate.getText().toString().isEmpty()) {
                            ok = false;
                            vDate.setError(getString(R.string.required_field));
                        }

                        if (vDescription.getText().toString().isEmpty()) {
                            ok = false;
                            vDescription.setError(getString(R.string.required_field));
                        }

                        if (ok) {

                            if (!existent) {
                                data.addHospitalizationsItem(hospitalization);
                            }

                            hospitalization.setType(HospitalizationDTO.TypeEnum.valueOf(vType.getSelectedItem().toString()));
                            hospitalization.setReason(vDescription.getText().toString());

                            dialog.dismiss();

                            initialize();
                        }
                    }
                });
            }
        });

        b.show();
    }

    public void openHospitalizationDialog(final HospitalizationDTO hospitalization, final boolean existent) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_hospitalization, null);
        dialogBuilder.setView(dialogView);

        final EditText vInstitution = (EditText) dialogView.findViewById(R.id.input_institution);
        final Spinner vType = (Spinner) dialogView.findViewById(R.id.input_type);
        final EditText vDate = (EditText) dialogView.findViewById(R.id.input_date);
        final EditText vReason = (EditText) dialogView.findViewById(R.id.input_reason);

        vInstitution.setText(hospitalization.getInstitution());
        vDate.setText(hospitalization.getDate() == null ? LocalDate.now().format(DATE_FORMATTER) : hospitalization.getDate().format(DATE_FORMATTER));
        vReason.setText(hospitalization.getReason());

        dialogBuilder.setTitle(getString(R.string.hospitalization));
        dialogBuilder.setPositiveButton(getString(R.string.accept), null);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog b = dialogBuilder.create();

        b.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        boolean ok = true;

                        if (vInstitution.getText().toString().isEmpty()) {
                            ok = false;
                            vInstitution.setError(getString(R.string.required_field));
                        }

                        if (vDate.getText().toString().isEmpty()) {
                            ok = false;
                            vDate.setError(getString(R.string.required_field));
                        }

                        if (vReason.getText().toString().isEmpty()) {
                            ok = false;
                            vReason.setError(getString(R.string.required_field));
                        }

                        if (ok) {

                            if (!existent) {
                                data.addHospitalizationsItem(hospitalization);
                            }

                            hospitalization.setInstitution(vInstitution.getText().toString());
                            hospitalization.setType(HospitalizationDTO.TypeEnum.valueOf(vType.getSelectedItem().toString()));
                            hospitalization.setReason(vReason.getText().toString());

                            dialog.dismiss();

                            initialize();
                        }
                    }
                });
            }
        });

        b.show();
    }

    public void openMedicationDialog(final MedicationDTO medication, final boolean existent) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_medication, null);
        dialogBuilder.setView(dialogView);

        final EditText vName = (EditText) dialogView.findViewById(R.id.input_name);
        final EditText vDescription = (EditText) dialogView.findViewById(R.id.input_description);
        final EditText vAmount = (EditText) dialogView.findViewById(R.id.input_amount);
        final EditText vPeriod = (EditText) dialogView.findViewById(R.id.input_period);

        vName.setText(medication.getName());
        vDescription.setText(medication.getDescription());
        vAmount.setText(medication.getAmount());
        vPeriod.setText(String.valueOf(medication.getPeriod()));

        dialogBuilder.setTitle(getString(R.string.medication));
        dialogBuilder.setPositiveButton(getString(R.string.accept), null);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog b = dialogBuilder.create();

        b.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        boolean ok = true;

                        if (vName.getText().toString().isEmpty()) {
                            ok = false;
                            vName.setError(getString(R.string.required_field));
                        }

                        if (vDescription.getText().toString().isEmpty()) {
                            ok = false;
                            vDescription.setError(getString(R.string.required_field));
                        }

                        if (vAmount.getText().toString().isEmpty()) {
                            ok = false;
                            vAmount.setError(getString(R.string.required_field));
                        }

                        if (vPeriod.getText().toString().isEmpty()) {
                            ok = false;
                            vPeriod.setError(getString(R.string.required_field));
                        }

                        if (ok) {

                            if (!existent) {
                                data.addMedicationsItem(medication);
                            }

                            medication.setName(vName.getText().toString());
                            medication.setDescription(vDescription.getText().toString());
                            medication.setAmount(Integer.valueOf(vAmount.getText().toString()));
                            medication.setPeriod(MedicationDTO.PeriodEnum.valueOf(vPeriod.getText().toString()));

                            dialog.dismiss();

                            initialize();
                        }
                    }
                });
            }
        });

        b.show();
    }

    public void openPathologyDialog(final PathologyDTO pathology, final boolean existent) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_pathology, null);
        dialogBuilder.setView(dialogView);

        final Spinner vType = (Spinner) dialogView.findViewById(R.id.input_type);
        final EditText vDescription = (EditText) dialogView.findViewById(R.id.input_description);
        final EditText vDate = (EditText) dialogView.findViewById(R.id.input_date);

        vDescription.setText(pathology.getDescription());
        vDate.setText(pathology.getDate() == null ? LocalDate.now().format(DATE_FORMATTER) : pathology.getDate().format(DATE_FORMATTER));

        dialogBuilder.setTitle(getString(R.string.pathology));
        dialogBuilder.setPositiveButton(getString(R.string.accept), null);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog b = dialogBuilder.create();

        b.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        boolean ok = true;

                        if (vDescription.getText().toString().isEmpty()) {
                            ok = false;
                            vDescription.setError(getString(R.string.required_field));
                        }

                        if (vDate.getText().toString().isEmpty()) {
                            ok = false;
                            vDate.setError(getString(R.string.required_field));
                        }

                        if (ok) {

                            if (!existent) {
                                data.addPathologiesItem(pathology);
                            }

                            pathology.setType(PathologyDTO.TypeEnum.valueOf(vType.getSelectedItem().toString()));
                            pathology.setDescription(vDescription.getText().toString());

                            dialog.dismiss();

                            initialize();
                        }
                    }
                });
            }
        });

        b.show();
    }

    public void openContactDialog(final UserContactDTO contact, final boolean existent) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_contact, null);
        dialogBuilder.setView(dialogView);

        final EditText vName = (EditText) dialogView.findViewById(R.id.input_name);
        final EditText vSurname = (EditText) dialogView.findViewById(R.id.input_surname);
        final EditText vPhone = (EditText) dialogView.findViewById(R.id.input_phone);

        vName.setText(contact.getFirstName());
        vSurname.setText(contact.getLastName());
        vPhone.setText(contact.getPhoneNumber());

        dialogBuilder.setTitle(getString(R.string.contact));
        dialogBuilder.setPositiveButton(getString(R.string.accept), null);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        AlertDialog b = dialogBuilder.create();

        b.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        boolean ok = true;

                        if (vName.getText().toString().isEmpty()) {
                            ok = false;
                            vName.setError(getString(R.string.required_field));
                        }

                        if (vSurname.getText().toString().isEmpty()) {
                            ok = false;
                            vSurname.setError(getString(R.string.required_field));
                        }

                        if (vPhone.getText().toString().isEmpty()) {
                            ok = false;
                            vPhone.setError(getString(R.string.required_field));
                        }

                        if (ok) {

                            if (!existent) {
                                data.addContactsItem(contact);
                            }

                            contact.setFirstName(vName.getText().toString());
                            contact.setLastName(vSurname.getText().toString());
                            contact.setPhoneNumber(vPhone.getText().toString());

                            dialog.dismiss();

                            initialize();
                        }
                    }
                });
            }
        });

        b.show();
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