package com.qre.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qre.R;
import com.qre.models.FileDTO;
import com.qre.models.MedicalRecordDTO;
import com.squareup.picasso.Picasso;

import org.threeten.bp.LocalDate;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<MedicalRecordDTO> items = Collections.emptyList();

    public MedicalRecordAdapter(Context context, List<MedicalRecordDTO> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
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
        LocalDate performed = value.getPerformed();
        holder.date.setText(performed.toString());

        if (!value.getFiles().isEmpty()) {
            final FileDTO file = value.getFiles().get(0);

            if (file.getMimeType().contains("image")) {
                Picasso.with(holder.image.getContext())
                        .load(file.getUrl())
                        .into(holder.image);
            } else {
                // TODO mostrar boton de descarga
            }

        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_text)
        public TextView text;

        @BindView(R.id.card_title)
        public TextView title;

        @BindView(R.id.card_date)
        public TextView date;

        @BindView(R.id.card_image)
        public ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}