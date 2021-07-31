package com.mihirzalawadiya.expensemanager.model;

public class PaymentMode {
    private int modeID;
    private String modeName;

    public int getModeID() {
        return modeID;
    }

    public void setModeID(int modeID) {
        this.modeID = modeID;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    @Override
    public String toString() {
        return modeName;
    }
}
