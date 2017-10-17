package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qre.R;
import com.qre.exception.InvalidQRException;
import com.qre.models.EmergencyData;
import com.qre.ui.adapters.EnumerationAdapter;
import com.qre.ui.components.DetailValueView;
import com.qre.utils.CryptoUtils;
import com.qre.utils.QRUtils;

import java.io.InputStream;
import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmergencyDataActivity extends AppCompatActivity {

    private static final String TAG = EmergencyDataActivity.class.getSimpleName();

    public static Intent getIntent(final Context context) {
        return new Intent(context, EmergencyDataActivity.class);
    }

    @BindView(R.id.toolbar)
    Toolbar vToolbar;

    @BindView(R.id.blood_type)
    DetailValueView vBloodType;

    @BindView(R.id.age)
    DetailValueView vAge;

    @BindView(R.id.sex)
    DetailValueView vSex;

    @BindView(R.id.contact_title)
    TextView vContactTitle;

    @BindView(R.id.contact_name)
    DetailValueView vContactName;

    @BindView(R.id.contact_phone)
    DetailValueView vContactPhone;

    @BindView(R.id.allergies_title)
    TextView vAllergiesTitle;

    @BindView(R.id.allergies)
    RecyclerView vAllergies;

    @BindView(R.id.pathologies_title)
    TextView vPathologiesTitle;

    @BindView(R.id.pathologies)
    RecyclerView vPathologies;

    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencydata);
        ButterKnife.bind(this);

        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Intent intent = getIntent();
        final String result = intent.getStringExtra("result");
        final InputStream key = getResources().openRawResource(R.raw.privatekey);

        try {
            final byte[] bytes = CryptoUtils.decryptText(result, key);
            final EmergencyData data = QRUtils.parseQR(bytes);
            this.uuid = data.getUUID();

            // Basic data

            vBloodType.setValue(data.getBloodType());
            vAge.setValue(MessageFormat.format(getString(R.string.x_years), data.getAge()));
            vSex.setValue(data.getSex());

            // Allergies

            if (data.getAllergies() != null && !data.getAllergies().isEmpty()) {
                vAllergiesTitle.setVisibility(View.VISIBLE);
                vAllergies.setVisibility(View.VISIBLE);
                vAllergies.setLayoutManager(new LinearLayoutManager(this));
                vAllergies.setAdapter(new EnumerationAdapter(this, data.getAllergies()));
            } else {
                vAllergiesTitle.setVisibility(View.GONE);
                vAllergies.setVisibility(View.GONE);
            }

            // Pathologies

            if (data.getPathologies() != null && !data.getPathologies().isEmpty()) {
                vPathologiesTitle.setVisibility(View.VISIBLE);
                vPathologies.setVisibility(View.VISIBLE);
                vPathologies.setLayoutManager(new LinearLayoutManager(this));
                vPathologies.setAdapter(new EnumerationAdapter(this, data.getPathologies()));
            } else {
                vPathologiesTitle.setVisibility(View.GONE);
                vPathologies.setVisibility(View.GONE);
            }

            // Contact data

            if (data.getContactName() != null && data.getContactPhone() != null) {
                vContactTitle.setVisibility(View.VISIBLE);
                vContactName.setVisibility(View.VISIBLE);
                vContactPhone.setVisibility(View.VISIBLE);
                vContactName.setValue(data.getContactName());
                vContactPhone.setValue(data.getContactPhone());
            } else {
                vContactTitle.setVisibility(View.GONE);
                vContactName.setVisibility(View.GONE);
                vContactPhone.setVisibility(View.GONE);
            }

        } catch (final InvalidQRException exception) {
            Log.e(TAG, "QR Invalido", exception);
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        } catch (final Exception e) {
            Log.e(TAG, "Cannot read QR", e);
        }
    }

    @OnClick(R.id.btn_see_more)
    public void seeMore() {
        final Intent intent = SeeMoreActivity.getIntent(this);
        intent.putExtra("uuid", this.uuid);
        startActivity(intent);
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