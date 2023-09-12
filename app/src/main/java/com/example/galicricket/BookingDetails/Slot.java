package com.example.galicricket.BookingDetails;
public class Slot {

    private boolean isAvailable;
    private String timing;

    public Slot() {
        // Default constructor required for Firebase
    }

    public Slot(boolean isAvailable, String timing) {
        this.isAvailable = isAvailable;
        this.timing = timing;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }
}
