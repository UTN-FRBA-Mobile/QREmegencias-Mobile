package com.qre.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qre.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailValueView extends LinearLayout {

    @BindView(R.id.icon)
    ImageView vIcon;

    @BindView(R.id.title)
    TextView vTitle;

    @BindView(R.id.value)
    TextView vValue;

    public DetailValueView(Context context) {
        super(context);
        initialize();
    }

    public DetailValueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public DetailValueView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize() {
        initialize(null);
    }

    private void initialize(AttributeSet attrs) {
        inflate(getContext(), R.layout.component_detail_value, this);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DetailValueView, 0, 0);
        ButterKnife.bind(this);
        try {
            vIcon.setImageDrawable(a.getDrawable(R.styleable.DetailValueView_icon));
            vTitle.setText(a.getString(R.styleable.DetailValueView_title));
            vValue.setText(a.getString(R.styleable.DetailValueView_value));
        } finally {
            a.recycle();
        }
    }

    public void setValue(String value) {
        this.vValue.setText(value);
    }

}