import React, { useState } from 'react';
import UnitSelector from "./UnitSelector";

let nextId = 0;
const SearchBar = () => {
    const [citySearchInput, setCitySearchInput] = useState("");
    const [latSearchInput, setLatSearchInput] = useState("");
    const [lonSearchInput, setLonSearchInput] = useState("");
    const [measurementUnit, setMeasurementUnit] = useState("imperial");
    const [forecasts, setForecasts] = useState([
        {
            id: nextId,
            description: "default dummy text - delete me",
            humidity: 70,
            minTemp: 45,
            maxTemp: 54,
            icon: "10n",
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
            icon: "01n",
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
    const handleCityChange = (e) => {
        e.preventDefault();
        setCitySearchInput(e.target.value);
        setLatSearchInput("");
        setLonSearchInput("");
        console.log(forecasts);
    };

    const handleLatitudeChange = (e) => {
        e.preventDefault();
        setLatSearchInput(e.target.value);
        setCitySearchInput("");

    };
    const handleLongitudeChange = (e) => {
        e.preventDefault();
        setLonSearchInput(e.target.value);
        setCitySearchInput("");
    };



    const handleKeyUp = (e) => {
        e.preventDefault();
        if (e.keyCode === 13) {
            let queryParams = citySearchInput ? `city=${citySearchInput}` : `lat=${latSearchInput}&lon=${lonSearchInput}`;
            console.log(measurementUnit);
            console.log("attempting fetch 2.0");
            fetch(`/api/forecast/hpaup?` + queryParams + `&units=${measurementUnit}`)
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
                            icon: data.icon.substring(1),// for some reason they get retuned from API with a "c"- prefix so I remove that here
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
            placeholder="Search here by city"
            onChange={handleCityChange}
            onKeyUp={handleKeyUp}
            value={citySearchInput} /> OR
        <input
            type="search"
            placeholder="Latitude"
            onChange={handleLatitudeChange}
            onKeyUp={handleKeyUp}
            value={latSearchInput} />
        <input
            type="search"
            placeholder="Longitude"
            onChange={handleLongitudeChange}
            onKeyUp={handleKeyUp}
            value={lonSearchInput} />
        <table>
            <tr>
                Dummy text - hpaup delete me...
                {forecasts.map((forecast) => {
                    return(
                        <tr>
                            <td>Description: {forecast.description}</td>
                            <td>Humidity: {forecast.humidity}</td>
                            <td>Windspeed: {forecast.windSpeed}</td>
                            <td>Icon: <img src={`https://openweathermap.org/img/wn/${forecast.icon}@2x.png`} /></td>
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
        </table>
    </div>
};

export default SearchBar;