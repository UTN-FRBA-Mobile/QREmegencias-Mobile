package com.qre.ui.fragments.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.qre.R;
import com.qre.injection.Injector;
import com.qre.models.PageOfMedicalRecordDTO;
import com.qre.services.networking.NetCallback;
import com.qre.services.networking.NetworkService;
import com.qre.ui.adapters.MedicalRecordAdapter;
import com.qre.ui.fragments.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import okhttp3.OkHttpClient;

public class UserClinicalHistoryFragment extends BaseFragment {

    @BindView(R.id.records_list)
    RecyclerView vRecordsList;

    @Inject
    NetworkService networkService;

    @Inject
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

        networkService.getSelfMedicalRecords(new NetCallback<PageOfMedicalRecordDTO>(){

            @Override
            public void onSuccess(PageOfMedicalRecordDTO response) {
                vRecordsList.setLayoutManager(new LinearLayoutManager(getContext()));
                vRecordsList.setAdapter(new MedicalRecordAdapter(getContext(), response.getContent(), okHttpClient));
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(vRecordsList.getContext(), DividerItemDecoration.VERTICAL);
                vRecordsList.addItemDecoration(dividerItemDecoration);
                vRecordsList.setHasFixedSize(true);
            }

            @Override
            public void onFailure(Throwable exception) {
                Log.e(getClass().getSimpleName(), "Error", exception);
            }
        });

    }
}