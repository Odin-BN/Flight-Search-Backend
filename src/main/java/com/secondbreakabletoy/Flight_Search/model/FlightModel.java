package com.secondbreakabletoy.Flight_Search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightModel {
    private String departureDate_first;
    private String departureTime_first;
    private String arrivalDate_first;
    private String arrivalTime_first;
    private String departureAirport_first;
    private String arrivalAirport_first;
    private String airlineCode_first;
    private String airlineName_first;
    private String departureDate_second;
    private String departureTime_second;
    private String arrivalDate_second;
    private String arrivalTime_second;
    private String departureAirport_second;
    private String arrivalAirport_second;
    private String airlineCode_second;
    private String airlineName_second;
    private String totalFlightTime;
    private List<String> segmentDurations;
    private List<String> layoverTimes;
    private double totalPrice;
    private double pricePerTraveler;

    public FlightModel() {};

    public FlightModel(String departureDate_first, String departureTime_first, String arrivalDate_first, String arrivalTime_first, String departureAirport_first, String arrivalAirport_first, String airlineCode_first, String airlineName_first, String departureDate_second, String departureTime_second, String arrivalDate_second, String departureAirport_second, String arrivalTime_second, String arrivalAirport_second, String airlineCode_second, String airlineName_second, String totalFlightTime, List<String> segmentDurations, List<String> layoverTimes, double totalPrice, double pricePerTraveler) {
        this.departureDate_first = departureDate_first;
        this.departureTime_first = departureTime_first;
        this.arrivalDate_first = arrivalDate_first;
        this.arrivalTime_first = arrivalTime_first;
        this.departureAirport_first = departureAirport_first;
        this.arrivalAirport_first = arrivalAirport_first;
        this.airlineCode_first = airlineCode_first;
        this.airlineName_first = airlineName_first;
        this.departureDate_second = departureDate_second;
        this.departureTime_second = departureTime_second;
        this.arrivalDate_second = arrivalDate_second;
        this.departureAirport_second = departureAirport_second;
        this.arrivalTime_second = arrivalTime_second;
        this.arrivalAirport_second = arrivalAirport_second;
        this.airlineCode_second = airlineCode_second;
        this.airlineName_second = airlineName_second;
        this.totalFlightTime = totalFlightTime;
        this.segmentDurations = segmentDurations;
        this.layoverTimes = layoverTimes;
        this.totalPrice = totalPrice;
        this.pricePerTraveler = pricePerTraveler;
    }

    public String getDepartureDate_first() {
        return departureDate_first;
    }

    public void setDepartureDate_first(String departureDate_first) {
        this.departureDate_first = departureDate_first;
    }

    public String getDepartureTime_first() {
        return departureTime_first;
    }

    public void setDepartureTime_first(String departureTime_first) {
        this.departureTime_first = departureTime_first;
    }

    public String getArrivalDate_first() {
        return arrivalDate_first;
    }

    public void setArrivalDate_first(String arrivalDate_first) {
        this.arrivalDate_first = arrivalDate_first;
    }

    public String getArrivalTime_first() {
        return arrivalTime_first;
    }

    public void setArrivalTime_first(String arrivalTime_first) {
        this.arrivalTime_first = arrivalTime_first;
    }

    public String getDepartureAirport_first() {
        return departureAirport_first;
    }

    public void setDepartureAirport_first(String departureAirport_first) {
        this.departureAirport_first = departureAirport_first;
    }

    public String getArrivalAirport_first() {
        return arrivalAirport_first;
    }

    public void setArrivalAirport_first(String arrivalAirport_first) {
        this.arrivalAirport_first = arrivalAirport_first;
    }

    public String getAirlineCode_first() {
        return airlineCode_first;
    }

    public void setAirlineCode_first(String airlineCode_first) {
        this.airlineCode_first = airlineCode_first;
    }

    public String getAirlineName_first() {
        return airlineName_first;
    }

    public void setAirlineName_first(String airlineName_first) {
        this.airlineName_first = airlineName_first;
    }

    public String getDepartureDate_second() {
        return departureDate_second;
    }

    public void setDepartureDate_second(String departureDate_second) {
        this.departureDate_second = departureDate_second;
    }

    public String getDepartureTime_second() {
        return departureTime_second;
    }

    public void setDepartureTime_second(String departureTime_second) {
        this.departureTime_second = departureTime_second;
    }

    public String getArrivalDate_second() {
        return arrivalDate_second;
    }

    public void setArrivalDate_second(String arrivalDate_second) {
        this.arrivalDate_second = arrivalDate_second;
    }

    public String getArrivalTime_second() {
        return arrivalTime_second;
    }

    public void setArrivalTime_second(String arrivalTime_second) {
        this.arrivalTime_second = arrivalTime_second;
    }

    public String getDepartureAirport_second() {
        return departureAirport_second;
    }

    public void setDepartureAirport_second(String departureAirport_second) {
        this.departureAirport_second = departureAirport_second;
    }

    public String getArrivalAirport_second() {
        return arrivalAirport_second;
    }

    public void setArrivalAirport_second(String arrivalAirport_second) {
        this.arrivalAirport_second = arrivalAirport_second;
    }

    public String getAirlineCode_second() {
        return airlineCode_second;
    }

    public void setAirlineCode_second(String airlineCode_second) {
        this.airlineCode_second = airlineCode_second;
    }

    public String getAirlineName_second() {
        return airlineName_second;
    }

    public void setAirlineName_second(String airlineName_second) {
        this.airlineName_second = airlineName_second;
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
}

