package com.gianlucadp.coinstracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gianlucadp.coinstracker.adapters.TransactionGroupAdapter;
import com.gianlucadp.coinstracker.model.TransactionGroup;

public class TransactionsListFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_transactions_list, container, false);

        mRevenueRecyclerView = rootView.findViewById(R.id.rv_revenues);
        mDepositRecyclerView = rootView.findViewById(R.id.rv_deposits);
        mExpenseRecyclerView = rootView.findViewById(R.id.rv_expenses);

        mRevenueAdapter = new TransactionGroupAdapter(getContext(),TransactionGroup.GroupType.REVENUE,null);
        mDepositAdapter = new TransactionGroupAdapter(getContext(),TransactionGroup.GroupType.DEPOSIT,null);
        mExpenseAdapter = new TransactionGroupAdapter(getContext(),TransactionGroup.GroupType.EXPENSE,null);

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
