package com.gianlucadp.coinstracker;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gianlucadp.coinstracker.model.Transaction;
import com.google.android.gms.ads.MobileAds;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class StatisticsFragment extends Fragment {
    private static final String ARG_PARAM1 = "transactions";

    private GraphView mGraphView;
    private TextView mMinIncome;
    private TextView mMaxIncome;
    private TextView mMeanIncome;

    private TextView mMinExpense;
    private TextView mMaxExpense;
    private TextView mMeanExpense;

    private TextView mMinNetValue;
    private TextView mMaxNetValue;
    private TextView mMeanNetValue;

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

        mMinNetValue = view.findViewById(R.id.tv_min_net);
        mMaxNetValue = view.findViewById(R.id.tv_max_net);
        mMeanNetValue = view.findViewById(R.id.tv_mean_net);
        new GenerateData().execute();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544~3347511713");

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

        private float mMinNet=Float.MAX_VALUE;
        private float mMinIn=Float.MAX_VALUE;
        private float mMinOut=Float.MAX_VALUE;

        private float mMaxNet=0;
        private float mMaxIn=0;
        private float mMaxOut=0;

        private float mMeanNet=0;
        private float mMeanIn=0;
        private float mMeanOut=0;

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

                    if (!mExpensesByDay.containsKey(dateValue)) {
                        mExpensesByDay.put(dateValue, value);
                    } else {

                        mExpensesByDay.put(dateValue, mExpensesByDay.get(dateValue) + value);
                    }
                    value *= -1;
                } else {

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

            int i = 0;
            int j = 0;
            int k = 0;


            for (Long date: asSortedList(mNetByDay.keySet())){

                if (mRevenuesByDay.containsKey(date)) {
                    float inValue = mRevenuesByDay.get(date);
                    float cumulativeInValue;
                    if (i>0){
                        cumulativeInValue = inValue + (float) inValues[i-1].getY();
                    }else{
                        cumulativeInValue = inValue;
                    }
                    inValues[i] = new DataPoint(new Date(date), cumulativeInValue);
                    i++;

                    if (inValue<mMinIn){
                        mMinIn = inValue;
                    }

                    if (inValue>mMaxIn){
                        mMaxIn = inValue;
                    }

                    mMeanIn += inValue;


                }else if (i>0){

                    inValues[i] = new DataPoint(new Date(date), inValues[i-1].getY());
                    i++;
                }else{
                    inValues[i] = new DataPoint(new Date(date), 0);
                    i++;
                }

                if (mExpensesByDay.containsKey(date)) {
                    float outValue = mExpensesByDay.get(date);
                    float cumulativeOutValue;
                    if (j>0){
                        cumulativeOutValue = outValue + (float) outValues[j-1].getY();
                    }else{
                        cumulativeOutValue = outValue;
                    }

                    outValues[j] = new DataPoint(new Date(date), cumulativeOutValue);
                    j++;

                    if (outValue<mMinOut){
                        mMinOut = outValue;
                    }

                    if (outValue>mMaxOut){
                        mMaxOut = outValue;
                    }

                    mMeanOut += outValue;

                }else if (j>0){
                    outValues[j] = new DataPoint(new Date(date), outValues[j-1].getY());
                    j++;
            }else{
                    outValues[j] = new DataPoint(new Date(date), 0);
                    j++;
                }


                float netValue = mNetByDay.get(date);
                float cumulativeNetValue;
                if (k>0){
                    cumulativeNetValue = netValue + (float) netValues[k-1].getY();
                }else{
                    cumulativeNetValue = netValue;
                }
                netValues[k] = new DataPoint(new Date(date),cumulativeNetValue);

                if (netValue<mMinNet){
                    mMinNet = netValue;
                }

                if (netValue>mMaxNet){
                    mMaxNet = netValue;
                }

                mMeanNet += netValue;
                k++;
            }

            if (i>0){
                mMeanIn = mMeanIn / i;
            }
            if (j>0){
                mMeanOut = mMeanOut / j;
            }
            if (k>0){
                mMeanNet = mMeanNet / k;
            }



            List<DataPoint[]> points = new ArrayList<>();
            for (DataPoint point: inValues) {
                Log.d("AAA",point.toString());
            }
            Log.d("AAA","----");
            for (DataPoint point: outValues) {
                Log.d("AAA",point.toString());
            }
            Log.d("AAA","----");

            for (DataPoint point: netValues) {
                Log.d("AAA",point.toString());
            }
            Log.d("AAA","----");

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




            }else{
                //Else no points are available, load default value
                Date date = new Date();
                mGraphView.getViewport().setMinX(date.getTime());
                mGraphView.getViewport().setMaxX(date.getTime()+ TimeUnit.DAYS.toSeconds(1));

            }

            if(mMinNet==Float.MAX_VALUE){
                mMinNet = 0;
            };
            if(mMinOut==Float.MAX_VALUE){
                mMinOut = 0;
            };
            if(mMinIn==Float.MAX_VALUE){
                mMinIn = 0;
            };

            mGraphView.setVisibility(View.VISIBLE);

            mMinIncome.setText(String.valueOf(mMinIn));
            mMaxIncome.setText(String.valueOf(mMaxIn));
            mMeanIncome.setText(new DecimalFormat("##.##").format(mMeanIn));

            mMinExpense.setText(String.valueOf(mMinOut));
            mMaxExpense.setText(String.valueOf(mMaxOut));
            mMeanExpense.setText(new DecimalFormat("##.##").format(mMeanOut));

            mMinNetValue.setText(String.valueOf(mMinNet));
            mMaxNetValue.setText(String.valueOf(mMaxNet));
            mMeanNetValue.setText(new DecimalFormat("##.##").format(mMeanNet));


        }

    }

}
