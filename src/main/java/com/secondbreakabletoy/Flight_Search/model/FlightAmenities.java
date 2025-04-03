package com.secondbreakabletoy.Flight_Search.model;

public class FlightAmenities {
    private String description;
    private boolean isChargeable;

    public FlightAmenities() {
    }

    public FlightAmenities(String description, boolean isChargeable) {
        this.description = description;
        this.isChargeable = isChargeable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChargeable() {
        return isChargeable;
    }

    public void setChargeable(boolean isChargeable) {
        this.isChargeable = isChargeable;
    }
}
