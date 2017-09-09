package com.qre.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qre.ui.activities.BaseFragmentActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {

	private Unbinder unbinder;

	protected abstract @LayoutRes int getLayout();

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
		return inflater.inflate(getLayout(), container, false);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		unbinder = ButterKnife.bind(this, view);
		initializeViews();
	}

	protected void initializeViews() {
		//No need to implement
	}

	@Override
	public void onDestroy() {
		if (unbinder != null) {
			unbinder.unbind();
		}
		super.onDestroy();
	}

	protected void slideNextFragment(@NonNull final Activity activity, @NonNull final Fragment fragment) {
		if (activity instanceof BaseFragmentActivity) {
			final BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) activity;
			baseFragmentActivity.slideNextFragment(fragment);
		}
	}

	protected void slidePreviousFragment(@NonNull final Activity activity) {
		if (activity instanceof BaseFragmentActivity) {
			final BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) activity;
			baseFragmentActivity.slidePreviousFragment();
		}
	}
}
