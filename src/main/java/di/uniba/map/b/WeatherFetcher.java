package di.uniba.map.b;

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

    private final static String URL_GET_LOCATION =
            "http://dataservice.accuweather.com/locations/v1/cities/ipaddress";

    private final static String URL_GET_WEATHER =
            "http://dataservice.accuweather.com/currentconditions/v1/";

    private final static String API_KEY = "XUmLuDSS5WHRBFaGkfuMqJXo0txjadwQ";

    private final static String DEFAULT_LOCATION_KEY = "214964";

    private final static String URL_GET_IP = "http://checkip.amazonaws.com/";

    private final static Client CLIENT = ClientBuilder.newClient();

    private static String locationKey = "";

    private static long latestFetchTime = 0;

    private static boolean lastFetchRaining = false;

    private static void fetchLocationKey() {
        if (locationKey.isEmpty()) {
            String ipAddress = "";

            try {
                URL url = new URL(URL_GET_IP);
                try (BufferedReader br =
                        new BufferedReader(new InputStreamReader(url.openStream()))) {
                    ipAddress = br.readLine();
                }

                // If no exceptions are thrown above, continue
                WebTarget target = CLIENT.target(URL_GET_LOCATION);
                Response response =
                        target.queryParam("apikey", API_KEY).queryParam("q", ipAddress)
                                .request(MediaType.APPLICATION_JSON).get();

                if (response.getStatus() == 200) {
                    String json = response.readEntity(String.class);
                    locationKey = getJsonField(json, "Key").getAsString();
                }
            } catch (Exception e) {
                locationKey = DEFAULT_LOCATION_KEY;
            }
        }
    }

    public static boolean isRaining() {
        fetchLocationKey();

        if (latestFetchTime == 0 || (System.currentTimeMillis() - latestFetchTime == 1800000)) {
            WebTarget target = CLIENT.target(URL_GET_WEATHER + locationKey);

            try {
                Response response = target.queryParam("apikey", API_KEY).queryParam("details", true)
                        .request(MediaType.APPLICATION_JSON).get();
                target.queryParam("apikey", API_KEY).queryParam("details", true)
                        .request(MediaType.APPLICATION_JSON).get();

                if (response.getStatus() == 200) {
                    latestFetchTime = System.currentTimeMillis();
                    String json = response.readEntity(String.class);
                    lastFetchRaining = getJsonField(json, "HasPrecipitation").getAsBoolean();
                }
            } catch (Exception e) {
                // Set default value
                lastFetchRaining = false;
            }
        }

        return lastFetchRaining;
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
