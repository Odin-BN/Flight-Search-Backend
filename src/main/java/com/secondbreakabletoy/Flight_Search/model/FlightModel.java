package com.secondbreakabletoy.Flight_Search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightModel {
    private String flight_id;
    //private List<FlightSegments> flightSegments;
    private List<FlightItineraries> infoPerItinerary;
    //private List<String> layoverTimes;
    private double totalPrice;
    private double pricePerTraveler;
    private double basePrice;
    private double feesPrice;


    public FlightModel() {};

    public FlightModel(String flight_id, List<FlightItineraries> infoPerItinerary, double totalPrice,
                       double pricePerTraveler, double feesPrice, double basePrice) {
        this.flight_id = flight_id;
        this.infoPerItinerary = infoPerItinerary;
        this.totalPrice = totalPrice;
        this.pricePerTraveler = pricePerTraveler;
        this.feesPrice = feesPrice;
        this.basePrice = basePrice;
    }

    public String getFlight_id() {
        return flight_id;
    }

    public void setFlight_id(String flight_id) {
        this.flight_id = flight_id;
    }

    public List<FlightItineraries> getInfoPerItinerary() {
        return infoPerItinerary;
    }

    public void setInfoPerItinerary(List<FlightItineraries> infoPerItinerary) {
        this.infoPerItinerary = infoPerItinerary;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getPricePerTraveler() {
        return pricePerTraveler;
    }

    public void setPricePerTraveler(double pricePerTraveler) {
        this.pricePerTraveler = pricePerTraveler;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getFeesPrice() {
        return feesPrice;
    }

    public void setFeesPrice(double feesPrice) {
        this.feesPrice = feesPrice;
    }
}

