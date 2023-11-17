package com.liveintent.weather.weatherservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liveintent.weather.weatherservice.model.Coordinates;
import com.liveintent.weather.weatherservice.model.Forecast;
import com.liveintent.weather.weatherservice.model.FullDayForecast;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class WeatherService {
    // 6 because we get today, and then 5 days after that for forecast lookahead
    private static final int DAILY_RESULT_LIMIT = 6;
    private static final String CITY_NAME_KEY = "city_name";
    private static final String VALID_DATE_KEY = "valid_date";
    @Autowired
    private RestTemplate template = new RestTemplate();

    /**public FullDayForecast findForecastByCoordinates(double lat, double lon, String apiKey) {
        return template.getForObject("https://api.openweathermap.org/data/2.5/weather?lat=40.67&lon=73.98&appid=a4b02892fa24ceb05260687cde51496e", FullDayForecast.class);
    }*/


    private String safelyExtractValueAsString(JSONObject o, String key) {
        Object x = o.get(key);
        if (x instanceof String) {
            return (String) x;
        }
        return "";
    }
    private FullDayForecast parseWeatherbitForecast(HttpResponse<String> response) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            JSONObject rawForecastResponseObject = (JSONObject) parser.parse(response.body());
            //@todo hpaup delete FullDayForecastSchema.json if not using it!
            if(!this.validateSchema(rawForecastResponseObject)) {
                //@todo hpaup return error message back to frontend
                return null;
            }

            //@todo hpaup consider just storing and returning these as strings and getting rid of all this
            Coordinates coordinates = new Coordinates();

            double latDouble = safelyExtractNumberValueAsDouble(rawForecastResponseObject, "lat");
            double lonDouble = safelyExtractNumberValueAsDouble(rawForecastResponseObject, "lon");
            coordinates.setLatitude(latDouble);
            coordinates.setLongitude(lonDouble);

            String cityName = safelyExtractValueAsString(rawForecastResponseObject, CITY_NAME_KEY);

            JSONArray mainData = (JSONArray) rawForecastResponseObject.get("data");
            JSONObject today = (JSONObject) mainData.get(0);
            //@todo hpaup create a safelyExtractNumbmerValueAsLong function
            long humidity = (long) today.get("rh");//@todo hpaup should this be int? api returns int but my code has long so prob need to update
            double maxTemp = this.safelyExtractNumberValueAsDouble(today, "max_temp");
            double minTemp = this.safelyExtractNumberValueAsDouble(today, "min_temp");
            double rightNowTemp = this.safelyExtractNumberValueAsDouble(today,"temp");

            JSONObject weatherBlock = (JSONObject) today.get("weather");
            String icon = (String) weatherBlock.get("icon");
            String desc = (String) weatherBlock.get("description");

            double windSpeed = this.safelyExtractNumberValueAsDouble(today, "wind_spd");

            Forecast[] fiveDayLookahead = new Forecast[5];
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
            System.out.println("Here is the Forecast model: ");
            System.out.println(fore);
            return fore;
        }
        catch (Exception e) {
            //@todo hpaup handle error
            System.out.println(e.getMessage());
        }

        return null;
    }

    private boolean validateSchema(JSONObject rawForecastResponseObject) throws JsonProcessingException {
        JsonSchema schema = getJsonSchema("#WeatherbitForecaseResponseSchema.json");
        Set<ValidationMessage> errors = schema.validate(new ObjectMapper().readTree(String.valueOf(rawForecastResponseObject)));
        return errors.isEmpty();
    }

    protected JsonSchema getJsonSchema(String schemaId) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        //return factory.getSchema("../../resources/static/FullDayForecastSchema.json"); //@todo hpaup this didn't work
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
                        "      \"type\": \"string\"\n" +
                        "    },\n" +
                        "    \"lon\": {\n" +
                        "      \"type\": \"string\"\n" +
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
        /**return factory.getSchema(
                "{\n"
                        + "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n"
                        + "  \"$id\": \"#MyJsonClassSchema.json\",\n"
                        + "  \"type\": \"object\",\n"
                        + "  \"properties\": {\n"
                        + "    \"myProperty\": {\n"
                        + "      \"oneOf\": [\n"
                        + "        {\n"
                        + "          \"type\": \"string\"\n"
                        + "        },\n"
                        + "        {\n"
                        + "          \"type\": \"array\",\n"
                        + "          \"items\": [\n"
                        + "            {\n"
                        + "              \"type\": \"string\"\n"
                        + "            }\n"
                        + "          ]\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  }\n"
                        + "}"
        );*/
    }

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
    private double safelyExtractNumberValueAsDouble(JSONObject rawObject, String key) {
        //@todo hpaup add try catch exception handling
        double temp = 0;
        Object tempRaw = rawObject.get(key);
        if (tempRaw instanceof Double) {
            temp = (double) rawObject.get(key);
        } else if (tempRaw instanceof String) {
            temp = Double.parseDouble((String) tempRaw);
        } else if (tempRaw instanceof Long) {
            temp = Double.parseDouble(Long.toString((Long) tempRaw));
        }
        return temp;
    }

    private Forecast parseIndividualForecast(JSONObject today) {
        JSONObject weatherBlock = (JSONObject) today.get("weather");
        String desc = (String) weatherBlock.get("description");

        double maxTemp = this.safelyExtractNumberValueAsDouble(today, "max_temp");
        double minTemp = this.safelyExtractNumberValueAsDouble(today, "min_temp");

        String validDate = this.safelyExtractValueAsString(today, VALID_DATE_KEY);

        Forecast forecast = new Forecast();
        forecast.setValidDate(validDate);
        forecast.setDescription(desc);
        forecast.setMaxTemp(maxTemp);
        forecast.setMinTemp(minTemp);
        return forecast;
    }

    public FullDayForecast findFiveDayForecastByCity(String city, String apiKey, String units) {
        String openWeatherApiParameterForCityInput = "q";
        String requestUri =
                "https://api.weatherbit.io/v2.0/forecast/daily?" +
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
            FullDayForecast fore = this.parseWeatherbitForecast(response);
            return fore;

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

    private String replaceSpaces(String raw) {
        try {
            return raw.replaceAll("\\x20", "%20");
        } catch (Exception e) {
            //@todo hpaup handle error
            return "";
        }

    }

    //@todo hpaup rename this and other functions to be fetch i think, instead of find
    //@todo hpaup refactor so instead of passing parameters through several layers in the same way, create a
    //@todo hpaup forecast request model or something and make city,apiKey, units attributes of the model so you can pass that
    //@todo hpaup and reuse it in the service for subsequent calls
    public FullDayForecast findFiveDayForecastByCoords(String lat, String lon, String apiKey, String units) {
        String openWeatherApiParameterForCityInput = "q";
        String requestUri =
                "https://api.weatherbit.io/v2.0/forecast/daily?" +
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
            FullDayForecast fore = this.parseWeatherbitForecast(response);
            return fore;

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
}


