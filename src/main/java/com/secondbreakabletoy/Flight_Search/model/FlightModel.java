package com.secondbreakabletoy.Flight_Search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightModel {

    private List<FlightSegments> flightSegments;
    private List<FlightPrices> flightPrices;
    private String totalFlightTime;
    private List<String> segmentDurations;
    private List<String> layoverTimes;
    private double totalPrice;
    private double pricePerTraveler;
    private double basePrice;
    private double feesPrice;

    public FlightModel() {};

    public FlightModel(List<FlightSegments> flightSegments, List<FlightPrices> flightPrices, String totalFlightTime, List<String> segmentDurations, List<String> layoverTimes, double totalPrice, double pricePerTraveler, double feesPrice, double basePrice) {
        this.flightSegments = flightSegments;
        this.flightPrices = flightPrices;
        this.totalFlightTime = totalFlightTime;
        this.segmentDurations = segmentDurations;
        this.layoverTimes = layoverTimes;
        this.totalPrice = totalPrice;
        this.pricePerTraveler = pricePerTraveler;
        this.feesPrice = feesPrice;
        this.basePrice = basePrice;
    }

    public List<FlightSegments> getFlightSegments() {
        return flightSegments;
    }

    public void setFlightSegments(List<FlightSegments> flightSegments) {
        this.flightSegments = flightSegments;
    }

    public List<FlightPrices> getFlightPrices() {
        return flightPrices;
    }

    public void setFlightPrices(List<FlightPrices> flightPrices) {
        this.flightPrices = flightPrices;
    }

    public String getTotalFlightTime() {
        return totalFlightTime;
    }

    public void setTotalFlightTime(String totalFlightTime) {
        this.totalFlightTime = totalFlightTime;
    }

    public List<String> getSegmentDurations() {
        return segmentDurations;
    }

    public void setSegmentDurations(List<String> segmentDurations) {
        this.segmentDurations = segmentDurations;
    }

    public List<String> getLayoverTimes() {
        return layoverTimes;
    }

    public void setLayoverTimes(List<String> layoverTimes) {
        this.layoverTimes = layoverTimes;
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

