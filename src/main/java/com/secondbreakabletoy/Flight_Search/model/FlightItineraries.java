package com.secondbreakabletoy.Flight_Search.model;

import java.util.List;

public class FlightItineraries {
    private String itinerate_id;
    private String totalDuration;
    private String waitTime; //esto no deberia ser lista
    private List<FlightSegments> flightSegments;

    public FlightItineraries() {
    }

    public FlightItineraries(String itinerate_id, String totalDuration, String waitTime, List<FlightSegments> flightSegments) {
        this.itinerate_id = itinerate_id;
        this.totalDuration = totalDuration;
        this.waitTime = waitTime;
        this.flightSegments = flightSegments;
    }

    public String getItinerate_id() {
        return itinerate_id;
    }

    public void setItinerate_id(String itinerate_id) {
        this.itinerate_id = itinerate_id;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public List<FlightSegments> getFlightSegments() {
        return flightSegments;
    }

    public void setFlightSegments(List<FlightSegments> flightSegments) {
        this.flightSegments = flightSegments;
    }
}
