package com.qre.ui.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.UserContactDTO;
import com.qre.models.UserProfileDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.ui.activities.MedicalClinicalHistoryActivity;
import com.qre.ui.adapters.EmergencyDataAdapter;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

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

    @BindView(R.id.input_sex)
    RadioGroup vSex;

    @BindView(R.id.male)
    RadioButton vMale;

    @BindView(R.id.female)
    RadioButton vFemale;

    @BindView(R.id.other)
    RadioButton vOther;

    @BindView(R.id.btn_save)
    Button vSave;

    @BindView(R.id.contacts)
    RecyclerView vContacts;

    private UserProfileDTO profile;

    @Override
    protected int getLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void initializeViews() {

        Injector.getServiceComponent().inject(this);

        getActivity().findViewById(R.id.loader).setVisibility(View.VISIBLE);

        networkService.getProfile(new NetCallback<UserProfileDTO>() {

            @Override
            public void onSuccess(UserProfileDTO response) {
                getActivity().findViewById(R.id.loader).setVisibility(View.GONE);
                profile = response;
                vName.setText(profile.getFirstName());
                vSurname.setText(profile.getLastName());
                vId.setText(profile.getIdNumber());
                vBirthday.setText(profile.getBirthDate().format(DATE_FORMATTER));
                vMale.setChecked("M".equals(profile.getSex()));
                vFemale.setChecked("F".equals(profile.getSex()));
                vOther.setChecked("O".equals(profile.getSex()));
                vContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
                vContacts.setAdapter(new EmergencyDataAdapter(getActivity(), profile.getContacts()));
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(vContacts.getContext(), DividerItemDecoration.VERTICAL);
                vContacts.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onFailure(Throwable e) {
                getActivity().findViewById(R.id.loader).setVisibility(View.GONE);
                Log.e(TAG, "Cannot get profile", e);
            }
        });
    }

    @OnClick(R.id.btn_save)
    public void save() {

        boolean ok = true;

        if (vName.getText().toString().isEmpty()) {
            ok = false;
            vName.setError(getString(R.string.required_field));
        }

        if (vSurname.getText().toString().isEmpty()) {
            ok = false;
            vSurname.setError(getString(R.string.required_field));
        }

        if (vId.getText().toString().isEmpty()) {
            ok = false;
            vId.setError(getString(R.string.required_field));
        }

        if (ok) {

            vSave.setEnabled(false);
            final Drawable vSaveBackground = vSave.getBackground();
            vSave.setBackgroundColor(Color.GRAY);

            profile.setFirstName(vName.getText().toString());
            profile.setLastName(vSurname.getText().toString());
            profile.setIdNumber(vId.getText().toString());

            switch (vSex.getCheckedRadioButtonId()) {
                case R.id.male:
                    profile.setSex("M");
                    break;
                case R.id.female:
                    profile.setSex("F");
                    break;
                case R.id.other:
                    profile.setSex("O");
                    break;
            }

            networkService.updateProfile(profile, true, new NetCallback<Void>() {

                @Override
                public void onSuccess(Void response) {
                    vSave.setEnabled(true);
                    vSave.setBackground(vSaveBackground);
                    Toast.makeText(getContext(), getString(R.string.save_profile_success), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e(TAG, "Cannot update profile", e);
                    vSave.setEnabled(true);
                    vSave.setBackground(vSaveBackground);
                    Toast.makeText(getContext(), getString(R.string.save_profile_error), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getContext(), getString(R.string.required_fields_error), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_add_contact)
    public void openContactDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_contact, null);
        dialogBuilder.setView(dialogView);

        final EditText vName = (EditText) dialogView.findViewById(R.id.input_name);
        final EditText vSurname = (EditText) dialogView.findViewById(R.id.input_surname);
        final EditText vPhone = (EditText) dialogView.findViewById(R.id.input_phone);

        dialogBuilder.setTitle(getString(R.string.add_contact));
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
                            UserContactDTO contact = new UserContactDTO();
                            contact.setFirstName(vName.getText().toString());
                            contact.setLastName(vSurname.getText().toString());
                            contact.setPhoneNumber(vPhone.getText().toString());
                            profile.getContacts().add(contact);
                            vContacts.getAdapter().notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        b.show();
    }

    @OnClick(R.id.input_birthday)
    public void openBirthdayDialog() {
        DialogFragment dialog = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DatePickerFragment.DATE, profile.getBirthDate());
        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, DatePickerFragment.CODE);
        dialog.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DatePickerFragment.CODE) {
            LocalDate date = (LocalDate) data.getSerializableExtra(DatePickerFragment.DATE);
            profile.setBirthDate(date);
            vBirthday.setText(date.format(DATE_FORMATTER));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public static final int CODE = 1;
        public static final String DATE = "date";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LocalDate date = (LocalDate) getArguments().getSerializable(DatePickerFragment.DATE);
            date = date != null ? date : LocalDate.now();
            return new DatePickerDialog(getActivity(), this, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Intent intent = new Intent();
            intent.putExtra(DatePickerFragment.DATE, LocalDate.of(year, month, dayOfMonth));
            getTargetFragment().onActivityResult(CODE, CODE, intent);
        }

    }

}