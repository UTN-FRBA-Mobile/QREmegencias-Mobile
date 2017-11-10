package com.qre.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.qre.R;
import com.qre.models.FileDTO;
import com.qre.models.MedicalRecordDTO;
import com.squareup.picasso.Picasso;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<MedicalRecordDTO> items = Collections.emptyList();
    private OkHttpClient okHttpClient;
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd / MM / yyyy");

    public MedicalRecordAdapter(Context context, List<MedicalRecordDTO> items, OkHttpClient okHttpClient) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MedicalRecordDTO value = items.get(position);
        holder.text.setText(value.getText());
        holder.title.setText(value.getName());
        holder.date.setText(value.getPerformed().format(DATE_FORMATTER));

        if (!value.getFiles().isEmpty()) {
            final FileDTO file = value.getFiles().get(0);

            if (file.getMimeType().contains("image")) {
                String url = file.getUrl();
                if (url.contains("/api/medicalRecord")) {
                    url = url.replace("/api/medicalRecord", "/api/mobile/medicalRecord");
                }

                new Picasso
                        .Builder(holder.image.getContext())
                        .downloader(new OkHttp3Downloader(okHttpClient))
                        .build()
                        .load(url)
                        .into(holder.image);
                holder.hasImage = true;
                holder.closeButton.setVisibility(View.VISIBLE);
            } else {
                holder.image.setVisibility(View.GONE);
                // TODO mostrar boton de descarga
            }

        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public boolean hasImage;

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

        @OnClick(R.id.view_image_button)
        public void viewButtonClicked() {
            if (hasImage) {
                image.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.VISIBLE);
                viewButton.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.close_image_button)
        public void closeButtonClicked() {
            if (hasImage) {
                image.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
                viewButton.setVisibility(View.VISIBLE);
            }
        }

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}