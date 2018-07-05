package com.gianlucadp.coinstracker.supportClasses;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import com.gianlucadp.coinstracker.adapters.TransactionGroupAdapter;
import com.gianlucadp.coinstracker.model.TransactionGroup;

public class DragListener implements View.OnDragListener {

    private boolean isDropped = false;

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

                    Log.d("AAA", sourceGroup.getName() + " -> " + targetGroup.getName());
                }
                break;
        }


        if (!isDropped && event.getLocalState() != null) {
            ((View) event.getLocalState()).setVisibility(View.VISIBLE);
        }
        return true;
    }
}