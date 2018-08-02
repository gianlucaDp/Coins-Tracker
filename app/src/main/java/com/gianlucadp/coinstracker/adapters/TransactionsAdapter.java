package com.gianlucadp.coinstracker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlucadp.coinstracker.R;
import com.gianlucadp.coinstracker.model.Transaction;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.supportClasses.IconsManager;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {
    private Context mContext;
    private List<Transaction> transactions;
    private Map<String,TransactionGroup> mGroups;

    private TransactionsAdapterListener listener;

    public TransactionsAdapter(Context context, List<Transaction> transactions, Map<String,TransactionGroup> groupMap) {
        this.mContext = context;
        this.transactions = transactions;
        this.mGroups = groupMap;

    }

    // define the listener
    public interface TransactionsAdapterListener {
        void onTransactionRemoved(Transaction transaction);
    }

    public void setListener(TransactionsAdapterListener listener) {
        this.listener = listener;
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
        private TextView mTextViewNotes;
        private Transaction mCurrentTransaction;



        private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Delete Transaction");
                alert.setMessage("Are you sure you want to delete this transaction?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        transactions.remove(getAdapterPosition());
                        notifyDataSetChanged();
                        if(listener != null) {
                            listener.onTransactionRemoved(mCurrentTransaction);
                        }
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();

                return true;
            }
        };



        public TransactionViewHolder(View view) {
            super(view);

            mImageViewIconFrom = view.findViewById(R.id.im_from_group);
            mTextViewFrom = view.findViewById(R.id.tv_from_group);
            mImageViewIconTo = view.findViewById(R.id.im_to_group);
            mTextViewTo = view.findViewById(R.id.tv_to_group);
            mTextViewValue = view.findViewById(R.id.tv_date);
            mTextViewDate = view.findViewById(R.id.tv_value);
            mTextViewNotes = view.findViewById(R.id.tv_notes);

            view.setOnLongClickListener(mOnLongClickListener);
        }


        public void loadTransactionGroup(int position) {
            if (transactions != null) {
                mCurrentTransaction = transactions.get(position);

                String fromGroupId = mCurrentTransaction.getFromGroup();
                String toGroupId = mCurrentTransaction.getToGroup();

                TransactionGroup fromGroup = mGroups.get(fromGroupId);
                TransactionGroup toGroup =   mGroups.get(toGroupId);

                int colorFrom = IconsManager.setColorBasedOnType(mContext,fromGroup.getType());
                Drawable fromIcon = new IconicsDrawable(mContext).icon(fromGroup.getImageId()).color(colorFrom).sizeDp(40);

                int colorTo = IconsManager.setColorBasedOnType(mContext,toGroup.getType());
                Drawable toIcon = new IconicsDrawable(mContext).icon(toGroup.getImageId()).color(colorTo).sizeDp(40);

                mImageViewIconFrom.setImageDrawable(fromIcon);
                mTextViewFrom.setText(fromGroup.getName());

                mImageViewIconTo.setImageDrawable(toIcon);
                mTextViewTo.setText(toGroup.getName());

                mTextViewValue.setText(String.valueOf(mCurrentTransaction.getValue()));

                long dateValue = mCurrentTransaction.getTimestamp();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                mTextViewDate.setText(sdf.format(new Date(dateValue)));
                mTextViewNotes.setText(mCurrentTransaction.getNotes());

            }

        }


    }




}
