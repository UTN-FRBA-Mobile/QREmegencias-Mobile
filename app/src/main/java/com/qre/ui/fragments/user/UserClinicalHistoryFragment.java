package com.qre.ui.fragments.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.MedicalRecordDTO;
import com.qre.models.PageOfMedicalRecordDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.services.preference.impl.UserPreferenceService;
import com.qre.ui.adapters.MedicalRecordAdapter;
import com.qre.ui.fragments.BaseFragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import okhttp3.OkHttpClient;

public class UserClinicalHistoryFragment extends BaseFragment {

    @BindView(R.id.records_list)
    RecyclerView vRecordsList;

    @Inject
    NetworkService networkService;

    @Inject
    UserPreferenceService userPreferenceService;

    @Inject
    @Named("withOAuth")
    OkHttpClient okHttpClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getServiceComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_clinical_history;
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();

        networkService.getSelfMedicalRecords(new NetCallback<PageOfMedicalRecordDTO>() {

            @Override
            public void onSuccess(PageOfMedicalRecordDTO response) {
                vRecordsList.setLayoutManager(new LinearLayoutManager(getContext()));
                final List<MedicalRecordDTO> content = response.getContent();
                if (content != null) {
                    Collections.sort(content, new Comparator<MedicalRecordDTO>() {
                        @Override
                        public int compare(MedicalRecordDTO o1, MedicalRecordDTO o2) {
                            if (o1.getPerformed() == null) return 1;
                            if (o2.getPerformed() == null) return -1;
                            return o2.getPerformed().compareTo(o1.getPerformed());
                        }
                    });
                }
                vRecordsList.setAdapter(new MedicalRecordAdapter(getContext(), content, okHttpClient, userPreferenceService, networkService));
                vRecordsList.setHasFixedSize(true);
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.e(getClass().getSimpleName(), "Error", exception);
            }
        });

    }
}