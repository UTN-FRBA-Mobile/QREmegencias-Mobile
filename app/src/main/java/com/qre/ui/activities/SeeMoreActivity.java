package com.qre.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.EmergencyDataDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.utils.CryptoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SeeMoreActivity extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

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

        // FALLA AL PARASEAR LAS FECHAS
        // ERROR:  org.threeten.bp.format.DateTimeParseException: Text '2016-10-10T00:00:00' could not be parsed at index 19
        networkService.getPublicEmergencyData(getIntent().getStringExtra("uuid"), new NetCallback<EmergencyDataDTO>() {
            @Override
            public void onSuccess(EmergencyDataDTO response) {
                getIntent().putExtra("response", response.toString());
                Log.i(TAG, "JSON: " + response.toString() );
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.i(TAG, "ERROR:  " + exception );
            }
        });

        new AsyncFetch().execute();

	}

    private class AsyncFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(SeeMoreActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("\tCargando...");
            pdLoading.setCancelable(true);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(getIntent().getStringExtra("url"));

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.toString();
            }
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            Log.i(TAG, result);

            pdLoading.dismiss();

            String text = "";

            try {
                JSONObject result_json = new JSONObject(result);
                JSONObject general_json = result_json.getJSONObject("general");
                JSONArray surgeries_json = result_json.getJSONArray("surgeries");
                JSONArray hospitalizations_json = result_json.getJSONArray("hospitalizations");
                JSONArray pathologies_json = result_json.getJSONArray("pathologies");
                JSONArray medications_json = result_json.getJSONArray("medications");

                text += "- Último chequeo médico : " + result_json.get("lastMedicalCheck").toString() + "\n\n";
                text += "- Tipo sangre : " + result_json.getJSONObject("general").get("bloodType").toString() + "\n\n";
                text += "- Donante de órganos : " +
                        ((general_json.get("organDonor").toString().equals("true")) ? "Sí" : "No") + "\n\n";
                for(int i=0;i<general_json.getJSONArray("allergies").length();i++) {
                    text += "- Alergia : " + general_json.getJSONArray("allergies").get(i).toString() + "\n\n";
                }
                for(int i=0;i<surgeries_json.length();i++) {

                    JSONObject operacion_json = surgeries_json.getJSONObject(i);

                    text += "- Operacion : \n";
                    text += "\t" + "Institución : " + operacion_json.get("institution") + "\n";
                    text += "\t" + "Tipo : " + operacion_json.get("type") + "\n";
                    text += "\t" + "Fecha : " + operacion_json.get("date") + "\n";
                    text += "\t" + "Motivo : " + operacion_json.get("reason") + "\n\n";
                }

                for(int i=0;i<hospitalizations_json.length();i++) {

                    JSONObject internaciones_json = hospitalizations_json.getJSONObject(i);

                    text += "- Internación : \n";
                    text += "\t" + "Institución : " + internaciones_json.get("institution") + "\n";
                    text += "\t" + "Tipo : " + internaciones_json.get("type") + "\n";
                    text += "\t" + "Fecha : " + internaciones_json.get("date") + "\n";
                    text += "\t" + "Motivo : " + internaciones_json.get("reason") + "\n\n";
                }

                for(int i=0;i<pathologies_json.length();i++) {

                    JSONObject patologias_json = pathologies_json.getJSONObject(i);

                    text += "- Patología : \n";
                    text += "\t" + "Tipo : " + patologias_json.get("type") + "\n";
                    text += "\t" + "Descripción : " + patologias_json.get("description") + "\n";
                    text += "\t" + "Fecha : " + patologias_json.get("date") + "\n\n";
                }

                for(int i=0;i<medications_json.length();i++) {

                    JSONObject medicaciones_json = medications_json.getJSONObject(i);

                    text += "- Medicación : \n";
                    text += "\t" + "Nombre : " + medicaciones_json.get("name") + "\n";
                    text += "\t" + "Descripcion : " + medicaciones_json.get("description") + "\n";
                    text += "\t" + "Cantidad : " + medicaciones_json.get("amount") + "\n";
                    text += "\t" + "Período : " + medicaciones_json.get("period") + "\n\n";
                }
                textview_see_more.setText(text);
                textview_see_more.setMovementMethod(new ScrollingMovementMethod());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
