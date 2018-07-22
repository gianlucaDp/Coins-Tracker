package com.gianlucadp.coinstracker.supportClasses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gianlucadp.coinstracker.model.Transaction;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.model.TransactionValue;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class DatabaseManager {
    private static DatabaseReference mDatabase;
    private static String mUserId;

    public static void setDatabase(DatabaseReference database){
        mDatabase = database;
    }

    public static DatabaseReference[] addUser(String userId){
        mUserId = userId;
        DatabaseReference[] references = {mDatabase.child("users").child(mUserId).child("transaction_groups"),
                mDatabase.child("users").child(mUserId).child("transaction")};
        return references;
    }

    public static void addTransactionGroup(TransactionGroup transactionGroup){
        mDatabase.child("users").child(mUserId).child("transaction_groups").push().setValue(transactionGroup);
    }

    public static void addTransaction(Transaction transaction){
        mDatabase.child("users").child(mUserId).child("transaction").push().setValue(transaction);
        addTransactionDetailsInGroup(transaction);
    }

    public static void addTransactionDetailsInGroup(Transaction transaction){
        TransactionValue transactionValueIN = new TransactionValue(transaction.getFirebaseId(),transaction.getValue(),false);
        TransactionValue transactionValueOUT = new TransactionValue(transaction.getFirebaseId(),transaction.getValue(),transaction.isExpense());

        mDatabase.child("users").child(mUserId).child("transaction_groups").child(transaction.getFromGroup()).child("flows").push().setValue(transactionValueOUT);
        mDatabase.child("users").child(mUserId).child("transaction_groups").child(transaction.getToGroup()).child("flows").push().setValue(transactionValueIN);
    }


    public static void getTransactions(){
        final float netValue;
        DatabaseReference transactions = mDatabase.child("users").child(mUserId).child("transaction");
        ChildEventListener mTransactionGroupsEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Transaction transaction = dataSnapshot.getValue(Transaction.class);
                transaction.setFirebaseId(dataSnapshot.getKey());
                Log.d("AAA", String.valueOf(transaction.getValue()));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        transactions.addChildEventListener(mTransactionGroupsEventListener);
    }


}
