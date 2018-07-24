package com.gianlucadp.coinstracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gianlucadp.coinstracker.adapters.TransactionGroupAdapter;
import com.gianlucadp.coinstracker.model.Transaction;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class TransactionsListFragment extends Fragment {
    private static final String ARG_PARAM1 = "revenue_tg_list";
    private static final String ARG_PARAM2 = "deposit_tg_list";
    private static final String ARG_PARAM3 = "expense_tg_list";

    RecyclerView mRevenueRecyclerView;
    TransactionGroupAdapter mRevenueAdapter;

    RecyclerView mDepositRecyclerView;
    TransactionGroupAdapter mDepositAdapter;

    RecyclerView mExpenseRecyclerView;
    TransactionGroupAdapter mExpenseAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_transactions_list, container, false);

        mRevenueRecyclerView = rootView.findViewById(R.id.rv_revenues);
        mDepositRecyclerView = rootView.findViewById(R.id.rv_deposits);
        mExpenseRecyclerView = rootView.findViewById(R.id.rv_expenses);

        ArrayList<TransactionGroup> revenues = null;
        ArrayList<TransactionGroup> deposits = null;
        ArrayList<TransactionGroup> expenses = null;


        if (getArguments() != null) {
            revenues = getArguments().getParcelableArrayList(ARG_PARAM1);

            deposits = getArguments().getParcelableArrayList(ARG_PARAM2);

            expenses = getArguments().getParcelableArrayList(ARG_PARAM3);


        }
        mRevenueAdapter = new TransactionGroupAdapter(getContext(),TransactionGroup.GroupType.REVENUE,revenues);
        mDepositAdapter = new TransactionGroupAdapter(getContext(),TransactionGroup.GroupType.DEPOSIT,deposits);
        mExpenseAdapter = new TransactionGroupAdapter(getContext(),TransactionGroup.GroupType.EXPENSE,expenses);

        buildRecycleView(mRevenueRecyclerView,mRevenueAdapter);
        buildRecycleView(mDepositRecyclerView,mDepositAdapter);
        buildRecycleView(mExpenseRecyclerView,mExpenseAdapter);

    return  rootView;
    }

    private void buildRecycleView(RecyclerView recyclerView, TransactionGroupAdapter adapter){
        //Set layout manager
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        // Set Adapter
        recyclerView.setAdapter(adapter);
    }

}
