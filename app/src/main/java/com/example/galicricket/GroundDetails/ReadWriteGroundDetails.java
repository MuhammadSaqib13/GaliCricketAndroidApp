package com.example.galicricket.GroundDetails;

import com.example.galicricket.BookingDetails.Slot;

import java.util.Map;

public class ReadWriteGroundDetails {
    private String Name;
    private Map<String, Map<String, Slot>> slots;
    private String Address;

    public ReadWriteGroundDetails(String name, String address) {
        Name = name;
        Address = address;
    }
    public ReadWriteGroundDetails(String name, Map<String, Map<String, Slot>> slots, String address) {
        Name = name;
        this.slots = slots;
        Address = address;
    }

    public Map<String, Map<String, Slot>> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, Map<String, Slot>> slots) {
        this.slots = slots;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
