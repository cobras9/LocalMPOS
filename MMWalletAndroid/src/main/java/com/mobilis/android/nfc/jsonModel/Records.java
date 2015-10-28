package com.mobilis.android.nfc.jsonModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lewischao on 10/03/15.
 */
public class Records<T> implements Serializable{
    private List<T> records;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
