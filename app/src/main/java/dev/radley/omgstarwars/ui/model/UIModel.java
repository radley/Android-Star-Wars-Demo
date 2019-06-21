package dev.radley.omgstarwars.ui.model;

import dev.radley.omgstarwars.R;

public class UIModel {

    public static int getPlaceholderImage(String id) {
        switch (id) {
            case "films":
            case "people":
            case "species":
                return R.drawable.placeholder_tall;

            case "starships":
            case "vehicles":
                return R.drawable.placeholder_wide;
        }
        // planets & generic
        return R.drawable.placeholder_square;
    }

    public static int getFallbackImage(String id) {
        switch (id) {
            case "films":
                return R.drawable.placeholder_tall;
            case "people":
                return R.drawable.generic_people;
            case "planets":
                return R.drawable.generic_planet;
            case "species":
                return R.drawable.generic_species;
            case "starships":
                return R.drawable.generic_starship;
            case "vehicles":
                return R.drawable.generic_vehicle;
        }
        // generic
        return R.drawable.placeholder_square;
    }
}
