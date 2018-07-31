package com.gianlucadp.coinstracker.supportClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import com.gianlucadp.coinstracker.AppBaseActivity;
import com.gianlucadp.coinstracker.NewTransactionActivity;
import com.gianlucadp.coinstracker.adapters.TransactionGroupAdapter;
import com.gianlucadp.coinstracker.model.TransactionGroup;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class DragListener implements View.OnDragListener {
    private Context mContext;
    private boolean isDropped = false;

    public DragListener(Context context){
        this.mContext = context;
    }
    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {

            case DragEvent.ACTION_DROP:
                isDropped = true;

                View viewSource = (View) event.getLocalState();
                Integer positionTarget = (Integer) v.getTag();

                if (viewSource != null && positionTarget != null) {

                    RecyclerView sourceList = (RecyclerView) viewSource.getParent().getParent();
                    TransactionGroupAdapter adapterSource = (TransactionGroupAdapter) sourceList.getAdapter();
                    int positionSource = (int) viewSource.getTag();
                    TransactionGroup sourceGroup = adapterSource.getTransactionGroups().get(positionSource);


                    RecyclerView targetList = (RecyclerView) v.getParent().getParent();
                    TransactionGroupAdapter targetAdapter = (TransactionGroupAdapter) targetList.getAdapter();
                    TransactionGroup targetGroup = targetAdapter.getTransactionGroups().get(positionTarget);

                    if (!sourceGroup.getFirebaseId().equals(targetGroup.getFirebaseId())
                            && !sourceGroup.getType().equals(TransactionGroup.GroupType.EXPENSE)
                            && !targetGroup.getType().equals(TransactionGroup.GroupType.REVENUE)
                            && !(sourceGroup.getType().equals(TransactionGroup.GroupType.REVENUE) && targetGroup.getType().equals(TransactionGroup.GroupType.EXPENSE))) {

                            ((View) event.getLocalState()).setVisibility(View.VISIBLE);


                        Intent createTransaction = new Intent(mContext.getApplicationContext(), NewTransactionActivity.class);
                        createTransaction.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        createTransaction.putExtra(Constants.INTENT_SOURCE_GROUP, sourceGroup);
                        createTransaction.putExtra(Constants.INTENT_TARGET_GROUP, targetGroup);
                        mContext.getApplicationContext().startActivity(createTransaction);
                    }
                }else {
                    Log.d("AAA",String.valueOf(v.getId()));
                }
                break;
        }


        if (!isDropped && event.getLocalState() != null) {
            ((View) event.getLocalState()).setVisibility(View.VISIBLE);
        }
        return true;
    }

}