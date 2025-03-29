package com.secondbreakabletoy.Flight_Search.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondbreakabletoy.Flight_Search.model.FlightModel;
import com.secondbreakabletoy.Flight_Search.model.FlightSearch;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;
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
    private String jsonResponse;

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

    public List<FlightModel> getFlightOffers(FlightSearch flightSearch){
        String token = getAccessToken();
        List<FlightModel> flights = new ArrayList<>();

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

            jsonResponse = client.get()
                    .uri(FOS_URL)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            //System.out.println(jsonResponse); //para checar la respuesta de la API

        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error obteniendo las busquedas disponibles: " + e.getResponseBodyAsString());
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode dataArray = root.path("data");
            JsonNode carriers = root.path("dictionaries").path("carriers");

            float num = 0; //para pruebas

            for (JsonNode flightNode : dataArray) {
                num = num + 1; //para pruebas

                FlightModel flight = new FlightModel();
                JsonNode itinerary = flightNode.path("itineraries").get(0);
                JsonNode firstSegment = itinerary.path("segments").get(0);

                //Crear una funcion que haga lo de abajo para la cantidad de vuelos que detecte, condiciones por si son 3, que cheque cual tiene escala y cual no

                //Extraer la informacion para cada propiedad que se pide de los vuelos, esto es para el primer vuelo
                flight.setDepartureDate_first(firstSegment.path("departure").path("at").asText().split("T")[0]);
                //System.out.println("Fecha de salida de prueba: " + flight.getDepartureDate_first());
                flight.setDepartureTime_first(firstSegment.path("departure").path("at").asText().split("T")[1]);
                flight.setArrivalDate_first(firstSegment.path("arrival").path("at").asText().split("T")[0]);
                flight.setArrivalTime_first(firstSegment.path("arrival").path("at").asText().split("T")[1]);
                flight.setDepartureAirport_first(firstSegment.path("departure").path("iataCode").asText());
                flight.setArrivalAirport_first(firstSegment.path("arrival").path("iataCode").asText());
                flight.setAirlineCode_first(firstSegment.path("carrierCode").asText());
                flight.setAirlineName_first(carriers.path(flight.getAirlineCode_first()).asText());


                //Informacion del segundo vuelo si es que existe debido a escala
                if (!flightSearch.getNonStop()) {
                    JsonNode secondSegment = itinerary.path("segments").get(1);

                    flight.setDepartureDate_second(secondSegment.path("departure").path("at").asText().split("T")[0]);
                    flight.setDepartureTime_second(secondSegment.path("departure").path("at").asText().split("T")[1]);
                    flight.setArrivalDate_second(secondSegment.path("arrival").path("at").asText().split("T")[0]);
                    flight.setArrivalTime_second(secondSegment.path("arrival").path("at").asText().split("T")[1]);
                    flight.setDepartureAirport_second(secondSegment.path("departure").path("iataCode").asText());
                    flight.setArrivalAirport_second(secondSegment.path("arrival").path("iataCode").asText());
                    flight.setAirlineCode_second(secondSegment.path("carrierCode").asText());
                    flight.setAirlineName_second(carriers.path(flight.getAirlineCode_second()).asText());
                }

                //Precio del vuelo y por persona junto con equipaje y eso
                flight.setTotalFlightTime(itinerary.path("duration").asText());
                flight.setTotalPrice(flightNode.path("price").path("grandTotal").asDouble());
                flight.setPricePerTraveler(flightNode.path("travelerPricings").get(0).path("price").path("total").asDouble());

                flights.add(flight);

                //de prueba para checar como se estan guardando los vuelos
                ObjectMapper objectMapper1 = new ObjectMapper();
                try {
                    String flight_json = objectMapper1.writerWithDefaultPrettyPrinter().writeValueAsString(flight);
                    System.out.println(flight_json);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                //private List<String> segmentDurations;
                //private List<String> layoverTimes;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Error al procesar el JSON");
        }


        return flights;

    }

}
