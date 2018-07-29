package com.gianlucadp.coinstracker.reminder;

        import android.content.Context;
        import android.os.AsyncTask;
        import com.firebase.jobdispatcher.JobParameters;
        import com.firebase.jobdispatcher.JobService;

public class TransactionReminder extends JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {


        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {

                Context context = TransactionReminder.this;
                ReminderTasks.executeTask(context, ReminderTasks.INSERT_TRANSACTION_REMINDER);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                jobFinished(jobParameters, false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}