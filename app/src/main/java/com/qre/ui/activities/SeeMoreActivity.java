package com.qre.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.qre.R;
import com.qre.utils.CryptoUtils;

import java.io.InputStream;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SeeMoreActivity extends AppCompatActivity {

	private static final String TAG = SeeMoreActivity.class.getSimpleName();

	public static Intent getIntent(final Context context) {
		return new Intent(context, SeeMoreActivity.class);
	}

	@BindView(R.id.webview_see_more)
	WebView webview_see_more;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_seemore);
		ButterKnife.bind(this);

		webview_see_more.loadUrl(getIntent().getStringExtra("url"));

	}

}