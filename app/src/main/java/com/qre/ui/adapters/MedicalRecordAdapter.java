package com.qre.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.qre.R;
import com.qre.models.FileDTO;
import com.qre.models.MedicalRecordDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.services.preference.impl.UserPreferenceService;
import com.squareup.picasso.Picasso;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;

import static com.qre.utils.Constants.ROLE_MEDICAL;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd / MM / yyyy");

    private final NetworkService networkService;
    private final LayoutInflater inflater;
    private List<MedicalRecordDTO> items = Collections.emptyList();
    private final OkHttpClient okHttpClient;
    private final UserPreferenceService userPreferenceService;
    private Context context;

    public MedicalRecordAdapter(Context context, List<MedicalRecordDTO> items, OkHttpClient okHttpClient,
                                UserPreferenceService userPreferenceService, NetworkService networkService) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.okHttpClient = okHttpClient;
        this.userPreferenceService = userPreferenceService;
        this.networkService = networkService;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MedicalRecordDTO value = items.get(position);
        holder.text.setText(value.getText());
        holder.title.setText(value.getName());
        holder.id = value.getId();
        holder.date.setText(value.getPerformed().format(DATE_FORMATTER));
        if (userPreferenceService.getRole().equals(ROLE_MEDICAL)) {
            holder.deleteButton.setVisibility(View.VISIBLE);
        }

        if (!value.getFiles().isEmpty()) {
            final FileDTO file = value.getFiles().get(0);
            if (file.getMimeType().contains("image")) {
                holder.imageUrl = file.getUrl().contains("/api/medicalRecord") ?
                        file.getUrl().replace("/api/medicalRecord", "/api/mobile/medicalRecord") :
                        file.getUrl();
            }
            holder.viewButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        String imageUrl;
        String id;

        @BindView(R.id.card_text)
        public TextView text;

        @BindView(R.id.card_title)
        public TextView title;

        @BindView(R.id.card_date)
        public TextView date;

        @BindView(R.id.card_image)
        public ImageView image;

        @BindView(R.id.close_image_button)
        public ImageButton closeButton;

        @BindView(R.id.view_image_button)
        public ImageButton viewButton;

        @BindView(R.id.button_delete_medical_record)
        public Button deleteButton;

        @OnClick(R.id.view_image_button)
        public void viewButtonClicked() {
            if (imageUrl != null) {

                if (image.getDrawable() == null) {
                    new Picasso
                            .Builder(image.getContext())
                            .downloader(new OkHttp3Downloader(okHttpClient))
                            .build()
                            .load(imageUrl)
                            .into(image);
                }
                image.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.VISIBLE);
                viewButton.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.close_image_button)
        public void closeButtonClicked() {
            if (imageUrl != null) {
                image.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
                viewButton.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.button_delete_medical_record)
        public void deleteButtonClicked() {

            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Borrar Historia");
            alert.setMessage("¿Está seguro que desea borrar este estudio?");
            alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {

                    networkService.deleteMedicalRecord(id, new NetCallback<Void>() {
                        @Override
                        public void onSuccess(Void response) {
                            dialog.dismiss();
                            items.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                        }

                        @Override
                        public void onFailure(Throwable exception) {
                            Toast.makeText(deleteButton.getContext(), "", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}