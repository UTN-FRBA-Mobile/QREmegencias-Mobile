package com.qre.ui.fragments.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.services.preference.impl.UserPreferenceService;
import com.qre.ui.fragments.BaseFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class UserManageQRFragment extends BaseFragment {

    private static final String TAG = UserManageQRFragment.class.getSimpleName();
    public static final String QR_FILE_NAME = "qr.png";

    @Inject
    UserPreferenceService userPreferenceService;

    @Inject
    NetworkService networkService;

    @BindView(R.id.im_qr_view)
    ImageView imageView;

    @BindView(R.id.btn_delete_qr)
    Button mButtonDelete;

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_manage_qr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getServiceComponent().inject(this);
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();
        viewQR(false);
    }

    private void viewQR(boolean forceFetch) {

        if (forceFetch) {
            fetchQR();
        } else {
            final File storedQR = new File(userPreferenceService.getQRLocation(), QR_FILE_NAME);
            if (storedQR.exists() && storedQR.length() > 0) {
                try (final FileInputStream fileInputStream = new FileInputStream(storedQR)) {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));
                    imageView.setVisibility(View.VISIBLE);
                    mButtonDelete.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    fetchQR();
                } catch (IOException e) {
                    Log.e(TAG, "Error al leer el QR guardado", e);
                }
            } else {
                fetchQR();
            }
        }

    }

    private void saveToInternalStorage(final Bitmap bitmapImage) {
        final File directory = getContext().getDir("qrDir", Context.MODE_PRIVATE);
        final File mypath = new File(directory, QR_FILE_NAME);
        try (final FileOutputStream fos = new FileOutputStream(mypath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (final Exception e) {
            Log.e(TAG, "Error al guardar QR", e);
        }
        userPreferenceService.putQRLocation(directory.getAbsolutePath());
    }

    private void fetchQR() {
        networkService.getQR(userPreferenceService.getUsername(), new NetCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                final Bitmap bm = BitmapFactory.decodeStream(response.byteStream());
                saveToInternalStorage(bm);
                imageView.setImageBitmap(bm);
                imageView.setVisibility(View.VISIBLE);
                mButtonDelete.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Throwable exception) {
                Log.e(TAG, "Error al obtener QR del paciente", exception);
                imageView.setVisibility(View.INVISIBLE);
                mButtonDelete.setVisibility(View.INVISIBLE);
            }
        });
    }

    @OnClick(R.id.btn_create_qr)
    public void createQR() {
        networkService.createQR(new NetCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                viewQR(true);
            }

            @Override
            public void onFailure(Throwable exception) {
                Toast.makeText(getContext(), "Error al crear QR", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO Borrar el archivo local no anda y borrar el archivo al cerrar session falta
    @OnClick(R.id.btn_delete_qr)
    public void deleteQR() {
        networkService.deleteQR(new NetCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                if (new File(userPreferenceService.getQRLocation()).delete()) {
                    imageView.setVisibility(View.INVISIBLE);
                    mButtonDelete.setVisibility(View.INVISIBLE);
                    userPreferenceService.putQRLocation(null);
                } else {
                    Toast.makeText(getContext(), "No se pudo borrar el QR", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                Toast.makeText(getContext(), "Error al borrar QR", Toast.LENGTH_LONG).show();
            }
        });
    }

}