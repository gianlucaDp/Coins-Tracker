package com.gianlucadp.coinstracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gianlucadp.coinstracker.adapters.TransactionGroupAdapter;
import com.gianlucadp.coinstracker.adapters.TransactionsAdapter;
import com.gianlucadp.coinstracker.model.Transaction;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.supportClasses.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



public class TransactionsHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "transactions";


    // TODO: Rename and change types of parameters
    private List<Transaction> mTransactions;
    private Map<String,TransactionGroup> mGroups = new HashMap<>();
    RecyclerView mTransactionsRecyclerView;
    TransactionsAdapter mTransactionsAdapter;

    //private OnFragmentInteractionListener mListener;

    public TransactionsHistoryFragment() {
        // Required empty public constructor
    }


    public static TransactionsHistoryFragment newInstance(ArrayList<Transaction> transactions, HashMap<String,TransactionGroup> groups) {
        TransactionsHistoryFragment fragment = new TransactionsHistoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, transactions);
        Utilities.writeMapTGAsBundle(args,groups);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             Utilities.readMapTGFromBundle(getArguments(),mGroups);
            mTransactions = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_transactions_history, container, false);
    mTransactionsRecyclerView = rootView.findViewById(R.id.rv_history);

        mTransactionsAdapter = new TransactionsAdapter(getContext(),mTransactions,mGroups);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mTransactionsRecyclerView.setLayoutManager(manager);
        mTransactionsRecyclerView.setHasFixedSize(true);
        // Set Adapter
        mTransactionsRecyclerView.setAdapter(mTransactionsAdapter);
    return  rootView;
    }
/**
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    */
}
