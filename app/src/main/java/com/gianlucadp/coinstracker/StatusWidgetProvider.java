package com.gianlucadp.coinstracker;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.gianlucadp.coinstracker.supportClasses.Constants;


public class StatusWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //Get the stored ingredients
        SharedPreferences prefs = context.getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.status_widget);
        //context.getString(R.string.no_recipe_selected)
        float expenses = prefs.getFloat(Constants.EXPENSES_SHARED_PREF_KEY,0);
        float  revenues = prefs.getFloat(Constants.REVENUE_SHARED_PREF_KEY,0);

        views.setTextViewText(R.id.appwidget_expenses, String.valueOf(expenses));
        views.setTextViewText(R.id.appwidget_revenues, String.valueOf(revenues));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Nothing to do
    }

    @Override
    public void onDisabled(Context context) {
        // Nothing to do
    }
}
