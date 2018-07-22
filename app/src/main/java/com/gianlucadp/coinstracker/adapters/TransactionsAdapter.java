package com.gianlucadp.coinstracker.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlucadp.coinstracker.AppBaseActivity;
import com.gianlucadp.coinstracker.R;
import com.gianlucadp.coinstracker.model.Transaction;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.supportClasses.IconsManager;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {
    private Context mContext;
    private List<Transaction> transactions;



    public TransactionsAdapter(Context context, TransactionGroup.GroupType elementsType, List<Transaction> transactions) {
        this.mContext = context;
        this.transactions = transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (transactions == null) {
            return 0;
        } else {
            return transactions.size();
        }
    }

    public List<Transaction> getTransactions(){
        return transactions;
    }

    public void addItem(Transaction transaction){
        if (transactions ==null){
            transactions = new ArrayList<>();
        }
        this.transactions.add(transaction);
        notifyDataSetChanged();
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_recyclerview_transaction;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TransactionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
            holder.loadTransactionGroup(position);
    }


    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageViewIconFrom;
        private TextView mTextViewFrom;
        private ImageView mImageViewIconTo;
        private TextView mTextViewTo;
        private TextView mTextViewValue;
        private TextView mTextViewDate;





        public TransactionViewHolder(View view) {
            super(view);

            mImageViewIconFrom= view.findViewById(R.id.im_from_group);
            mTextViewFrom= view.findViewById(R.id.tv_from_group);
            mImageViewIconTo= view.findViewById(R.id.im_to_group);
            mTextViewTo= view.findViewById(R.id.tv_to_group);
            mTextViewValue= view.findViewById(R.id.tv_date);
            mTextViewDate= view.findViewById(R.id.tv_value);
        }


        public void loadTransactionGroup(int position) {
            if (transactions != null) {
                Transaction currentTransaction = transactions.get(position);

                String fromGroupId = currentTransaction.getFromGroup();
                String toGroupId = currentTransaction.getToGroup();

                TransactionGroup fromGroup = ((AppBaseActivity) mContext).getGroup(fromGroupId);
                TransactionGroup toGroup = ((AppBaseActivity) mContext).getGroup(toGroupId);

                int colorFrom = IconsManager.setColorBasedOnType(mContext,fromGroup.getType());
                Drawable fromIcon = new IconicsDrawable(mContext).icon(fromGroup.getImageId()).color(colorFrom).sizeDp(40);

                int colorTo = IconsManager.setColorBasedOnType(mContext,toGroup.getType());
                Drawable toIcon = new IconicsDrawable(mContext).icon(toGroup.getImageId()).color(colorTo).sizeDp(40);

                mImageViewIconFrom.setImageDrawable(fromIcon);
                mTextViewFrom.setText(fromGroup.getName());

                mImageViewIconTo.setImageDrawable(toIcon);
                mTextViewTo.setText(toGroup.getName());

                mTextViewValue.setText(String.valueOf(currentTransaction.getValue()));
                //Todo: add date
                mTextViewDate.setText("11-11-2018");


            }

        }


    }




}
