package com.secondbreakabletoy.Flight_Search.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondbreakabletoy.Flight_Search.model.*;
import com.sun.jdi.IntegerValue;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


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
    private List<FlightModel> flights = new ArrayList<>();
    private final Map<String, Airport> airports = new HashMap<>();

    public FlightServices(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl(AUTH_URL).build();
    }

    @PostConstruct
    public void init() {
        loadAirports();
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

    public void getFlightOffers(FlightSearch flightSearch){
        String token = getAccessToken();
        //System.out.println(flightSearch.getReturnDate());

        String FOS_URL = "?originLocationCode=" + flightSearch.getOriginLocationCode()
                + "&destinationLocationCode=" + flightSearch.getDestinationLocationCode()
                + "&departureDate=" + flightSearch.getDepartureDate();
        if (!Objects.equals(flightSearch.getReturnDate(), "")){
            FOS_URL = FOS_URL + "&returnDate=" + flightSearch.getReturnDate();
        }
        FOS_URL = FOS_URL + "&adults=" + flightSearch.getAdults();

        if (flightSearch.getNonStop()){
            FOS_URL = FOS_URL + "&nonStop=true";
        } else {
            FOS_URL = FOS_URL + "&nonStop=false";
        }

        FOS_URL = FOS_URL + "&currencyCode=" + flightSearch.getCurrencyCode() + "&max=50";

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
        //List<FlightModel> flights = new ArrayList<>(); reiniciarlo cada que se llama
        flights = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode dictionary = root.path("dictionaries");
            JsonNode dataArray = root.path("data");
            //JsonNode carriers = root.path("dictionaries").path("carriers");

            float num = 0; //para pruebas

            for (JsonNode flightNode : dataArray) {
                num = num + 1; //para pruebas

                FlightModel flight = new FlightModel();

                flight.setFlight_id(String.valueOf(num));

                List<FlightPrices> flightsPricesList = new ArrayList<>();
                List<FlightItineraries> flightsInfoItinerary = new ArrayList<>();
                List<FlightAmenities> flightAmenitiesList = new ArrayList<>();
                JsonNode amenities = flightNode.path("travelerPricings").get(0).path("fareDetailsBySegment").get(0).path("amenities");
                JsonNode itinerary = flightNode.path("itineraries");

                float iti_id = 1;

                //amenities
                for (JsonNode amenities_seg : amenities) {
                    FlightAmenities flightAmenities = new FlightAmenities();

                    flightAmenities.setDescription(amenities_seg.path("description").asText());
                    flightAmenities.setChargeable(amenities_seg.path("isChargeable").asBoolean());

                    flightAmenitiesList.add(flightAmenities);
                }

                int seg_id = 1;

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

                for (JsonNode itinerary_Seg : itinerary) {
                    FlightItineraries flightPerItinerary = new FlightItineraries();

                    List<FlightSegments> flightsSegmentsList = new ArrayList<>();

                    flightPerItinerary.setItinerate_id(String.valueOf(iti_id));
                    iti_id = iti_id + 1;
                    flightPerItinerary.setTotalDuration(convertDuration(Duration.parse(itinerary_Seg.path("duration").asText())));

                    JsonNode Segments = itinerary_Seg.path("segments");

                    if (Segments.size() == 2) {
                        String itinerary_dur = itinerary_Seg.path("duration").asText();
                        String seg1_dur = Segments.get(0).path("duration").asText();
                        String seg2_dur = Segments.get(1).path("duration").asText();

                        String resultWaitTime = differenceDurations(itinerary_dur, seg1_dur, seg2_dur);

                        flightPerItinerary.setWaitTime(resultWaitTime);

                    } else {
                        flightPerItinerary.setWaitTime("0");
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
                        //checar porque al parecer esta mal
                        flightSeg.setOperatingAirlineName(dictionary.path("carriers").path(flightSeg.getOperatingAirlineCode()).asText());

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
                        flightSeg.setFlightAmenities(flightAmenitiesList);
                        flightSeg.setId(seg_id);
                        seg_id = seg_id + 1;
                        flightSeg.setFlightPrices(flightsPricesList);
                    }

                    flightPerItinerary.setFlightSegments(flightsSegmentsList); //Agrega la lista de segmentos del vuelo por oferta
                    //fli.setFlightSegments(flightsSegmentsList);
                    flightsInfoItinerary.add(flightPerItinerary); // Agregar el intinerario actual a la lista de itinerarios que luego se agrega al modelo del vuelo
                }

                flight.setInfoPerItinerary(flightsInfoItinerary); //se agrega la lista de itinerarios al vuelo



                //Imprimir un valor para probar
                //System.out.println("Fecha de salida de prueba: " + flight.getDepartureDate_first());

                //Precio del vuelo en general
                //flight.setTotalFlightTime(itinerary.path("duration").asText());
                flight.setTotalPrice(flightNode.path("price").path("grandTotal").asDouble());
                flight.setPricePerTraveler(flightNode.path("travelerPricings").get(0).path("price").path("total").asDouble());
                flight.setBasePrice(flightNode.path("travelerPricings").get(0).path("price").path("base").asDouble());

                flights.add(flight);

                //imprimir los vuelos para checar como se estan guardando los datos
                /*ObjectMapper objectMapper1 = new ObjectMapper();
                try {
                    String flight_json = objectMapper1.writerWithDefaultPrettyPrinter().writeValueAsString(flight);
                    System.out.println(flight_json);
                } catch (JsonProcessingException e) {
                    System.out.println("Error imprimiendo el JSON");
                    throw new RuntimeException(e);
                }*/
                //private List<String> segmentDurations;
                //private List<String> layoverTimes;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Error al procesar el JSON");
        }

        //sortFlightsList("sortBy1", "order1", "sortBy2", "order2", 1, 4 );
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

    public void sortFlightsList(String sortBy1, String order1, String sortBy2, String order2) {
        Comparator<FlightModel> comparator1 = getComparison(sortBy1, order1);
        Comparator<FlightModel> comparator2 = getComparison(sortBy2, order2);

        flights.sort(comparator1.thenComparing(comparator2));
    }

    private Comparator<FlightModel> getComparison(String sortBy, String order) {
        Comparator<FlightModel> comparator = null;

        if ("price".equals(sortBy)) {
            comparator = Comparator.comparing(FlightModel :: getTotalPrice);
        } else if ("duration".equals(sortBy)) {
            comparator = Comparator.comparing(flight -> Duration.parse(flight.getInfoPerItinerary().get(0).getTotalDuration()).toMinutes());
        }

        if ("desc".equals(order) && comparator != null) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    public List<FlightModel> getPaginatedFlights(int page, int pageSize) {
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(page * pageSize, flights.size());

        if (fromIndex >= flights.size()) {
            return Collections.emptyList();
        }

        //agregar que busque el nombre de los vuelos paginados

        List<FlightModel> flights_page = flights.subList(fromIndex, toIndex);

        List<FlightModel> flights_page1 = setNames(flights_page);
        //Imprimir la lista paginada para checar que este bien
        ObjectMapper objectMapper1 = new ObjectMapper();
        try {
            String flight_json = objectMapper1.writerWithDefaultPrettyPrinter().writeValueAsString(flights_page1);
            System.out.println(flight_json);
        } catch (JsonProcessingException e) {
            System.out.println("Error imprimiendo el JSON");
            throw new RuntimeException(e);
        }

        return flights_page1;
    }

    private List<FlightModel> setNames(List<FlightModel> flights_page) {
        for (FlightModel flightModel : flights_page) {
            for (FlightItineraries flightItineraries : flightModel.getInfoPerItinerary()) {
                for (FlightSegments flightSegments : flightItineraries.getFlightSegments()) {

                    String departureIata = flightSegments.getDepartureAirport();
                    String arrivalIata = flightSegments.getArrivalAirport();

                    Airport departureAirport = airports.get(departureIata);
                    Airport arrivalAirport = airports.get(arrivalIata);

                    if (departureAirport != null) {
                        flightSegments.setDepartureAirportName(departureAirport.getName());
                        flightSegments.setDepartureCity(departureAirport.getCity());
                    }

                    if (arrivalAirport != null) {
                        flightSegments.setArrivalAirportName(arrivalAirport.getName());
                        flightSegments.setArrivalCity(arrivalAirport.getCity());
                    }
                }
            }
        }
        return flights_page;
    }

    //de momento ya no lo uso
    private String cityNameByAirport(String airportCode) {
        String response = getLocationByID(airportCode);
        ObjectMapper objectMapper = new ObjectMapper();
        String CityName;

        try {
            JsonNode root = objectMapper.readTree(response);
            CityName = root.path("data").path("address").path("cityName").asText();
            //accede a la ruta del nombre de la ciudad en base el codigo del aeropuerto


        } catch (JsonProcessingException e) {
            System.out.println("Error obteniendo los nombres de las ciudades");
            throw new RuntimeException(e);
        }

        return CityName;
    }

    private void loadAirports(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/airportData.json");


            Map<String, Map<String, String>> data = objectMapper.readValue(
                    inputStream, new TypeReference<Map<String, Map<String, String>>>() {
                    }
            );

            for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
               Map<String, String> airportData = entry.getValue();
               airports.put(
                       airportData.get("iata"),
                       new Airport(
                               airportData.get("iata"),
                               airportData.get("name"),
                               airportData.get("city")
                       )
               );
            }
        } catch (Exception e) {
            System.out.println("Error cargando los aeropuertos");
            throw new RuntimeException(e);
        }

    };

    public List<Airport> searchAirports(String query) {
        if(query == null || query.isEmpty()) {
            return Collections.emptyList();
        }

        String lowerQuery = query.toLowerCase();
        return airports.values().stream()
                .filter(a -> a.getIata().toLowerCase().contains(lowerQuery) ||
                        a.getCity().toLowerCase().contains(lowerQuery))
                .limit(3)
                .collect(Collectors.toList());
    }

}
