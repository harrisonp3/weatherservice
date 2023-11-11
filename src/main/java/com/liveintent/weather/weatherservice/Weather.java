package com.liveintent.weather.weatherservice;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Weather
{

    public static void main ( String[] args )
    {
        System.out.println("HPAUP i'm in the mainline in Weather.java class");
        Weather app = new Weather();
        app.demo();
    }

    public void demo ( )
    {
        //Creating a JSONParser object
        JSONParser jsonParser = new JSONParser();
        try
        {
            // Download JSON.
            String yourKey = "b6907d289e10d714a6e88b30761fae22";
            URL url = new URL( "https://samples.openweathermap.org/data/2.5/forecast/hourly?zip=79843&appid=b6907d289e10d714a6e88b30761fae22" + yourKey ); // 79843 = US postal Zip Code for Marfa, Texas.
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );


            // Parse JSON
            JSONObject jsonObject = ( JSONObject ) jsonParser.parse( reader );
            System.out.println( "jsonObject = " + jsonObject );

            JSONArray list = ( JSONArray ) jsonObject.get( "list" );
            System.out.println( "list = " + list );

            // Loop through each item
            for ( Object o : list )
            {
                JSONObject forecast = ( JSONObject ) o;

                Long dt = ( Long ) forecast.get( "dt" );          // Parse text into a number of whole seconds.
                Instant instant = Instant.ofEpochSecond( dt );    // Parse the count of whole seconds since 1970-01-01T00:00Z into a `Instant` object, representing a moment in UTC with a resolution of nanoseconds.
                ZoneId z = ZoneId.of( "America/Chicago" );        // Specify a time zone using a real `Continent/Region` time zone name. Never use 2-4 letter pseudo-zones such as `PDT`, `CST`, `IST`, etc.
                ZonedDateTime zdt = instant.atZone( z );          // Adjust from a moment in UTC to the wall-clock used by the people of a particular region (a time zone). Same moment, same point on the timeline, different wall-clock time.
                LocalTime lt = zdt.toLocalTime() ;
                // … compare with lt.equals( LocalTime.NOON ) to find the data sample you desire.
                System.out.println( "dt : " + dt );
                System.out.println( "instant : " + instant );
                System.out.println( "zdt : " + zdt );

                JSONObject main = ( JSONObject ) forecast.get( "main" );
                System.out.println( "main = " + main );


                Double temp = ( Double ) main.get( "temp" );  // Better to use BigDecimal instead of Double for accuracy. But I do not know how to get the JSON-Simple library to parse the original string input as a BigDecimal.
                System.out.println( "temp = " + temp );

                JSONObject wind = ( JSONObject ) forecast.get( "wind" );
                System.out.println( "wind = " + wind );

                System.out.println( "BASIL - wind.getCLass: " + wind.getClass() );
                Double speed = ( Double ) wind.get( "speed" );
                System.out.println( "speed = " + speed );

                System.out.println( "\n" );
            }
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        catch ( ParseException e )
        {
            e.printStackTrace();
        }
    }
}