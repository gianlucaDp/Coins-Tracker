package com.gianlucadp.coinstracker.adapters;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlucadp.coinstracker.AddNewGroupFragment;
import com.gianlucadp.coinstracker.AppBaseActivity;
import com.gianlucadp.coinstracker.R;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.supportClasses.DragListener;
import com.gianlucadp.coinstracker.supportClasses.GestureListener;
import com.gianlucadp.coinstracker.supportClasses.IconsManager;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;


public class TransactionGroupAdapter extends RecyclerView.Adapter<TransactionGroupAdapter.GroupViewHolder> {
    private Context mContext;
    private  TransactionGroup.GroupType elementsType;
    private List<TransactionGroup> transactionGroups;
    private TransactionsGroupAdapterListener listener;

    public interface  TransactionsGroupAdapterListener{
        void onTransactionGroupRemoved(TransactionGroup group);
    }

    public void setListener(TransactionsGroupAdapterListener listener) {
        this.listener = listener;
    }

    public TransactionGroupAdapter(Context context, TransactionGroup.GroupType elementsType, List<TransactionGroup> transactionGroups) {
        this.mContext = context;
        this.transactionGroups = transactionGroups;
        this.elementsType = elementsType;
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
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_recyclerview_transaction_group;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new GroupViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        if (transactionGroups == null || position== transactionGroups.size()){
            holder.loadAddNewButton();

        }
        else{
            holder.loadTransactionGroup(position);
            holder.initializeDragAndDrop(position);
            holder.initializeDelete();

        }
    }


    class GroupViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageViewIcon;
        private TextView mTextViewName;
        private TextView mTextViewValue;
        private TransactionGroup mCurrentTransactionGroup;
        private GestureDetector mDetector;




        private GroupViewHolder(View view) {
            super(view);
            mImageViewIcon = view.findViewById(R.id.iv_icon_group_item);
            mTextViewName = view.findViewById(R.id.tv_item_name);
            mTextViewValue = view.findViewById(R.id.tv_item_value);

        }

        private void initializeDragAndDrop(int position){
            mImageViewIcon.setTag(position);
            mImageViewIcon.setOnDragListener(new DragListener(mContext));
        }

        private void loadTransactionGroup(int position) {
            if (transactionGroups != null) {
                mCurrentTransactionGroup = transactionGroups.get(position);
                int color = IconsManager.setColorBasedOnType(mContext,mCurrentTransactionGroup.getType());
                Drawable addNewIcon = new IconicsDrawable(mContext)
                        .icon(mCurrentTransactionGroup.getImageId()).color(color).sizeDp(48);
                mImageViewIcon.setImageDrawable(addNewIcon);
                mImageViewIcon.setOnLongClickListener(mOnClickListener);
                mImageViewIcon.setOnClickListener(null);
                mTextViewName.setText(mCurrentTransactionGroup.getName());
                mTextViewValue.setText(String.valueOf(mCurrentTransactionGroup.getValue()));


            }

        }


        private void loadAddNewButton(){
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



        private void initializeDelete() {


            GestureListener gestureListener = new GestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    alert.setTitle(R.string.delete_group_title);
                    alert.setMessage(R.string.delete_group_message);
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            transactionGroups.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            if(listener != null) {
                                listener.onTransactionGroupRemoved(mCurrentTransactionGroup);
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
                    return false;
                }
            };

            mDetector = new GestureDetector(mContext, gestureListener);

            mImageViewIcon.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return mDetector.onTouchEvent(motionEvent);
                }
            });
        }
    }




}
