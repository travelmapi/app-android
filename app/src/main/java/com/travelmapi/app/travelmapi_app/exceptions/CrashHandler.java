package com.travelmapi.app.travelmapi_app.exceptions;

import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by sam on 5/19/16.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final int NOTIFICATION_TRIP = 1;
    public static final int NOTIFICATION_GPS = 2;

    private Thread.UncaughtExceptionHandler defaultUEH;
    private NotificationManager notificationManager;

    public CrashHandler(Context context)
    {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void uncaughtException(Thread t, Throwable e)
    {
        if (notificationManager != null)
        {
            try
            {
                notificationManager.cancel(NOTIFICATION_TRIP);
                notificationManager.cancel(NOTIFICATION_GPS);
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
            }
        }
        notificationManager = null;

        defaultUEH.uncaughtException(t, e);
    }
}
