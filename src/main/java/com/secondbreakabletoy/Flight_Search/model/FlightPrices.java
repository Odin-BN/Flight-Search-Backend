package com.secondbreakabletoy.Flight_Search.model;

public class FlightPrices {
    private String CabinType;
    private String ClassType;
    private String CheckedBagsWeight;
    private String CheckedBagsUnit;

    public FlightPrices() {
    }

    public FlightPrices(String cabinType, String classType, String checkedBagsWeight, String checkedBagsUnit) {
        CabinType = cabinType;
        ClassType = classType;
        CheckedBagsWeight = checkedBagsWeight;
        CheckedBagsUnit = checkedBagsUnit;
    }

    public String getCabinType() {
        return CabinType;
    }

    public void setCabinType(String cabinType) {
        CabinType = cabinType;
    }

    public String getClassType() {
        return ClassType;
    }

    public void setClassType(String classType) {
        ClassType = classType;
    }

    public String getCheckedBagsWeight() {
        return CheckedBagsWeight;
    }

    public void setCheckedBagsWeight(String checkedBagsWeight) {
        CheckedBagsWeight = checkedBagsWeight;
    }

    public String getCheckedBagsUnit() {
        return CheckedBagsUnit;
    }

    public void setCheckedBagsUnit(String checkedBagsUnit) {
        CheckedBagsUnit = checkedBagsUnit;
    }
}
