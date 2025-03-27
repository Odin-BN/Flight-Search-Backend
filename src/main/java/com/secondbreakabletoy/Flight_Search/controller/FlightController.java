package com.secondbreakabletoy.Flight_Search.controller;


import com.secondbreakabletoy.Flight_Search.model.FlightSearch;
import com.secondbreakabletoy.Flight_Search.services.FlightServices;
import org.springframework.web.bind.annotation.*;

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
    public String getFlightOffers(@RequestBody FlightSearch flightSearch){
        System.out.println(flightServices.getFlightOffers(flightSearch));
        return "Prueba1";
    }
}
