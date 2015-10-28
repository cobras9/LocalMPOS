package com.mobilis.android.nfc.tasks;

/**
 * Created by lewischao on 20/10/15.
 */
public class PINModel {
    public String id;
    public String entry;

    public String getId() {
        return id;
    }

    public PINModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getEntry() {
        return entry;
    }

    public PINModel setEntry(String entry) {
        this.entry = entry;
        return this;
    }
}
