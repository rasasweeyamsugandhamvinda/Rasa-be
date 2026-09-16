package com.rasa.Rasa_be.modules.recommendation.utility;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class ClimateResolver {

    /*
     * V1: Static climate classification for major Indian cities.
     *
     * These classifications represent the dominant annual climate profile
     * from a fragrance recommendation perspective, not today's weather.
     *
     * Categories:
     *
     * tropical_humid
     * hot_humid
     * hot_semi_arid
     * hot_arid
     * subtropical
     * moderate_temperate
     * cool_temperate
     * mountain_cool
     *
     * V2 can replace this with a real weather/climate API.
     */

    private static final Set<String> TROPICAL_HUMID_CITIES = Set.of(
            // West coast
            "mumbai",
            "thane",
            "navi mumbai",
            "goa",
            "panaji",

            // Konkan / Maharashtra
            "ratnagiri",

            // South
            "kochi",
            "thiruvananthapuram",
            "thrissur",
            "kollam",
            "kozhikode",
            "kannur",

            "chennai",
            "pondicherry",
            "puducherry",

            "mangalore",
            "mangaluru",
            "udupi",

            // East / Northeast
            "kolkata",
            "howrah",
            "bhubaneswar",
            "cuttack",

            "visakhapatnam",
            "vijayawada",

            // Islands
            "port blair"
    );

    private static final Set<String> HOT_HUMID_CITIES = Set.of(
            // Eastern / central India
            "patna",
            "ranchi",
            "guwahati",
            "siliguri",

            // Telangana / Andhra
            "warangal",
            "tirupati",

            // Tamil Nadu
            "madurai",
            "coimbatore",
            "trichy",
            "tiruchirappalli",
            "salem",
            "tirunelveli",

            // Odisha / Bengal
            "durgapur",
            "asansol",

            // Chhattisgarh
            "raipur",
            "bilaspur",
            "durg",
            "bhilai"
    );

    private static final Set<String> HOT_SEMI_ARID_CITIES = Set.of(
            // Delhi NCR
            "delhi",
            "new delhi",
            "gurgaon",
            "gurugram",
            "noida",
            "ghaziabad",
            "faridabad",

            // Rajasthan
            "jaipur",
            "jodhpur",
            "udaipur",
            "kota",
            "ajmer",
            "bikaner",

            // Gujarat
            "ahmedabad",
            "vadodara",
            "rajkot",
            "surat",
            "gandhinagar",

            // Maharashtra
            "nagpur",
            "nashik",
            "aurangabad",
            "chhatrapati sambhajinagar",
            "solapur",

            // Telangana
            "hyderabad",
            "secunderabad",

            // Madhya Pradesh
            "bhopal",
            "indore",
            "gwalior",
            "jabalpur",
            "ujjain",

            // Karnataka / Deccan
            "hubli",
            "hubballi",
            "dharwad",
            "belgaum",
            "belagavi"
    );

    private static final Set<String> HOT_ARID_CITIES = Set.of(
            // Western Rajasthan
            "jaisalmer",
            "barmer",

            // Haryana / Punjab belt with strong hot-dry summers
            "hisar",
            "rohtak",
            "bhiwani",

            // Gujarat / Kutch
            "bhuj"
    );

    private static final Set<String> SUBTROPICAL_CITIES = Set.of(
            // North India
            "lucknow",
            "kanpur",
            "agra",
            "varanasi",
            "prayagraj",
            "allahabad",
            "meerut",
            "bareilly",
            "moradabad",

            // Punjab / Haryana
            "amritsar",
            "ludhiana",
            "jalandhar",
            "patiala",
            "chandigarh",
            "ambala",

            // Bihar / Jharkhand
            "gaya",
            "muzaffarpur",

            // West Bengal
            "darjeeling"
    );

    private static final Set<String> MODERATE_TEMPERATE_CITIES = Set.of(
            // Karnataka
            "bangalore",
            "bengaluru",
            "mysore",
            "mysuru",

            // Maharashtra
            "pune",
            "lonavala",
            "mahableshwar",

            // Karnataka hill regions
            "coorg",
            "madikeri",

            // Tamil Nadu highlands
            "ooty",
            "udhagamandalam",
            "kodaikanal",

            // Northeast
            "shillong",

            // Smaller but notable
            "nainital"
    );

    private static final Set<String> COOL_TEMPERATE_CITIES = Set.of(
            // Himachal Pradesh
            "shimla",
            "manali",
            "dharamshala",
            "mc leod ganj",
            "solan",

            // Uttarakhand
            "dehradun",
            "mussoorie",
            "almora",
            "ranikhet",

            // Kashmir
            "srinagar",

            // Sikkim
            "gangtok"
    );

    private static final Set<String> MOUNTAIN_COOL_CITIES = Set.of(
            // Higher altitude Himalayan regions
            "leh",
            "ladakh",
            "keylong",
            "spiti",

            // Arunachal Pradesh
            "tawang",

            // Kashmir high-altitude regions
            "gulmarg",
            "pahalgam"
    );

    /*
     * Optional state-level fallback.
     *
     * Useful when the city is missing or is not in our explicit city map.
     */
    private static final Map<String, String> STATE_DEFAULT_CLIMATES = Map.ofEntries(
            Map.entry("andhra pradesh", "hot_humid"),
            Map.entry("arunachal pradesh", "mountain_cool"),
            Map.entry("assam", "hot_humid"),
            Map.entry("bihar", "subtropical"),
            Map.entry("chhattisgarh", "hot_semi_arid"),
            Map.entry("goa", "tropical_humid"),
            Map.entry("gujarat", "hot_semi_arid"),
            Map.entry("haryana", "hot_semi_arid"),
            Map.entry("himachal pradesh", "cool_temperate"),
            Map.entry("jharkhand", "hot_humid"),
            Map.entry("karnataka", "moderate_temperate"),
            Map.entry("kerala", "tropical_humid"),
            Map.entry("madhya pradesh", "hot_semi_arid"),
            Map.entry("maharashtra", "hot_semi_arid"),
            Map.entry("manipur", "moderate_temperate"),
            Map.entry("meghalaya", "moderate_temperate"),
            Map.entry("mizoram", "moderate_temperate"),
            Map.entry("nagaland", "moderate_temperate"),
            Map.entry("odisha", "hot_humid"),
            Map.entry("punjab", "subtropical"),
            Map.entry("rajasthan", "hot_semi_arid"),
            Map.entry("sikkim", "cool_temperate"),
            Map.entry("tamil nadu", "hot_humid"),
            Map.entry("telangana", "hot_semi_arid"),
            Map.entry("tripura", "hot_humid"),
            Map.entry("uttar pradesh", "subtropical"),
            Map.entry("uttarakhand", "cool_temperate"),
            Map.entry("west bengal", "hot_humid"),

            // Union Territories
            Map.entry("delhi", "hot_semi_arid"),
            Map.entry("jammu and kashmir", "cool_temperate"),
            Map.entry("ladakh", "mountain_cool"),
            Map.entry("puducherry", "tropical_humid"),
            Map.entry("andaman and nicobar islands", "tropical_humid")
    );

    public String resolveClimate(String city, String state) {

        String normalizedCity = normalize(city);
        String normalizedState = normalize(state);

        if (!normalizedCity.isBlank()) {

            if (TROPICAL_HUMID_CITIES.contains(normalizedCity)) {
                return "tropical_humid";
            }

            if (HOT_HUMID_CITIES.contains(normalizedCity)) {
                return "hot_humid";
            }

            if (HOT_ARID_CITIES.contains(normalizedCity)) {
                return "hot_arid";
            }

            if (HOT_SEMI_ARID_CITIES.contains(normalizedCity)) {
                return "hot_semi_arid";
            }

            if (SUBTROPICAL_CITIES.contains(normalizedCity)) {
                return "subtropical";
            }

            if (MODERATE_TEMPERATE_CITIES.contains(normalizedCity)) {
                return "moderate_temperate";
            }

            if (COOL_TEMPERATE_CITIES.contains(normalizedCity)) {
                return "cool_temperate";
            }

            if (MOUNTAIN_COOL_CITIES.contains(normalizedCity)) {
                return "mountain_cool";
            }
        }

        /*
         * City unknown → use state fallback.
         */
        if (!normalizedState.isBlank()) {
            String stateClimate = STATE_DEFAULT_CLIMATES.get(normalizedState);

            if (stateClimate != null) {
                return stateClimate;
            }
        }

        /*
         * Final fallback for an unknown Indian location.
         *
         * Better than tropical_humid because it avoids assuming
         * extreme humidity for every unidentified Indian city.
         */
        return "subtropical";
    }

    private String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}