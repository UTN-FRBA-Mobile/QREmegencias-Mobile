package com.qre.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

public final class ResourceUtils {

	private ResourceUtils() {
		throw new IllegalAccessError("This is an utility class");
	}

	public static @ColorInt int getColor(@ColorRes final int colorRes, final Context context) {
		final int color;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			color = context.getResources().getColor(colorRes, context.getTheme());
		} else {
			color = context.getResources().getColor(colorRes);
		}
		return color;
	}

	public static Drawable getDrawable(@DrawableRes final int drawableRes, final Context context) {
		final Drawable drawable;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			drawable = context.getResources().getDrawable(drawableRes, context.getTheme());
		} else {
			drawable = context.getResources().getDrawable(drawableRes);
		}
		return drawable;
	}

}