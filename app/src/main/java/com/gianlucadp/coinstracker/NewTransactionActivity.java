package com.gianlucadp.coinstracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlucadp.coinstracker.model.Transaction;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.supportClasses.Constants;
import com.gianlucadp.coinstracker.supportClasses.DatabaseManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NewTransactionActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener {
private TransactionGroup mSourceTG;
private TransactionGroup mTargetTG;
private Date mCurrentDate;
private TextView mTvCurrentDate;
private EditText mEtTransactionValue;
private EditText mEtTransactionNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCurrentDate = findViewById(R.id.tv_date);
        FloatingActionButton fab = findViewById(R.id.fab_create_transaction);
        mEtTransactionValue = findViewById(R.id.et_transaction_value);
        mEtTransactionNotes = findViewById(R.id.et_transaction_notes);

        if (savedInstanceState == null) {
            mSourceTG = getIntent().getParcelableExtra(Constants.INTENT_SOURCE_GROUP);
            mTargetTG = getIntent().getParcelableExtra(Constants.INTENT_TARGET_GROUP);
            setTitle(mSourceTG.getName() + " > " + mTargetTG.getName());
        }
        mCurrentDate = new Date();

        mTvCurrentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
            ;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float value = Float.valueOf(mEtTransactionValue.getText().toString());
                String notes = mEtTransactionNotes.getText().toString();
                Log.d("AAA", "sono uguali?: " + String.valueOf( mTargetTG.getType().equals(mSourceTG.getType())));
                boolean isExpense = mTargetTG.getType().equals(TransactionGroup.GroupType.EXPENSE) ||  mTargetTG.getType().equals(mSourceTG.getType());
                long time = getDateInMillis(mCurrentDate);
                time -= (time%(TimeUnit.DAYS.toSeconds(1)));
                Transaction transaction = new Transaction(mSourceTG.getFirebaseId(), mTargetTG.getFirebaseId(), value,time,notes,isExpense);
                DatabaseManager.addTransaction(transaction);
                finish();
            }
        });

        ImageView pickDate = findViewById(R.id.iv_set_date);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }

            ;
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSelected(Date date) {
        mCurrentDate = date;
        applyDate(date);
    }

    public void applyDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY");
        mTvCurrentDate.setText(simpleDateFormat.format(date));
    }

    public static long getDateInMillis(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
