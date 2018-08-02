package com.gianlucadp.coinstracker;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gianlucadp.coinstracker.model.Transaction;
import com.gianlucadp.coinstracker.supportClasses.IconsManager;
import com.google.android.gms.ads.MobileAds;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StatisticsFragment extends Fragment {
    private static final String ARG_PARAM1 = "transactions";

    private GraphView mGraphView;
    private TextView mMinIncome;
    private TextView mMaxIncome;
    private TextView mMeanIncome;

    private TextView mMinExpense;
    private TextView mMaxExpense;
    private TextView mMeanExpense;

    private ProgressBar mProgressBar;

    private ImageView mImageViewNoStatistics;
    private TextView mTextViewNoStatistics;

    private ArrayList<Transaction> mTransactions;

    public StatisticsFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment StatisticsFragment.
     */
    public static StatisticsFragment newInstance(ArrayList<Transaction> transactions) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, transactions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("AAA", "Start of on view created");
        mGraphView = view.findViewById(R.id.graph);
        mMinIncome = view.findViewById(R.id.tv_min_income);
        mMaxIncome = view.findViewById(R.id.tv_max_income);
        mMeanIncome = view.findViewById(R.id.tv_mean_income);

        mMinExpense = view.findViewById(R.id.tv_min_expense);
        mMaxExpense = view.findViewById(R.id.tv_max_expense);
        mMeanExpense = view.findViewById(R.id.tv_mean_expense);

        mProgressBar = view.findViewById(R.id.pb_loading);
        mImageViewNoStatistics = view.findViewById(R.id.im_no_statistic);
        mTextViewNoStatistics = view.findViewById(R.id.tv_no_statistics);
        new GenerateData().execute();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.BUILD_VARIANT.equals("free")) {
            MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544~3347511713");
        }
        if (getArguments() != null) {
            mTransactions = getArguments().getParcelableArrayList(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }


public static  <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
    List<T> list = new ArrayList<T>(c);
    java.util.Collections.sort(list);
    return list;
}

    private class GenerateData extends AsyncTask<Void, Void,List<DataPoint[]>> {

        private Map<Long,Float> mExpensesByDay = new HashMap<>();
        private Map<Long,Float> mRevenuesByDay = new HashMap<>();
        private Map<Long,Float> mNetByDay = new HashMap<>();

        private float mMinDate = Float.MAX_VALUE;
        private float mMaxDate = 0;

        private float minValue = 0;
        private float maxValue = 1000;

        private float mMinIn=Float.MAX_VALUE;
        private float mMinOut=Float.MAX_VALUE;

        private float mMaxIn=0;
        private float mMaxOut=0;

        private float mMeanIn=0;
        private float mMeanOut=0;

        private int mNumberOfExpenses = 0;
        private int mNumberOfRevenues = 0;

        @Override
        protected  List<DataPoint[]> doInBackground(Void... params) {

            ArrayList<Transaction> transactions =  mTransactions;
            for (Transaction transaction: transactions) {
                long dateValue = transaction.getTimestamp();

                if (dateValue > mMaxDate) {
                    mMaxDate = dateValue;
                }
                if (dateValue < mMinDate) {
                    mMinDate = dateValue;
                }

                float value = transaction.getValue();

                if (transaction.isExpense()) {
                    mNumberOfExpenses+=1;
                    if (!mExpensesByDay.containsKey(dateValue)) {
                        mExpensesByDay.put(dateValue, value);
                    } else {

                        mExpensesByDay.put(dateValue, mExpensesByDay.get(dateValue) + value);
                    }
                    value *= -1;
                } else {
                    mNumberOfRevenues+=1;
                    if (!mRevenuesByDay.containsKey(dateValue)) {
                        mRevenuesByDay.put(dateValue, value);
                    } else {
                        mRevenuesByDay.put(dateValue, mRevenuesByDay.get(dateValue) + value);
                    }
                }

                if (!mNetByDay.containsKey(dateValue)) {
                    mNetByDay.put(dateValue, value);
                } else {
                    mNetByDay.put(dateValue, mNetByDay.get(dateValue) + value);
                }
            }


            DataPoint[] inValues = new DataPoint[mNetByDay.size()];
            DataPoint[] outValues = new DataPoint[mNetByDay.size()];
            DataPoint[] netValues = new DataPoint[mNetByDay.size()];

            int inCounter = 0;
            int outCounter = 0;
            int meanCounter = 0;


            for (Long date: asSortedList(mNetByDay.keySet())){

                if (mRevenuesByDay.containsKey(date)) {
                    float inValue = mRevenuesByDay.get(date);
                    float cumulativeInValue;
                    if (inCounter>0){
                        cumulativeInValue = inValue + (float) inValues[inCounter-1].getY();
                    }else{
                        cumulativeInValue = inValue;
                    }
                    inValues[inCounter] = new DataPoint(new Date(date), cumulativeInValue);
                    inCounter++;

                    if (inValue<mMinIn){
                        mMinIn = inValue;
                    }

                    if (inValue>mMaxIn){
                        mMaxIn = inValue;
                    }

                    mMeanIn += inValue;


                }else if (inCounter>0){

                    inValues[inCounter] = new DataPoint(new Date(date), inValues[inCounter-1].getY());
                    inCounter++;
                }else{
                    inValues[inCounter] = new DataPoint(new Date(date), 0);
                    inCounter++;
                }

                if (mExpensesByDay.containsKey(date)) {
                    float outValue = mExpensesByDay.get(date);
                    float cumulativeOutValue;
                    if (outCounter>0){
                        cumulativeOutValue = outValue + (float) outValues[outCounter-1].getY();
                    }else{
                        cumulativeOutValue = outValue;
                    }

                    outValues[outCounter] = new DataPoint(new Date(date), cumulativeOutValue);
                    outCounter++;

                    if (outValue<mMinOut){
                        mMinOut = outValue;
                    }

                    if (outValue>mMaxOut){
                        mMaxOut = outValue;
                    }

                    mMeanOut += outValue;

                }else if (outCounter>0){
                    outValues[outCounter] = new DataPoint(new Date(date), outValues[outCounter-1].getY());
                    outCounter++;
            }else{
                    outValues[outCounter] = new DataPoint(new Date(date), 0);
                    outCounter++;
                }


                float netValue = mNetByDay.get(date);
                float cumulativeNetValue;
                if (meanCounter>0){
                    cumulativeNetValue = netValue + (float) netValues[meanCounter-1].getY();
                }else{
                    cumulativeNetValue = netValue;
                }
                netValues[meanCounter] = new DataPoint(new Date(date),cumulativeNetValue);
                meanCounter++;
            }

            if (inCounter>0 && mNumberOfRevenues>0){
                mMeanIn = mMeanIn / mNumberOfRevenues;
            }
            if (outCounter>0 && mNumberOfExpenses>0){
                mMeanOut = mMeanOut / mNumberOfExpenses;
            }



            List<DataPoint[]> points = new ArrayList<>();
            points.add(inValues);
            points.add(outValues);
            points.add(netValues);

            return points;
        }

        @Override
        protected void onPostExecute( List<DataPoint[]> points) {

            LineGraphSeries<DataPoint> inSeries = new LineGraphSeries<>(points.get(0));
            inSeries.setColor(Color.GREEN);
            LineGraphSeries<DataPoint> outSeries = new LineGraphSeries<>(points.get(1));
            outSeries.setColor(Color.RED);
            LineGraphSeries<DataPoint> netSeries = new LineGraphSeries<>(points.get(2));
            netSeries.setColor(Color.BLUE);
            mGraphView.addSeries(inSeries);
            mGraphView.addSeries(outSeries);
            mGraphView.addSeries(netSeries);

            int maxXElements = 4;
            if (mNetByDay.keySet().size()<4){
                maxXElements = mNetByDay.keySet().size();
            }
            mGraphView.getGridLabelRenderer().setNumHorizontalLabels(maxXElements); // only 4 because of the space
            mGraphView.getGridLabelRenderer().setNumVerticalLabels(5);
            // set manual x bounds to have nice steps
            if (mMinDate<mMaxDate) {
                mGraphView.getViewport().setMinX(mMinDate);
                mGraphView.getViewport().setMaxX(mMaxDate);

                mGraphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                mGraphView.getViewport().setXAxisBoundsManual(true);
                mGraphView.getViewport().setYAxisBoundsManual(false);

                // as we use dates as labels, the human rounding to nice readable numbers
                // is not necessary
                mGraphView.getGridLabelRenderer().setHumanRounding(false);
                mGraphView.setVisibility(View.VISIBLE);




            }else{
                //Else no points are available, load sad face
                Drawable noDataDrawable = IconsManager.createNewIcon(getContext(), CommunityMaterial.Icon.cmd_emoticon_sad, Color.LTGRAY,160);
                mImageViewNoStatistics.setImageDrawable(noDataDrawable);
                mImageViewNoStatistics.setVisibility(View.VISIBLE);
                mTextViewNoStatistics.setVisibility(View.VISIBLE);

            }
            mProgressBar.setVisibility(View.GONE);

            if(mMinOut==Float.MAX_VALUE){
                mMinOut = 0;
            };
            if(mMinIn==Float.MAX_VALUE){
                mMinIn = 0;
            };


            mMinIncome.setText(String.valueOf(mMinIn));
            mMaxIncome.setText(String.valueOf(mMaxIn));
            mMeanIncome.setText(new DecimalFormat("##.##").format(mMeanIn));

            mMinExpense.setText(String.valueOf(mMinOut));
            mMaxExpense.setText(String.valueOf(mMaxOut));
            mMeanExpense.setText(new DecimalFormat("##.##").format(mMeanOut));


        }

    }

}
