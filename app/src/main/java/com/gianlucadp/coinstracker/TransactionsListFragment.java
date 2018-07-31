package com.gianlucadp.coinstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gianlucadp.coinstracker.adapters.TransactionGroupAdapter;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.supportClasses.Constants;

import java.util.ArrayList;

public class TransactionsListFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String ARG_PARAM1 = "revenue_tg_list";
    private static final String ARG_PARAM2 = "deposit_tg_list";
    private static final String ARG_PARAM3 = "expense_tg_list";

    RecyclerView mRevenueRecyclerView;
    TransactionGroupAdapter mRevenueAdapter;

    RecyclerView mDepositRecyclerView;
    TransactionGroupAdapter mDepositAdapter;

    RecyclerView mExpenseRecyclerView;
    TransactionGroupAdapter mExpenseAdapter;

    TextView mTotalRevenuesTextView;
    TextView mTotalExpensesTextView;

    private OnTransactionGroupInteractionListener mListener;

    public TransactionGroupAdapter getRevenueAdapter() {
        return mRevenueAdapter;
    }

    public TransactionGroupAdapter getDepositAdapter() {
        return mDepositAdapter;
    }

    public TransactionGroupAdapter getExpenseAdapter() {
        return mExpenseAdapter;
    }



    public TransactionsListFragment() {
    }

    public static TransactionsListFragment newInstance(ArrayList<TransactionGroup> revenueGroup,ArrayList<TransactionGroup> depositGroup, ArrayList<TransactionGroup> expenseGroup) {
        TransactionsListFragment fragment = new TransactionsListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, revenueGroup);
        args.putParcelableArrayList(ARG_PARAM2, depositGroup);
        args.putParcelableArrayList(ARG_PARAM3, expenseGroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateTotalsTable();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_transactions_list, container, false);

        mRevenueRecyclerView = rootView.findViewById(R.id.rv_revenues);
        mDepositRecyclerView = rootView.findViewById(R.id.rv_deposits);
        mExpenseRecyclerView = rootView.findViewById(R.id.rv_expenses);
        mTotalRevenuesTextView = rootView.findViewById(R.id.tv_total_revenue);
        mTotalExpensesTextView = rootView.findViewById(R.id.tv_total_expenses);


        ArrayList<TransactionGroup> revenues = null;
        ArrayList<TransactionGroup> deposits = null;
        ArrayList<TransactionGroup> expenses = null;


        if (getArguments() != null) {
            revenues = getArguments().getParcelableArrayList(ARG_PARAM1);

            deposits = getArguments().getParcelableArrayList(ARG_PARAM2);

            expenses = getArguments().getParcelableArrayList(ARG_PARAM3);


        }

        TransactionGroupAdapter.TransactionsGroupAdapterListener listener = new TransactionGroupAdapter.TransactionsGroupAdapterListener() {
            @Override
            public void onTransactionGroupRemoved(TransactionGroup group) {
                mListener.onTransactionGroupDeleted(group);
            }
        };
        mRevenueAdapter = new TransactionGroupAdapter(getContext(),TransactionGroup.GroupType.REVENUE,revenues);
        mRevenueAdapter.setListener(listener);
        mDepositAdapter = new TransactionGroupAdapter(getContext(),TransactionGroup.GroupType.DEPOSIT,deposits);
        mDepositAdapter.setListener(listener);
        mExpenseAdapter = new TransactionGroupAdapter(getContext(),TransactionGroup.GroupType.EXPENSE,expenses);
        mExpenseAdapter.setListener(listener);


        buildRecycleView(mRevenueRecyclerView,mRevenueAdapter);
        buildRecycleView(mDepositRecyclerView,mDepositAdapter);
        buildRecycleView(mExpenseRecyclerView,mExpenseAdapter);

        updateTotalsTable();


    return  rootView;
    }

    public void updateTotalsTable() {
        if (getContext().getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE)!=null) {
            SharedPreferences prefs = getContext().getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);

            float totalExpenses = prefs.getFloat(Constants.EXPENSES_SHARED_PREF_KEY, 0);
            float totalRevenues = prefs.getFloat(Constants.REVENUE_SHARED_PREF_KEY, 0);

            mTotalExpensesTextView.setText(String.valueOf(totalExpenses));
            mTotalRevenuesTextView.setText(String.valueOf(totalRevenues));
        }
    }

    private void buildRecycleView(RecyclerView recyclerView, TransactionGroupAdapter adapter){
        //Set layout manager
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        // Set Adapter
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (mTotalRevenuesTextView != null && mTotalExpensesTextView != null) {
            float expenses = sharedPreferences.getFloat(Constants.EXPENSES_SHARED_PREF_KEY, 0);
            float revenues = sharedPreferences.getFloat(Constants.REVENUE_SHARED_PREF_KEY, 0);
            mTotalExpensesTextView.setText(String.valueOf(expenses));
            mTotalRevenuesTextView.setText(String.valueOf(revenues));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTransactionGroupInteractionListener) {
            mListener = (OnTransactionGroupInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTransactionGroupInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTransactionGroupInteractionListener {
        void onTransactionGroupDeleted(TransactionGroup transactionGroup);
    }
}
