package com.secondbreakabletoy.Flight_Search.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondbreakabletoy.Flight_Search.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.cdimascio.dotenv.Dotenv;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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

        //////////////////////////////////////////////////////////////

        ObjectMapper objectMapper = new ObjectMapper();
        List<FlightModel> flights = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode dictionary = root.path("dictionaries");
            JsonNode dataArray = root.path("data");
            //JsonNode carriers = root.path("dictionaries").path("carriers");

            float num = 0; //para pruebas

            for (JsonNode flightNode : dataArray) {
                num = num + 1; //para pruebas

                FlightModel flight = new FlightModel();
                List<FlightSegments> flightsSegmentsList = new ArrayList<>();
                List<FlightPrices> flightsPricesList = new ArrayList<>();
                List<FlightItineraries> flightsInfoItinerary = new ArrayList<>();
                JsonNode itinerary = flightNode.path("itineraries");


                for (JsonNode itinerary_Seg : itinerary) {
                    FlightItineraries flightPerItinerary = new FlightItineraries();

                    flightPerItinerary.setTotalDuration(convertDuration(Duration.parse(itinerary_Seg.path("duration").asText())));

                    JsonNode Segments = itinerary_Seg.path("segments");
                    List<String> waitTimes = new ArrayList<>();

                    if (Segments.size() == 2) {
                        String itinerary_dur = itinerary_Seg.path("duration").asText();
                        String seg1_dur = Segments.get(0).path("duration").asText();
                        String seg2_dur = Segments.get(1).path("duration").asText();

                        String resultWaitTime = differenceDurations(itinerary_dur, seg1_dur, seg2_dur);

                        waitTimes.add(resultWaitTime);

                    } else {
                        waitTimes.add("0");
                    }

                    for (JsonNode flightSegments : Segments) {
                        FlightSegments flightSeg = new FlightSegments();

                        flightSeg.setDepartureDate(flightSegments.path("departure").path("at").asText().split("T")[0]);
                        flightSeg.setDepartureTime(flightSegments.path("departure").path("at").asText().split("T")[1]);
                        flightSeg.setArrivalDate(flightSegments.path("arrival").path("at").asText().split("T")[0]);
                        flightSeg.setArrivalTime(flightSegments.path("arrival").path("at").asText().split("T")[1]);
                        flightSeg.setDepartureAirport(flightSegments.path("departure").path("iataCode").asText());
                        flightSeg.setArrivalAirport(flightSegments.path("arrival").path("iataCode").asText());
                        flightSeg.setAirlineCode(flightSegments.path("carrierCode").asText());
                        flightSeg.setAirlineName(dictionary.path("carriers").path(flightSeg.getAirlineCode()).asText());
                        flightSeg.setOperatingAirlineCode(flightSegments.path("operating").path("carrierCode").asText());
                        flightSeg.setOperatingAirlineName(dictionary.path("carriers").path(flightSeg.getAirlineCode()).asText());

                        /*String dur = flightSegments.path("duration").asText();
                        Duration durTime = Duration.parse(dur);
                        long hours = durTime.toHours();
                        long minutes = durTime.toMinutes();*/
                        flightSeg.setDuration(convertDuration(Duration.parse(flightSegments.path("duration").asText())));
                        //convierte la duracion del formato PT8H15M --> 8h 15m

                        flightSeg.setFlightNumber(flightSegments.path("number").asText());
                        flightSeg.setAircraftCode(flightSegments.path("aircraft").path("code").asText());
                        flightSeg.setAircraftName(dictionary.path("aircraft").path(flightSeg.getAircraftCode()).asText());

                        flightsSegmentsList.add(flightSeg);
                    }

                    flightPerItinerary.setFlightSegments(flightsSegmentsList); //Agrega la lista de segmentos del vuelo por oferta
                    flightPerItinerary.setWaitTime(waitTimes); //Agrega la lista de los tiempos de espera de cada segmento del itinerario
                    //fli.setFlightSegments(flightsSegmentsList);
                    flightsInfoItinerary.add(flightPerItinerary); // Agregar el intinerario actual a la lista de itinerarios que luego se agrega al modelo del vuelo
                }

                flight.setInfoPerItinerary(flightsInfoItinerary); //se agrega la lista de itinerarios al vuelo

                JsonNode pricesBySegment = flightNode.path("travelerPricings").get(0).path("fareDetailsBySegment"); //revisar si el get(0) esta bien

                for (JsonNode PriceSegment : pricesBySegment) {
                    FlightPrices flightPrices = new FlightPrices();
                    //los precios por segmento
                    flightPrices.setCabinType(PriceSegment.path("cabin").asText());
                    flightPrices.setClassType(PriceSegment.path("class").asText());
                    flightPrices.setCheckedBagsWeight(PriceSegment.path("includedCheckedBags").path("weight").asText());
                    flightPrices.setCheckedBagsUnit(PriceSegment.path("includedCheckedBags").path("weightUnit").asText());

                    //Agregar las diferente ammenities

                    flightsPricesList.add(flightPrices);
                }

                flight.setFlightPrices(flightsPricesList); //Agrega la lista de precios de los segmentos de los vuelos de la oferta

                //Imprimir un valor para probar
                //System.out.println("Fecha de salida de prueba: " + flight.getDepartureDate_first());

                //Precio del vuelo en general
                //flight.setTotalFlightTime(itinerary.path("duration").asText());
                flight.setTotalPrice(flightNode.path("price").path("grandTotal").asDouble());
                flight.setPricePerTraveler(flightNode.path("travelerPricings").get(0).path("price").path("total").asDouble());
                flight.setBasePrice(flightNode.path("travelerPricings").get(0).path("price").path("base").asDouble());

                flights.add(flight);

                //de prueba para checar como se estan guardando los vuelos
                ObjectMapper objectMapper1 = new ObjectMapper();
                try {
                    String flight_json = objectMapper1.writerWithDefaultPrettyPrinter().writeValueAsString(flight);
                    System.out.println(flight_json);
                } catch (JsonProcessingException e) {
                    System.out.println("Error imprimiendo el JSON");
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

    public String differenceDurations(String itinerary_dur, String seg1_dur, String seg2_dur) {
        Duration result = Duration.parse(itinerary_dur);

        result = result.minus(Duration.parse(seg1_dur));
        result = result.minus(Duration.parse(seg2_dur));

        return convertDuration(result);
    }

    public String convertDuration(Duration dur) {
        long hours = dur.toHours();
        long minutes = dur.toMinutes();

        return (hours > 0 ? hours + "h " : "") + (minutes > 0? minutes + "m " :"");
    }

}
