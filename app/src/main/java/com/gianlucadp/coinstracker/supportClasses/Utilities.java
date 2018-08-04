package com.gianlucadp.coinstracker.supportClasses;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.gianlucadp.coinstracker.model.Transaction;
import com.gianlucadp.coinstracker.model.TransactionGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Utilities {

    public static void writeMapTGAsBundle(Bundle bundle, Map<String, TransactionGroup> map) {
        for (Map.Entry<String, TransactionGroup> entry : map.entrySet()) {
            bundle.putParcelable(entry.getKey(), entry.getValue());
        }
    }

    public static void readMapTGFromBundle(Bundle bundle, Map<String, TransactionGroup> map) {
        for (String key : bundle.keySet()) {
            map.put(key, (TransactionGroup)bundle.getParcelable(key));
        }
    }


    public static void writeMapTAsBundle(Bundle bundle, Map<String, Transaction> map) {
        for (Map.Entry<String, Transaction> entry : map.entrySet()) {
            bundle.putParcelable(entry.getKey(), entry.getValue());
        }
    }

    public static void readMapTFromBundle(Bundle bundle, Map<String, Transaction> map) {
        for (String key : bundle.keySet()) {
            map.put(key, (Transaction)bundle.getParcelable(key));
        }
    }

    public static HashMap mergeMaps(Map<String,TransactionGroup> a, Map<String,TransactionGroup> b, Map<String,TransactionGroup> c) {
        HashMap<String,TransactionGroup> d = new HashMap();

        d.putAll(a);
        d.putAll(b);
        d.putAll(c);
        return  d;
    }

    public static AlertDialog showDialog(Context ctx, String title, String msg,
                                  String btn1, String btn2,
                                  DialogInterface.OnClickListener listener1,
                                  DialogInterface.OnClickListener listener2) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(btn1, listener1);
        if (btn2 != null)
            builder.setNegativeButton(btn2, listener2);

        AlertDialog alert = builder.create();
        return alert;
    }

}
