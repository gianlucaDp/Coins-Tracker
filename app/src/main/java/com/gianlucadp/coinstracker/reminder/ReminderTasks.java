package com.gianlucadp.coinstracker.reminder;

import android.content.Context;

public class ReminderTasks {


    static final String INSERT_TRANSACTION_REMINDER = "insert-transaction-reminder";

    public static void executeTask(Context context, String action) {
        if (INSERT_TRANSACTION_REMINDER.equals(action)) {
            issueChargingReminder(context);
        }
    }

    private static void issueChargingReminder(Context context) {
        ReminderUtilities.remindUserBecauseCharging(context);
    }
}