package com.liveintent.weather.weatherservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveintent.weather.weatherservice.model.Coordinates;
import com.liveintent.weather.weatherservice.model.Forecast;
import com.liveintent.weather.weatherservice.model.FullDayForecast;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    // 6 because we get today, and then 5 days after that for forecast lookahead
    private static final int DAILY_RESULT_LIMIT = 6;
    private static final String CITY_NAME_KEY = "city_name";
    private static final String VALID_DATE_KEY = "valid_date";
    private static final String LAT_KEY = "lat";
    private static final String LON_KEY = "lon";
    private static final String DATA_KEY = "data";
    private static final String HUMIDITY_KEY = "rh";
    private static final String MAX_TEMP_KEY = "max_temp";
    private static final String MIN_TEMP_KEY = "min_temp";
    private static final String TEMP_KEY = "temp";
    private static final String WEATHER_KEY = "weather";
    private static final String ICON_KEY = "icon";
    private static final String DESCRIPTION_KEY = "description";
    private static final String WIND_SPEED_KEY = "wind_spd";
    private static final int LOOKAHEAD_COUNT = 5;
    private static final String API_ENDPOINT_BASE = "https://api.weatherbit.io/v2.0/forecast/daily?";

    /**
     * Transformation logic lives here - taking the HttpResponse and the JSON it contains and using
     * the data to construct our FullDayForecast model
     *
     * @param response HttpResponse<String> Response from calling external API
     *
     * @return FullDayForecast Forecast model that includes the five day lookahead data
     * @throws ParseException
     */
    private FullDayForecast parseWeatherbitForecast(HttpResponse<String> response) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            JSONObject rawForecastResponseObject = (JSONObject) parser.parse(response.body());
            // Validate the response object shape with our defined json schema, if it doesn't
            // pass validation, return error
            if(!this.validateSchema(rawForecastResponseObject)) {
                //@todo return error message back to frontend
                return null;
            }

            // Gather coordinates data
            Coordinates coordinates = new Coordinates();
            double latDouble = safelyExtractNumberValueAsDouble(rawForecastResponseObject, LAT_KEY);
            double lonDouble = safelyExtractNumberValueAsDouble(rawForecastResponseObject, LON_KEY);
            coordinates.setLatitude(latDouble);
            coordinates.setLongitude(lonDouble);

            // Gather city name
            String cityName = safelyExtractValueAsString(rawForecastResponseObject, CITY_NAME_KEY);

            // Pull out the list of forecasts as mainData
            // Grab relevant metadata for today
            JSONArray mainData = (JSONArray) rawForecastResponseObject.get(DATA_KEY);
            JSONObject today = (JSONObject) mainData.get(0);
            long humidity = this.safelyExtractNumberValueAsLong(today, HUMIDITY_KEY);
            double maxTemp = this.safelyExtractNumberValueAsDouble(today, MAX_TEMP_KEY);
            double minTemp = this.safelyExtractNumberValueAsDouble(today, MIN_TEMP_KEY);
            double rightNowTemp = this.safelyExtractNumberValueAsDouble(today,TEMP_KEY);

            // Pull out icon and description from attribute called "weather"
            JSONObject weatherBlock = (JSONObject) today.get(WEATHER_KEY);
            String icon = (String) weatherBlock.get(ICON_KEY);
            String desc = (String) weatherBlock.get(DESCRIPTION_KEY);

            // Grab wind speed
            double windSpeed = this.safelyExtractNumberValueAsDouble(today, WIND_SPEED_KEY);

            // Create the five day lookahead by pulling out each day from the mainData object
            // and using it to populate a Forecast model
            Forecast[] fiveDayLookahead = new Forecast[LOOKAHEAD_COUNT];
            Forecast tomorrow = this.parseIndividualForecast((JSONObject) mainData.get(1));
            Forecast twoDaysFromNow = this.parseIndividualForecast((JSONObject) mainData.get(2));
            Forecast threeDaysFromNow = this.parseIndividualForecast((JSONObject) mainData.get(3));
            Forecast fourDaysFromNow = this.parseIndividualForecast((JSONObject) mainData.get(4));
            Forecast fiveDaysFromNow = this.parseIndividualForecast((JSONObject) mainData.get(5));
            fiveDayLookahead[0] = tomorrow;
            fiveDayLookahead[1] = twoDaysFromNow;
            fiveDayLookahead[2] = threeDaysFromNow;
            fiveDayLookahead[3] = fourDaysFromNow;
            fiveDayLookahead[4] = fiveDaysFromNow;

            // Instantiate and populate the FullDayForecast model for returning
            FullDayForecast fore = new FullDayForecast();
            fore.setFiveDayForecast(fiveDayLookahead);
            fore.setHumidity(humidity);
            fore.setCoord(coordinates);
            fore.setIcon(icon);
            fore.setMinTemp(minTemp);
            fore.setMaxTemp(maxTemp);
            fore.setTemp(rightNowTemp);
            fore.setDescription(desc);
            fore.setWindSpeed(windSpeed);
            fore.setCityName(cityName);
            return fore;
        }
        catch (Exception e) {
            //@todo handle error
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Extract data from JSONObject for an individual forecast model, build out that
     * model and return it
     *
     * @param today JSONObject Object we want to pull forecast metadata from
     *
     * @return Forecast model we built
     */
    private Forecast parseIndividualForecast(JSONObject today) {
        JSONObject weatherBlock = (JSONObject) today.get(WEATHER_KEY);
        String desc = (String) weatherBlock.get(DESCRIPTION_KEY);

        double maxTemp = this.safelyExtractNumberValueAsDouble(today, MAX_TEMP_KEY);
        double minTemp = this.safelyExtractNumberValueAsDouble(today, MIN_TEMP_KEY);

        String validDate = this.safelyExtractValueAsString(today, VALID_DATE_KEY);

        Forecast forecast = new Forecast();
        forecast.setValidDate(validDate);
        forecast.setDescription(desc);
        forecast.setMaxTemp(maxTemp);
        forecast.setMinTemp(minTemp);
        return forecast;
    }

    /**
     * Fetch five day forecast from external API using "city" query parameter in the request
     *
     * @param city   String City we want to search for
     * @param apiKey String API key used to authenticate our app to external API
     * @param units  String Units we want our results in {"M", "I", "S"}
     *
     * @return FullDayForecast Returns FullDayForecast model
     */
    public FullDayForecast fetchFiveDayForecastByCity(String city, String apiKey, String units) {
        String requestUri =
                API_ENDPOINT_BASE +
                        "city=" +
                        city +
                        "&units=" +
                        units +
                        "&days=" +
                        DAILY_RESULT_LIMIT +
                        "&key=" + apiKey;
        String spaceReplacedUri = this.replaceSpaces(requestUri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(spaceReplacedUri))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            FullDayForecast forecast = this.parseWeatherbitForecast(response);
            return forecast;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
        return null;
    }

    /**
     * Replace spaces in a string with "%20"
     *
     * @param raw String input to modify by replacing spaces
     *
     * @return String result with spaces replaced
     */
    private String replaceSpaces(String raw) {
        try {
            return raw.replaceAll("\\x20", "%20");
        } catch (Exception e) {
            //@todo handle error
            return "";
        }
    }

    /**
     * Fetch five day forecast from external API using "lat" and "lon" query parameters in the request
     *
     * @param lat    String Latitude we want to search for
     * @param lon    String Longitude we want to search for
     * @param apiKey String API key used to authenticate our app to external API
     * @param units  String Units we want our results in {"M", "I", "S"}
     *
     * @return FullDayForecast Returns FullDayForecast model
     */
    public FullDayForecast fetchFiveDayForecastByCoords(String lat, String lon, String apiKey, String units) {
        String requestUri =
                API_ENDPOINT_BASE +
                        "lat=" +
                        lat +
                        "&lon=" +
                        lon +
                        "&units=" +
                        units +
                        "&days=" +
                        DAILY_RESULT_LIMIT +
                        "&key=" + apiKey;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUri))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            FullDayForecast forecast = this.parseWeatherbitForecast(response);
            return forecast;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body());
        return null;
    }

    /**
     * Validate that our JSON object from third party API is in the shape we expect for the rest of
     * our logic to work as expected!
     *
     * @param rawForecastResponseObject JSONObject Raw object we're validating against defined JSON schema
     *
     * @return Boolean true if object is in the shape we expect
     * @throws JsonProcessingException
     */
    private boolean validateSchema(JSONObject rawForecastResponseObject) throws JsonProcessingException {
        JsonSchema schema = getJsonSchema("#WeatherbitForecaseResponseSchema.json");
        Set<ValidationMessage> errors = schema.validate(new ObjectMapper().readTree(String.valueOf(rawForecastResponseObject)));
        return errors.isEmpty();
    }

    /**
     * Extract number value safely from JSONObject and cast as Double
     *
     * @param rawObject JSONObject Object we want to pull data from
     * @param key       String     Key to use to pull from JSONObject
     *
     * @return Double value we wanted
     */
    private double safelyExtractNumberValueAsDouble(JSONObject rawObject, String key) {
        double temp = 0;
        try {
            Object tempRaw = rawObject.get(key);
            if (tempRaw instanceof Double) {
                temp = (double) rawObject.get(key);
            } else if (tempRaw instanceof String) {
                temp = Double.parseDouble((String) tempRaw);
            } else if (tempRaw instanceof Long) {
                temp = Double.parseDouble(Long.toString((Long) tempRaw));
            }
            return temp;
        } catch(Exception e) {
            //@todo handle error
        }
        return temp;
    }

    /**
     * Extract number value safely from JSONObject and cast as Long
     *
     * @param rawObject JSONObject Object we want to pull data from
     * @param key       String     Key to use to pull from JSONObject
     *
     * @return Long value we wanted
     */
    private long safelyExtractNumberValueAsLong(JSONObject rawObject, String key) {
        long temp = 0;
        try {
            Object tempRaw = rawObject.get(key);
            if (tempRaw instanceof Double) {
                System.out.println("trying to cast double to long");
                Double tempAsDouble = (Double) rawObject.get(key);
                temp = Double.valueOf(tempAsDouble).longValue();
            } else if (tempRaw instanceof String) {
                temp = Long.parseLong((String) tempRaw);
            } else if (tempRaw instanceof Long) {
                temp = (long) tempRaw;
            }
            return temp;
        } catch(Exception e) {
            //@todo handle error
        }
        return temp;
    }

    /**
     * Extract string value from JSONObject or return empty string
     *
     * @param o   JSONObject Object we want to pull data from
     * @param key String     Key to use to pull from JSONObject
     *
     * @return String empty if unable to retrieve
     */
    private String safelyExtractValueAsString(JSONObject o, String key) {
        try {
            Object x = o.get(key);
            if (x instanceof String) {
                return (String) x;
            }
            return "";
        } catch(Exception e) {
            //@todo handle error
        }
        return "";
    }

    /**
     * Returns the entire JSON schema (which is really big) we want to validate our
     * API response against
     *
     * @param schemaId String ID used to fetch schema from factory
     *
     * @return JsonSchema returns the schema object
     */
    protected JsonSchema getJsonSchema(String schemaId) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        return factory.getSchema(
                "{\n" +
                        "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
                        "  \"$id\": \"#WeatherbitForecaseResponseSchema.json\",\n" +
                        "  \"type\": \"object\",\n" +
                        "  \"properties\": {\n" +
                        "    \"city_name\": {\n" +
                        "      \"type\": \"string\"\n" +
                        "    },\n" +
                        "    \"data\": {\n" +
                        "      \"type\": \"array\",\n" +
                        "      \"items\": [\n" +
                        this.getIndividualDayForecastSchema() + ", " +
                        this.getIndividualDayForecastSchema() + ", " +
                        this.getIndividualDayForecastSchema() + ", " +
                        this.getIndividualDayForecastSchema() + ", " +
                        this.getIndividualDayForecastSchema() + ", " +
                        this.getIndividualDayForecastSchema() +
                        "      ]\n" +
                        "    },\n" +
                        "    \"lat\": {\n" +
                        "      \"type\": [\"number\",\"string\"]" +
                        "    },\n" +
                        "    \"lon\": {\n" +
                        "      \"type\": [\"number\",\"string\"]" +
                        "    }\n" +
                        "  },\n" +
                        "  \"required\": [\n" +
                        "    \"city_name\",\n" +
                        "    \"data\",\n" +
                        "    \"lat\",\n" +
                        "    \"lon\"\n" +
                        "  ]\n" +
                        "}"
        );
    }

    /**
     * Pulled this part of the schema into its own method because the same text gets repeated
     *
     * @return String Schema shape
     */
    private String getIndividualDayForecastSchema() {
        return "{\n" +
                "          \"type\": \"object\",\n" +
                "          \"properties\": {\n" +
                "            \"max_temp\": {\n" +
                "              \"type\": \"number\"\n" +
                "            },\n" +
                "            \"min_temp\": {\n" +
                "              \"type\": \"number\"\n" +
                "            },\n" +
                "            \"rh\": {\n" +
                "              \"type\": \"number\"\n" +
                "            },\n" +
                "            \"temp\": {\n" +
                "              \"type\": \"number\"\n" +
                "            },\n" +
                "            \"valid_date\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"weather\": {\n" +
                "              \"type\": \"object\",\n" +
                "              \"properties\": {\n" +
                "                \"description\": {\n" +
                "                  \"type\": \"string\"\n" +
                "                },\n" +
                "                \"icon\": {\n" +
                "                  \"type\": \"string\"\n" +
                "                }\n" +
                "              },\n" +
                "              \"required\": [\n" +
                "                \"description\",\n" +
                "                \"icon\"\n" +
                "              ]\n" +
                "            },\n" +
                "            \"wind_spd\": {\n" +
                "              \"type\": \"number\"\n" +
                "            }\n" +
                "          },\n" +
                "          \"required\": [\n" +
                "            \"max_temp\",\n" +
                "            \"min_temp\",\n" +
                "            \"rh\",\n" +
                "            \"temp\",\n" +
                "            \"valid_date\",\n" +
                "            \"weather\",\n" +
                "            \"wind_spd\"\n" +
                "          ]\n" +
                "        }";
    }
}


