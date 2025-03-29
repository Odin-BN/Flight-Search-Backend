package com.secondbreakabletoy.Flight_Search.model;

public class FlightSegments {
    private String departureDate;
    private String departureTime;
    private String arrivalDate;
    private String arrivalTime;
    private String departureAirport;
    private String arrivalAirport;
    private String airlineCode;
    private String airlineName;
    private String OperatingAirlineCode;
    private String OperatingAirlineName;
    private String Duration; //Duracion del vuelo
    private String FlightNumber;
    private String AircraftCode;
    private String AircraftName;

    public FlightSegments() {
    }

    public FlightSegments(String departureDate, String departureTime, String arrivalDate, String arrivalTime, String departureAirport, String arrivalAirport, String airlineCode, String airlineName, String operatingAirlineCode, String operatingAirlineName, String duration, String flightNumber, String aircraftCode, String aircraftName) {
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.airlineCode = airlineCode;
        this.airlineName = airlineName;
        OperatingAirlineCode = operatingAirlineCode;
        OperatingAirlineName = operatingAirlineName;
        Duration = duration;
        FlightNumber = flightNumber;
        AircraftCode = aircraftCode;
        AircraftName = aircraftName;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getOperatingAirlineCode() {
        return OperatingAirlineCode;
    }

    public void setOperatingAirlineCode(String operatingAirlineCode) {
        OperatingAirlineCode = operatingAirlineCode;
    }

    public String getOperatingAirlineName() {
        return OperatingAirlineName;
    }

    public void setOperatingAirlineName(String operatingAirlineName) {
        OperatingAirlineName = operatingAirlineName;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getFlightNumber() {
        return FlightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        FlightNumber = flightNumber;
    }

    public String getAircraftCode() {
        return AircraftCode;
    }

    public void setAircraftCode(String aircraftCode) {
        AircraftCode = aircraftCode;
    }

    public String getAircraftName() {
        return AircraftName;
    }

    public void setAircraftName(String aircraftName) {
        AircraftName = aircraftName;
    }
}
