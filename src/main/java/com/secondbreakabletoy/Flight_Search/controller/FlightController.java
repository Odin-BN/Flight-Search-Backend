package com.secondbreakabletoy.Flight_Search.controller;


import com.secondbreakabletoy.Flight_Search.model.FlightModel;
import com.secondbreakabletoy.Flight_Search.model.FlightSearch;
import com.secondbreakabletoy.Flight_Search.services.FlightServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/amadeus")
public class FlightController {

    private final FlightServices flightServices;

    public FlightController(FlightServices flightServices) {
        this.flightServices = flightServices;
    }

    @GetMapping("/locations")
    public String getLocations(@RequestParam String keyword) {
        return flightServices.getLocations(keyword);
    }

    @GetMapping("/locations/{id}")
    public String getLocationsByID(@PathVariable String id){
        return flightServices.getLocationByID(id);
    }

    @GetMapping("/airlines")
    public String getNameByCode(@RequestParam String airlineCodes) {
        return flightServices.getNameByCode(airlineCodes);
    }

    @PostMapping("/FlightSearch")
    public ResponseEntity<Void> setFlightOffers(@RequestBody FlightSearch flightSearch){
        flightServices.getFlightOffers(flightSearch);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/Flights")
    public ResponseEntity<Void> sortFlights(@RequestParam(required = false, defaultValue = "price") String sortBy1,
                                        @RequestParam(required = false, defaultValue = "asc") String order1,
                                        @RequestParam(required = false, defaultValue = "duration") String sortBy2,
                                        @RequestParam(required = false, defaultValue = "asc") String order2) {
        flightServices.sortFlightsList(sortBy1, order1, sortBy2, order2);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/Flights")
    public List<FlightModel> getFlights(@RequestParam(defaultValue = "1") String page) {

        return flightServices.getPaginatedFlights(Integer.parseInt(page), 4);

    }
}
