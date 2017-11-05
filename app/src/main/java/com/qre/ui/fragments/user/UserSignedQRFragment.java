package com.qre.ui.fragments.user;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.qre.R;
import com.qre.injection.Injector;
import com.qre.services.preference.impl.UserPreferenceService;
import com.qre.ui.fragments.BaseFragment;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class UserSignedQRFragment extends BaseFragment {

    private static final String TAG = UserSignedQRFragment.class.getSimpleName();

    private static final String CHARSET_NAME = "ISO-8859-1";
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private static final Map<EncodeHintType, Object> HINTS = new ConcurrentHashMap<>(2);
    public static final String SEPARATOR = " ";

    static {
        HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        HINTS.put(EncodeHintType.CHARACTER_SET, CHARSET_NAME);
        HINTS.put(EncodeHintType.MARGIN, 0);
    }

    private static final int WIDTH = 360;
    private static final int HEIGHT = 360;

    @Inject
    UserPreferenceService userPreferenceService;

    @BindView(R.id.im_qr_view)
    ImageView imageView;

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_signed_qr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getServiceComponent().inject(this);
    }

    @OnClick(R.id.btn_signed_qr)
    public void generateQR() {
        final PrivateKey privateKey = userPreferenceService.getPrivateKey();
        final Bitmap bitmap = createBitmapQR(privateKey);
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
    }

    @NonNull
    private Bitmap createBitmapQR(PrivateKey privateKey) {
        try {
            final Signature dsa = Signature.getInstance("SHA256withECDSA");
            dsa.initSign(privateKey);
            long timestamp = System.currentTimeMillis();
            final String id = userPreferenceService.getUsername();
            dsa.update((id+timestamp).getBytes());
            final byte[] sign = dsa.sign();
            final String signature = new String(sign, CHARSET_NAME);
            final String signatureSize = String.format("%03d", signature.length());
            final String contents = signatureSize + signature + id + SEPARATOR + timestamp;
            final BitMatrix bitMatrix = new QRCodeWriter()
                    .encode(contents, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, HINTS);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bitmap.setPixel(x, y,  bitMatrix.get(x, y) ? BLACK : WHITE);
                }
            }
            return bitmap;
        } catch (Exception e) {
            throw new RuntimeException("FALLO");
        }

    }

}