package com.secondbreakabletoy.Flight_Search.services;

import com.secondbreakabletoy.Flight_Search.model.FlightSearch;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;


@Service
public class FlightServices {

    Dotenv dotenv = Dotenv.load();

    private final WebClient webClient;
    private final String api_key = dotenv.get("API_KEY");
    private final String api_secret = dotenv.get("API_SECRET");
    private final String AUTH_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";
    private final String LOCATIONS_URL = "https://test.api.amadeus.com/v1/reference-data/locations";
    private final String AIRLINES_URL = "https://test.api.amadeus.com/v1/reference-data/airlines";
    private final String FLIGHT_OFFERS_URL = "https://test.api.amadeus.com/v2/shopping/flight-offers";

    public FlightServices(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl(AUTH_URL).build();
    }

    public String getAccessToken(){
        try {
            String body = "grant_type=client_credentials&client_id=" + api_key + "&client_secret=" + api_secret;
            /*String body = "grant_type=client_credentials&client_id=" +
                    URLEncoder.encode(API_KEY, StandardCharsets.UTF_8) +
                    "&client_secrets=" +
                    URLEncoder.encode(API_SECRET, StandardCharsets.UTF_8);*/

            return webClient.post()
                    .uri("")
                    .header("Content-type","application/x-www-form-urlencoded")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> response.get("access_token").toString())
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error obteniendo el token: " + e.getResponseBodyAsString());
        }
    }


    public String getLocations(String keyword){
        String token = getAccessToken();

        try {
            WebClient client = WebClient.builder()
                    .baseUrl(LOCATIONS_URL)
                    .defaultHeader("Authorization", "Bearer " + token)
                    .build();
            return client.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("keyword", keyword)
                            .queryParam("subType", "CITY,AIRPORT")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error obteniendo ubicaciones: " + e.getResponseBodyAsString());
        }
    }

    public String getLocationByID(String id) {
        String token = getAccessToken();

        try {
            WebClient client = WebClient.builder()
                    .baseUrl(LOCATIONS_URL)
                    .defaultHeader("Authorization", "Bearer " + token)
                    .build();
            return client.get()
                    .uri(String.format("/%s", id))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error obteniendo la ubicacion del id: " + e.getResponseBodyAsString());
        }
    }

    public String getNameByCode(String airlineCodes) {
        String token = getAccessToken();

        try {
            WebClient client = WebClient.builder()
                    .baseUrl(AIRLINES_URL)
                    .defaultHeader("Authorization", "Bearer " + token)
                    .build();
            return client.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("airlineCodes", airlineCodes).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error obteniendo el nombre por el codigo: " + e.getResponseBodyAsString());
        }
    }

    public String getFlightOffers(FlightSearch flightSearch){
        String token = getAccessToken();

        String FOS_URL = "?originLocationCode=" + flightSearch.getOriginLocationCode()
                + "&destinationLocationCode=" + flightSearch.getDestinationLocationCode()
                + "&departureDate=" + flightSearch.getDepartureDate();
        if (flightSearch.getReturnDate() != null){
            FOS_URL = FOS_URL + "&returnDate=" + flightSearch.getReturnDate();
        }
        FOS_URL = FOS_URL + "&adults=" + flightSearch.getAdults();

        if (flightSearch.getNonStop()){
            FOS_URL = FOS_URL + "&nonStop=true";
        } else {
            FOS_URL = FOS_URL + "&nonStop=false";
        }

        FOS_URL = FOS_URL + "&currencyCode=" + flightSearch.getCurrencyCode();

        try {
            WebClient client = WebClient.builder()
                    .baseUrl(FLIGHT_OFFERS_URL)
                    .defaultHeader("Authorization", "Bearer " + token)
                    .build();
            return client.get()
                    .uri(FOS_URL)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error obteniendo las busquedas disponibles: " + e.getResponseBodyAsString());
        }
    }

}
