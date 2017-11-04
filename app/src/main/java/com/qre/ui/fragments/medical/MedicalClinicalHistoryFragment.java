package com.qre.ui.fragments.medical;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.services.preference.impl.UserPreferenceService;
import com.qre.ui.fragments.BaseFragment;
import com.qre.ui.fragments.ProfileFragment;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MedicalClinicalHistoryFragment extends BaseFragment {

    private static final String TAG = MedicalClinicalHistoryFragment.class.getSimpleName();

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd / MM / yyyy");

    private static final int CODE_DATE = 1;
    private static final int CODE_CAMERA = 2;
    private static final int CODE_GALLERY = 3;

    @Inject
    UserPreferenceService userPreferenceService;

    @Inject
    NetworkService networkService;

    @BindView(R.id.input_name)
    EditText vName;

    @BindView(R.id.input_date)
    EditText vDate;

    @BindView(R.id.input_text)
    EditText vText;

    @BindView(R.id.btn_save)
    Button vSave;

    private LocalDate date;
    private File file;

    @Override
    protected int getLayout() {
        return R.layout.fragment_medical_clinical_history;
    }

    @Override
    protected void initializeViews() {
        Injector.getServiceComponent().inject(this);
    }

    @OnClick(R.id.btn_save)
    public void save() {

        vSave.setEnabled(false);

        String name = vName.getText().toString();
        String text = vText.getText().toString();
        String user = userPreferenceService.getUsername();

        networkService.createMedicalRecord(name, text, date, user, file, new NetCallback<Map<String, String>>() {

            @Override
            public void onSuccess(Map<String, String> response) {
                vSave.setEnabled(true);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "Cannot create clinical history", e);
                vSave.setEnabled(true);
            }

        });
    }

    @OnClick(R.id.input_date)
    public void openDateDialog() {
        DialogFragment dialog = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DatePickerFragment.DATE, date);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(this, CODE_DATE);
        dialog.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @OnClick(R.id.btn_load)
    public void openPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle(getString(R.string.load_image));
        String[] pictureDialogItems = { getString(R.string.load_image_from_gallery), getString(R.string.load_image_from_camera) };
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: choosePhotoFromGallery();
                        break;
                    case 1: takePhotoFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    public File saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "qr");
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getContext(), new String[]{f.getPath()}, new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            return f;
        } catch (IOException e) {
            Log.e(TAG, "Cannot save image", e);
        }
        return null;
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, CODE_GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CODE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DATE) {
            date = (LocalDate) data.getSerializableExtra(DatePickerFragment.DATE);
            vDate.setText(date.format(DATE_FORMATTER));
        } else if (requestCode == CODE_GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    file = saveImage(bitmap);
                } catch (IOException e) {
                    Log.e(TAG, "Cannot load image from gallery", e);
                }
            }
        } else if (requestCode == CODE_CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            file = saveImage(thumbnail);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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
            getTargetFragment().onActivityResult(CODE_DATE, CODE_DATE, intent);
        }

    }

}