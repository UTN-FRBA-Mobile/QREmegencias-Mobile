package com.qre.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import com.qre.R;

import java.io.File;

import static com.qre.ui.fragments.user.UserManageQRFragment.QR_FILE_NAME;

public class QREmergenciasWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_qr);

            final String qrDir = context.getDir("qrDir", Context.MODE_PRIVATE).getAbsolutePath();
            final File storedQR = new File(qrDir, QR_FILE_NAME);
            if (storedQR.exists() && storedQR.isFile()) {
                remoteViews.setImageViewBitmap(R.id.widget_image, BitmapFactory.decodeFile(qrDir + "/" + QR_FILE_NAME));
            } else {
                remoteViews.setImageViewResource(R.id.widget_image, R.drawable.logo);
            }

            final Intent intent = new Intent(context, QREmergenciasWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

    }
}
