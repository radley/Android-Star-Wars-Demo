package dev.radley.omgstarwars.data;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Oleur on 22/12/2014.
 * Vehicle model represents a single transport craft that does not have hyperdrive capability.
 */
public class Vehicle extends SWModel implements Serializable {

    public String model;
    public String manufacturer;
    public String length;
    public String crew;
    public String passengers;
    public String consumables;

    @SerializedName("vehicle_class")
    public String vehicleClass;

    @SerializedName("cost_in_credits")
    public String costInCredits;

    @SerializedName("max_atmosphering_speed")
    public String maxAtmospheringSpeed;

    @SerializedName("cargo_capacity")
    public String cargoCapacity;

    @SerializedName("pilots")
    public ArrayList<String> pilotsUrls;

    @SerializedName("films")
    public ArrayList<String> filmsUrls;

    @Override
    public ArrayList<String> getPeople() {
        return pilotsUrls;
    }

    @Override
    public ArrayList<String> getFilms() {
        return filmsUrls;
    }

    @Override
    public String getRelatedPeopleTitle() {
        return "Pilots";
    }
}