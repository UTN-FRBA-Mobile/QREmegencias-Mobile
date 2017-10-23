package com.qre.ui.fragments.user;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.services.preference.impl.UserPreferenceService;
import com.qre.ui.fragments.BaseFragment;
import com.qre.utils.CryptoUtils;

import java.security.KeyPair;
import java.security.Signature;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class UserManageQRFragment extends BaseFragment {

    private static final String CHARSET_NAME = "ISO-8859-1";
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    @Inject
    UserPreferenceService userPreferenceService;

    @Inject
    NetworkService networkService;

    @BindView(R.id.im_qr_view)
    ImageView imageView;

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_manage_qr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getServiceComponent().inject(this);
    }

    @OnClick(R.id.btn_signed_qr)
    public void generateQR() {

        final KeyPair keyPair = CryptoUtils.generateKeyPair();
        networkService.uploadPublicKey(keyPair.getPublic().getEncoded(), new NetCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                final Bitmap bitmap = createBitmapQR(keyPair);
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(Throwable exception) {
                Toast.makeText(getContext(), "Error al subir PK", Toast.LENGTH_LONG).show();
            }
        });

    }

    @NonNull
    private Bitmap createBitmapQR(KeyPair keyPair) {
        try {
            final int WIDTH = 360;
            final int HEIGHT = 360;
            final Map<EncodeHintType, Object> hints = new ConcurrentHashMap<>(2);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.CHARACTER_SET, CHARSET_NAME);
            hints.put(EncodeHintType.MARGIN, 0);

            final Signature dsa = Signature.getInstance("SHA256withRSA");
            dsa.initSign(keyPair.getPrivate());
            final String id = userPreferenceService.getUsername();
            dsa.update(id.getBytes());
            final byte[] sign = dsa.sign();
            final String CHARSET_NAME = "ISO-8859-1";
            final String signature = new String(sign, CHARSET_NAME);

            final BitMatrix bitMatrix = new QRCodeWriter()
                    .encode(signature + System.currentTimeMillis() + " " + id,
                            BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
                }
            }

            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            throw new RuntimeException("FALLO");
        }

    }

}