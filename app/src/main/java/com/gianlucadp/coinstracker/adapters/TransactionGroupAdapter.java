package com.gianlucadp.coinstracker.adapters;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlucadp.coinstracker.AddNewGroupFragment;
import com.gianlucadp.coinstracker.AppBaseActivity;
import com.gianlucadp.coinstracker.R;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.model.TransactionValue;
import com.gianlucadp.coinstracker.supportClasses.DatabaseManager;
import com.gianlucadp.coinstracker.supportClasses.DragListener;
import com.gianlucadp.coinstracker.supportClasses.IconsManager;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;


public class TransactionGroupAdapter extends RecyclerView.Adapter<TransactionGroupAdapter.GroupViewHolder> {
    private Context mContext;
    private  TransactionGroup.GroupType elementsType;
    private List<TransactionGroup> transactionGroups;



    public TransactionGroupAdapter(Context context, TransactionGroup.GroupType elementsType, List<TransactionGroup> transactionGroups) {
        this.mContext = context;
        this.transactionGroups = transactionGroups;
        this.elementsType = elementsType;
    }

    public void setTransactionGroups(List<TransactionGroup> groups) {
        this.transactionGroups = groups;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (transactionGroups == null) {
            return 1;
        } else {
            return transactionGroups.size()+1;
        }
    }

    public List<TransactionGroup> getTransactionGroups(){
        return transactionGroups;
    }
    public void addItem(TransactionGroup transactionGroup){
        if (transactionGroups==null){
            transactionGroups = new ArrayList<>();
        }
        this.transactionGroups.add(transactionGroup);
        notifyDataSetChanged();
    }

    public void updateGroup(TransactionGroup tobeUpdatedGroup){
        for (int i=0;i< transactionGroups.size();i++) {
            if (transactionGroups.get(i).getFirebaseId().equals(tobeUpdatedGroup.getFirebaseId())){
                transactionGroups.set(i,tobeUpdatedGroup);
                break;
            }
        }
        notifyDataSetChanged();

    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_recyclerview_transaction_group;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new GroupViewHolder(view);
    }


    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        if (transactionGroups == null || position== transactionGroups.size()){
            holder.loadAddNewButton();

        }
        else{
            holder.loadTransactionGroup(position);
            holder.initializeDragAndDrop(position);

        }
    }


    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageViewIcon;
        private TextView mTextViewName;
        private TextView mTextViewValue;





        public GroupViewHolder(View view) {
            super(view);
            mImageViewIcon = view.findViewById(R.id.iv_icon_group_item);
            mTextViewName = view.findViewById(R.id.tv_item_name);
            mTextViewValue = view.findViewById(R.id.tv_item_value);

        }

        private void initializeDragAndDrop(int position){
            mImageViewIcon.setTag(position);
            mImageViewIcon.setOnDragListener(new DragListener(mContext));
        }

        public void loadTransactionGroup(int position) {
            if (transactionGroups != null) {
                TransactionGroup currentTransaction = transactionGroups.get(position);
                int color = IconsManager.setColorBasedOnType(mContext,currentTransaction.getType());
                Drawable addNewIcon = new IconicsDrawable(mContext)
                        .icon(currentTransaction.getImageId()).color(color).sizeDp(48);
                mImageViewIcon.setImageDrawable(addNewIcon);
                mImageViewIcon.setOnLongClickListener(mOnClickListener);
                mImageViewIcon.setOnClickListener(null);
                mTextViewName.setText(currentTransaction.getName());

                float value = 0;

                if (currentTransaction.getType()== TransactionGroup.GroupType.DEPOSIT) {
                    value+=  currentTransaction.getInitialValue();

                }

                if (currentTransaction.getTransactionsValue()!=null && currentTransaction.getTransactionsValue().size()>0){

                    for (TransactionValue transaction:currentTransaction.getTransactionsValue()) {
                        value+=transaction.getRealValue();
                    }
                    mTextViewValue.setText(String.valueOf(value));
                }else{
                    mTextViewValue.setText(String.valueOf(value));
                }

            }

        }


        public void loadAddNewButton(){
            Drawable addNewIcon  = IconsManager.createNewIcon(mContext,CommunityMaterial.Icon.cmd_plus_circle,Color.LTGRAY,56);
            mImageViewIcon.setImageDrawable(addNewIcon);
            mImageViewIcon.setOnClickListener(mOnClickNewItemListener);
            mTextViewName.setText(R.string.add_new);
        }


        private final View.OnLongClickListener mOnClickListener = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return  true;
            }
        };

        private final View.OnClickListener mOnClickNewItemListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment addNewGroupFragment = AddNewGroupFragment.newInstance(elementsType);
                addNewGroupFragment.show(((AppBaseActivity) mContext).getSupportFragmentManager(), "dialog");
            }
        };

    }




}
