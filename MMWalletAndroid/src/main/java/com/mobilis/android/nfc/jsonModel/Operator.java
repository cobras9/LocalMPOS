package com.mobilis.android.nfc.jsonModel;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewischao on 10/03/15.
 */
public class Operator {
    private String description;
    private List<Preset> presets;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Preset> getPresets() {
        return presets;
    }

    public void setPresets(List<Preset> presets) {
        this.presets = presets;
    }
    public void addPreset(Preset preset){
        if(presets == null){
            presets = new ArrayList<>();
        }
        presets.add(preset);
    }
}
