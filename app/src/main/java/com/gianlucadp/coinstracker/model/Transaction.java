package com.gianlucadp.coinstracker.model;


import java.util.Date;

public class Transaction {
    private String fromGroup;
    private String toGroup;
    private float value;
    private long timestamp;

    public Transaction(){}

    public Transaction(String fromGroup, String toGroup, float value, long timestamp){
        this.fromGroup = fromGroup;
        this.toGroup = toGroup;
        this.value = value;
        this.timestamp = timestamp;

    }
}
