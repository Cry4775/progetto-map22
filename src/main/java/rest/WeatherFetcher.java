package rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class WeatherFetcher {

    private static final String URL_GET_LOCATION =
            "http://dataservice.accuweather.com/locations/v1/cities/ipaddress";

    private static final String URL_GET_WEATHER =
            "http://dataservice.accuweather.com/currentconditions/v1/";

    private static final String API_KEY = "XUmLuDSS5WHRBFaGkfuMqJXo0txjadwQ";

    private static final String DEFAULT_LOCATION_KEY = "214964";

    private static final String URL_GET_IP = "http://checkip.amazonaws.com/";

    private static final Client CLIENT = ClientBuilder.newClient();

    private static String locationKey = "";

    private static final int FETCH_INTERVAL = 1800000;

    private static long latestFetchTime = 0;

    private static boolean latestFetchRaining = false;

    private static State state;

    public static State getState() {
        return state;
    }

    static {
        state = State.FETCHING_IP;
        try {
            String ipAddress = "";
            URL url = new URL(URL_GET_IP);
            try (BufferedReader br =
                    new BufferedReader(new InputStreamReader(url.openStream()))) {
                ipAddress = br.readLine();
            }

            // If no exceptions are thrown above, continue
            state = State.FETCHING_LOCATION_KEY;
            WebTarget target = CLIENT.target(URL_GET_LOCATION);
            Response response =
                    target.queryParam("apikey", API_KEY).queryParam("q", ipAddress)
                            .request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 200) {
                String json = response.readEntity(String.class);
                locationKey = getJsonField(json, "Key").getAsString();
            } else {
                locationKey = DEFAULT_LOCATION_KEY;
            }
        } catch (Exception e) {
            state = State.FETCHING_LOCATION_KEY;
            locationKey = DEFAULT_LOCATION_KEY;
        }
    }

    public enum State {
        FETCHING_IP,
        FETCHING_LOCATION_KEY,
        FETCHING_WEATHER,
        DONE
    }

    public static boolean isRaining() {
        if (latestFetchTime == 0 || (System.currentTimeMillis() - latestFetchTime >= FETCH_INTERVAL)) {
            state = State.FETCHING_WEATHER;
            WebTarget target = CLIENT.target(URL_GET_WEATHER + locationKey);

            try {
                Response response = target.queryParam("apikey", API_KEY).queryParam("details", true)
                        .request(MediaType.APPLICATION_JSON).get();
                target.queryParam("apikey", API_KEY).queryParam("details", true)
                        .request(MediaType.APPLICATION_JSON).get();

                if (response.getStatus() == 200) {
                    String json = response.readEntity(String.class);
                    latestFetchRaining = getJsonField(json, "HasPrecipitation").getAsBoolean();
                }
            } catch (Exception e) {
                // Set default value
                latestFetchRaining = false;
            }
        }

        state = State.DONE;
        latestFetchTime = System.currentTimeMillis();
        return latestFetchRaining;
    }

    private static JsonElement getJsonField(String json, String fieldName) {
        JsonElement element = JsonParser.parseString(json);

        if (element.isJsonObject()) {
            return element.getAsJsonObject().get(fieldName);
        } else if (element.isJsonArray()) {
            if (!element.getAsJsonArray().isEmpty())
                return element.getAsJsonArray().get(0).getAsJsonObject().get(fieldName);
        }

        return null;
    }
}
