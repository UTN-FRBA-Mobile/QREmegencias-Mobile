package com.qre.ui.fragments.medical;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.networking.NetCallback;
import com.qre.ui.fragments.BaseFragment;
import com.qre.ui.fragments.ProfileFragment;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.OnClick;

public class MedicalClinicalHistoryFragment extends BaseFragment {

    private static final String TAG = MedicalClinicalHistoryFragment.class.getSimpleName();

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd / MM / yyyy");

    @BindView(R.id.input_name)
    EditText vName;

    @BindView(R.id.input_date)
    EditText vDate;

    @BindView(R.id.input_text)
    EditText vText;

    @BindView(R.id.btn_save)
    Button vSave;

    private LocalDate date;

    @Override
    protected int getLayout() {
        return R.layout.fragment_medical_clinical_history;
    }

    @Override
    protected void initializeViews() {
        //Injector.getServiceComponent().inject(this);
    }

    @OnClick(R.id.btn_save)
    public void save() {
        vSave.setEnabled(false);
    }

    @OnClick(R.id.input_date)
    public void openDateDialog() {
        DialogFragment dialog = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DatePickerFragment.DATE, date);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, DatePickerFragment.CODE);
        dialog.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DatePickerFragment.CODE) {
            date = (LocalDate) data.getSerializableExtra(DatePickerFragment.DATE);
            vDate.setText(date.format(DATE_FORMATTER));
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