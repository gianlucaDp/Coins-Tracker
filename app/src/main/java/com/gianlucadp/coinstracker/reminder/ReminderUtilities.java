package com.gianlucadp.coinstracker.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.gianlucadp.coinstracker.AppBaseActivity;
import com.gianlucadp.coinstracker.R;

import java.util.concurrent.TimeUnit;

public class ReminderUtilities {

    private static final int REMINDER_INTERVAL_DAYS = 1;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.DAYS.toSeconds(REMINDER_INTERVAL_DAYS));
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;
    private static final String REMINDER_JOB_TAG = "insert_transaction_reminder_tag";
    private static boolean sInitialized;

    private static final int TRANSACTION_REMINDER_NOTIFICATION_ID = 1138;
    private static final int TRANSACTION_REMINDER_PENDING_INTENT_ID = 3417;
    private static final String TRANSACTION_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";


    synchronized public static void scheduleChargingReminder(@NonNull final Context context) {

        if (sInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(TransactionReminder.class)
                .setTag(REMINDER_JOB_TAG)

                .setLifetime(Lifetime.FOREVER)

                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(constraintReminderJob);
        sInitialized = true;
    }


    public static void remindUserToInsertTransaction(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    TRANSACTION_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, TRANSACTION_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.bank)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                .setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(TRANSACTION_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, AppBaseActivity.class);
        return PendingIntent.getActivity(
                context,
                TRANSACTION_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, R.drawable.fui_done_check_mark);
    }

}
