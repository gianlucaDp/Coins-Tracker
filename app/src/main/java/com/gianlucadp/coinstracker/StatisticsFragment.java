package com.gianlucadp.coinstracker;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gianlucadp.coinstracker.model.Transaction;
import com.google.android.gms.ads.MobileAds;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StatisticsFragment extends Fragment {
    private static final String ARG_PARAM1 = "transactions";

    private GraphView mGraphView;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("AAA", "Start of on view created");
        mGraphView = view.findViewById(R.id.graph);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        private float mMinNet=0;
        private float mMinIn=0;
        private float mMinOut=0;

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
                //Rounding to day value
                dateValue -= (dateValue%(24*1000*60*60));

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


            DataPoint[] inValues = new DataPoint[mRevenuesByDay.size()];
            DataPoint[] outValues = new DataPoint[mExpensesByDay.size()];
            DataPoint[] netValues = new DataPoint[mNetByDay.size()];

            int i = 0;
            int j = 0;
            int k = 0;

            for (Long date: asSortedList(mNetByDay.keySet())){
                if (mRevenuesByDay.containsKey(date)) {
                    float inValue = mRevenuesByDay.get(date);
                    inValues[i] = new DataPoint(new Date(date), inValue);
                    i++;

                    if (inValue<mMinIn){
                        mMinIn = inValue;
                    }

                    if (inValue>mMaxIn){
                        mMaxIn = inValue;
                    }

                    mMeanIn += inValue;

                }
                if (mExpensesByDay.containsKey(date)) {
                    float outValue = mExpensesByDay.get(date);
                    outValues[j] = new DataPoint(new Date(date), outValue);
                    j++;

                    if (outValue<mMinOut){
                        mMinOut = outValue;
                    }

                    if (outValue>mMaxOut){
                        mMaxOut = outValue;
                    }

                    mMeanOut += outValue;


                }
                float netValue = mNetByDay.get(date);
                netValues[k] = new DataPoint(new Date(date),netValue);

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
                //Else no points are available, load default value
                Date date = new Date();
                mGraphView.getViewport().setMinX(date.getTime());
                mGraphView.getViewport().setMaxX(date.getTime()+ 24*1000*60*60);

            }

        }

    }

}
