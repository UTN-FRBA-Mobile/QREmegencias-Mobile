package com.qre.ui.activities;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.qre.R;

public abstract class BaseFragmentActivity extends AppCompatActivity {

	protected abstract @IdRes
	int getFragmentContentId();

	public void setInitialFragment(@NonNull final Fragment fragment) {
		final FragmentManager fragmentManager = getSupportFragmentManager();
		final FragmentTransaction fragmentTransaction =
				fragmentManager.beginTransaction();
		fragmentTransaction.add(getFragmentContentId(), fragment).commit();
	}

	public void slideNextFragment(@NonNull final Fragment fragment) {
		final FragmentManager fragmentManager = getSupportFragmentManager();
		final FragmentTransaction fragmentTransaction =
				fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(
				R.anim.slide_in_right,
				R.anim.slide_out_left,
				R.anim.slide_in_left,
				R.anim.slide_out_right);
		fragmentTransaction.addToBackStack(fragment.getClass().getName());
		fragmentTransaction.replace(getFragmentContentId(), fragment).commit();
	}

	public void slidePreviousFragment() {
		getSupportFragmentManager().popBackStackImmediate();
	}
}
