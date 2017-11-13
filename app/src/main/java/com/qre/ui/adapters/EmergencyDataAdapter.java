package com.qre.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qre.R;
import com.qre.models.HospitalizationDTO;
import com.qre.models.MedicationDTO;
import com.qre.models.PathologyDTO;
import com.qre.models.UserContactDTO;
import com.qre.ui.components.DetailValueView;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmergencyDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd / MM / yyyy");

    public final static int TYPE_HEADER = 1;
    public final static int TYPE_ALLERGY = 2;
    public final static int TYPE_HOSPITALIZATION = 3;
    public final static int TYPE_MEDICATION = 4;
    public final static int TYPE_PATHOLOGY = 5;
    public final static int TYPE_CONTACT = 6;
    public final static int TYPE_SURGERY = 7;

    private Context context;
    private Listener listener;
    private LayoutInflater inflater;
    private boolean editable = false;
    private List<?> items = Collections.emptyList();

    public EmergencyDataAdapter(Context context, List<?> items) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    public EmergencyDataAdapter(Context context, List<?> items, boolean editable) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.editable = editable;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_header, parent, false));
            case TYPE_ALLERGY:
                return new AllergyViewHolder(inflater.inflate(R.layout.item_allergy, parent, false));
            case TYPE_HOSPITALIZATION:
                return new HospitalizationViewHolder(inflater.inflate(R.layout.item_hospitalization, parent, false));
            case TYPE_SURGERY:
                return new HospitalizationViewHolder(inflater.inflate(R.layout.item_hospitalization, parent, false));
            case TYPE_MEDICATION:
                return new MedicationViewHolder(inflater.inflate(R.layout.item_medication, parent, false));
            case TYPE_PATHOLOGY:
                return new PathologyViewHolder(inflater.inflate(R.layout.item_pathology, parent, false));
            case TYPE_CONTACT:
                return new ContactViewHolder(inflater.inflate(R.layout.item_contact, parent, false));
        }
        throw new IllegalArgumentException("Invalid view type " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                final int type = (Integer) items.get(position);
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.add.setVisibility(editable ? View.VISIBLE : View.GONE);
                headerViewHolder.value.setText(context.getString(getHeaderTitle(type)));
                headerViewHolder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onAddItem(type);
                    }
                });
                break;
            case TYPE_ALLERGY:
                final String allergy = (String) items.get(position);
                AllergyViewHolder allergyViewHolder = (AllergyViewHolder) holder;
                allergyViewHolder.value.setText(allergy);
                if (editable) {
                    allergyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onEditItem(TYPE_ALLERGY, allergy);
                        }
                    });
                }
                allergyViewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRemoveItem(TYPE_ALLERGY, allergy);
                    }
                });
                break;
            case TYPE_HOSPITALIZATION:
                final HospitalizationDTO hospitalizationDTO = (HospitalizationDTO) items.get(position);
                HospitalizationViewHolder hospitalizationViewHolder = (HospitalizationViewHolder) holder;
                hospitalizationViewHolder.actions.setVisibility(editable ? View.VISIBLE : View.GONE);
                hospitalizationViewHolder.institution.setValue(hospitalizationDTO.getInstitution());
                hospitalizationViewHolder.type = hospitalizationDTO.getType();
                if (hospitalizationDTO.getDate() != null)
                    hospitalizationViewHolder.date.setValue(hospitalizationDTO.getDate().format(DATE_FORMATTER));
                hospitalizationViewHolder.reason.setValue(hospitalizationDTO.getReason());
                hospitalizationViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onEditItem(TYPE_HOSPITALIZATION, hospitalizationDTO);
                    }
                });
                hospitalizationViewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRemoveItem(TYPE_HOSPITALIZATION, hospitalizationDTO);
                    }
                });
                break;
            case TYPE_SURGERY:
                final HospitalizationDTO surgery = (HospitalizationDTO) items.get(position);
                HospitalizationViewHolder surgeryViewHolder = (HospitalizationViewHolder) holder;
                surgeryViewHolder.actions.setVisibility(editable ? View.VISIBLE : View.GONE);
                surgeryViewHolder.institution.setValue(surgery.getInstitution());
                surgeryViewHolder.type = surgery.getType();
                if (surgery.getDate() != null)
                    surgeryViewHolder.date.setValue(surgery.getDate().format(DATE_FORMATTER));
                surgeryViewHolder.reason.setValue(surgery.getReason());
                surgeryViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onEditItem(TYPE_SURGERY, surgery);
                    }
                });
                surgeryViewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRemoveItem(TYPE_SURGERY, surgery);
                    }
                });
                break;
            case TYPE_MEDICATION:
                final MedicationDTO medicationDTO = (MedicationDTO) items.get(position);
                MedicationViewHolder medicationViewHolder = (MedicationViewHolder) holder;
                medicationViewHolder.actions.setVisibility(editable ? View.VISIBLE : View.GONE);
                medicationViewHolder.name.setValue(medicationDTO.getName());
                medicationViewHolder.description.setValue(medicationDTO.getDescription());
                medicationViewHolder.amount.setValue(String.valueOf(medicationDTO.getAmount()));
                medicationViewHolder.period.setValue(String.valueOf(medicationDTO.getPeriod()));
                medicationViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onEditItem(TYPE_MEDICATION, medicationDTO);
                    }
                });
                medicationViewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRemoveItem(TYPE_MEDICATION, medicationDTO);
                    }
                });
                break;
            case TYPE_PATHOLOGY:
                final PathologyDTO pathologyDTO = (PathologyDTO) items.get(position);
                PathologyViewHolder pathologyViewHolder = (PathologyViewHolder) holder;
                pathologyViewHolder.actions.setVisibility(editable ? View.VISIBLE : View.GONE);
                pathologyViewHolder.type.setValue(String.valueOf(pathologyDTO.getType()));
                pathologyViewHolder.description.setValue(pathologyDTO.getDescription());
                if (pathologyDTO.getDate() != null)
                    pathologyViewHolder.date.setValue(pathologyDTO.getDate().format(DATE_FORMATTER));
                pathologyViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onEditItem(TYPE_PATHOLOGY, pathologyDTO);
                    }
                });
                pathologyViewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRemoveItem(TYPE_PATHOLOGY, pathologyDTO);
                    }
                });
                break;
            case TYPE_CONTACT:
                final UserContactDTO userContactDTO = (UserContactDTO) items.get(position);
                ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
                contactViewHolder.actions.setVisibility(editable ? View.VISIBLE : View.GONE);
                contactViewHolder.name.setValue(userContactDTO.getFirstName());
                contactViewHolder.surname.setValue(userContactDTO.getLastName());
                contactViewHolder.phone.setValue(userContactDTO.getPhoneNumber());
                contactViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onEditItem(TYPE_CONTACT, userContactDTO);
                    }
                });
                contactViewHolder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onRemoveItem(TYPE_CONTACT, userContactDTO);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String) {
            return TYPE_ALLERGY;
        }
        if (item instanceof HospitalizationDTO) {
            switch (((HospitalizationDTO) item).getType()) {
                case CIRUGIA:
                    return TYPE_SURGERY;
                case ADMISION:
                    return TYPE_HOSPITALIZATION;
            }
        }
        if (item instanceof MedicationDTO) {
            return TYPE_MEDICATION;
        }
        if (item instanceof PathologyDTO) {
            return TYPE_PATHOLOGY;
        }
        if (item instanceof UserContactDTO) {
            return TYPE_CONTACT;
        }
        if (item instanceof Integer) {
            return TYPE_HEADER;
        }
        throw new IllegalStateException("Cannot determine view holder type for " + item);
    }

    public int getHeaderTitle(int viewType) {
        switch (viewType) {
            case TYPE_SURGERY:
                return R.string.surgeries;
            case TYPE_ALLERGY:
                return R.string.allergies;
            case TYPE_HOSPITALIZATION:
                return R.string.hospitalizations;
            case TYPE_MEDICATION:
                return R.string.medications;
            case TYPE_PATHOLOGY:
                return R.string.pathologies;
            case TYPE_CONTACT:
                return R.string.contacts;
        }
        throw new IllegalArgumentException("Invalid view type " + viewType);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.value)
        public TextView value;

        @BindView(R.id.btn_add)
        public ImageButton add;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public class AllergyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.value)
        public TextView value;

        @BindView(R.id.btn_delete)
        public ImageButton remove;

        public AllergyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public class HospitalizationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.institution)
        public DetailValueView institution;

        public HospitalizationDTO.TypeEnum type;

        @BindView(R.id.date)
        public DetailValueView date;

        @BindView(R.id.reason)
        public DetailValueView reason;

        @BindView(R.id.btn_edit)
        public Button edit;

        @BindView(R.id.btn_delete)
        public Button remove;

        @BindView(R.id.actions)
        public View actions;

        public HospitalizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public class MedicationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        public DetailValueView name;

        @BindView(R.id.description)
        public DetailValueView description;

        @BindView(R.id.amount)
        public DetailValueView amount;

        @BindView(R.id.period)
        public DetailValueView period;

        @BindView(R.id.btn_edit)
        public Button edit;

        @BindView(R.id.btn_delete)
        public Button remove;

        @BindView(R.id.actions)
        public View actions;

        public MedicationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public class PathologyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.type)
        public DetailValueView type;

        @BindView(R.id.description)
        public DetailValueView description;

        @BindView(R.id.date)
        public DetailValueView date;

        @BindView(R.id.btn_edit)
        public Button edit;

        @BindView(R.id.btn_delete)
        public Button remove;

        @BindView(R.id.actions)
        public View actions;

        public PathologyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.contact_name)
        public DetailValueView name;

        @BindView(R.id.contact_surname)
        public DetailValueView surname;

        @BindView(R.id.contact_phone)
        public DetailValueView phone;

        @BindView(R.id.btn_edit)
        public Button edit;

        @BindView(R.id.btn_delete)
        public Button remove;

        @BindView(R.id.actions)
        public View actions;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface Listener {

        void onAddItem(int type);

        void onEditItem(int type, Object item);

        void onRemoveItem(int type, Object item);

    }

}