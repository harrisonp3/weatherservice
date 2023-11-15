import React, { useState } from 'react';
import UnitSelector from "./UnitSelector";

let nextId = 0;
const SearchBar = () => {
    const [searchInput, setSearchInput] = useState("");
    const [measurementUnit, setMeasurementUnit] = useState("imperial");
    const [forecasts, setForecasts] = useState([
        {
            id: nextId,
            description: "default dummy text - delete me",
            humidity: 70,
            minTemp: 45,
            maxTemp: 54,
            icon: "",
            windSpeed: 56,
            coordinates: {
                latitude: -75.121,
                longitude: -67.009
            },
            fiveDayForecast: [
                {
                    "minTemp": 65.8,
                    "maxTemp": 72.0,
                    "description": "Overcast clouds"
                },
                {
                    "minTemp": 65.7,
                    "maxTemp": 72.7,
                    "description": "Overcast clouds"
                }
            ]
        }, {
            id: nextId++,
            description: "alt dummy text - delete me",
            humidity: 12,
            minTemp: 45,
            maxTemp: 54,
            icon: "",
            windSpeed: 56,
            coordinates: {
                latitude: -75.009,
                longitude: -67.890
            },
            fiveDayForecast: [
                {
                    minTemp: 65.8,
                    maxTemp: 72.0,
                    description: "Overcast clouds"
                },
                {
                    minTemp: 65.7,
                    maxTemp: 72.7,
                    description: "Overcast clouds"
                }
                ]
        }
    ]);
    const handleChange = (e) => {
        e.preventDefault();
        setSearchInput(e.target.value);
        console.log(forecasts);
    };

    const handleKeyUp = (e) => {
        e.preventDefault();
        if (e.keyCode === 13) {
            console.log(measurementUnit);
            console.log("attempting fetch 2.0");
            fetch(`/api/forecast/hpaup?city=${searchInput}&units=${measurementUnit}`)
                .then(response => {
                    console.log(response.data);
                    console.log(response);
                    return response.json();
                })
                .then(data => {
                    console.log(data);
                    setForecasts([
                        // Un-comment the spread operator to make the results additive - so each time
                        // you look one up it appends to the view instead of replaces last results
                        //...forecasts,
                        {
                            id: nextId++,
                            humidity: data.humidity,
                            description: data.description,
                            minTemp: data.minTemp,
                            maxTemp: data.maxTemp,
                            icon: data.icon,
                            windSpeed: data.windSpeed,
                            coordinates: {
                                latitude: data.coord.latitude,
                                longitude: data.coord.longitude
                            },
                            fiveDayForecast: data.fiveDayForecast
                    }
                    ]);//@todo hpaup the forecasts.map rednering below didn't work until i wrapped the data here in a array
                }).catch(error => {
                    //handle error
                });
        }
    };

    return <div>
        <UnitSelector
            onSelectionChange={setMeasurementUnit}
        />
        <input
            type="search"
            placeholder="Search here"
            onChange={handleChange}
            onKeyUp={handleKeyUp}
            value={searchInput} />
        <table>
            <tr>
                Dummy text - hpaup delete me...
                {forecasts.map((forecast) => {
                    return(
                        <tr>
                            <td>Description: {forecast.description}</td>
                            <td>Humidity: {forecast.humidity}</td>
                            <td>Windspeed: {forecast.windSpeed}</td>
                            <td>Icon: {forecast.icon}</td>
                            <td>Minimum temperature: {forecast.minTemp}</td>
                            <td>Maximum temperature: {forecast.maxTemp}</td>
                            <td>Latitude: {forecast.coordinates.latitude}</td>
                            <td>Longitude: {forecast.coordinates.longitude}</td>
                            {forecast.fiveDayForecast.map((miniForecast) => {
                                return (
                                    <tr>
                                        <td>MINI MIN TEMP:{miniForecast.minTemp}</td>
                                        <td>MINI MAX TEMP:{miniForecast.maxTemp}</td>
                                        <td>MINI DESCRIPTION:{miniForecast.description}</td>
                                    </tr>
                                )
                            })}
                        </tr>
                    )})}
            </tr>
            <tr>
                <th>Country</th>
                <th>Continent</th>
            </tr>
        </table>
    </div>
};

export default SearchBar;