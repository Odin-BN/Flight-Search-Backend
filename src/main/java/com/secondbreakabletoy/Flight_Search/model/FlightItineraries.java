package com.secondbreakabletoy.Flight_Search.model;

import java.util.List;

public class FlightItineraries {
    private String totalDuration;
    private List<String> waitTime;
    private List<FlightSegments> flightSegments;

    public FlightItineraries() {
    }

    public FlightItineraries(String totalDuration, List<String> waitTime, List<FlightSegments> flightSegments) {
        this.totalDuration = totalDuration;
        this.waitTime = waitTime;
        this.flightSegments = flightSegments;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public List<String> getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(List<String> waitTime) {
        this.waitTime = waitTime;
    }

    public List<FlightSegments> getFlightSegments() {
        return flightSegments;
    }

    public void setFlightSegments(List<FlightSegments> flightSegments) {
        this.flightSegments = flightSegments;
    }
}
