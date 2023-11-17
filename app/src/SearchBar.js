import React, { useState } from 'react';
import UnitSelector from "./UnitSelector";

let nextId = 0;
const SearchBar = () => {
    const [citySearchInput, setCitySearchInput] = useState("");
    const [latSearchInput, setLatSearchInput] = useState("");
    const [lonSearchInput, setLonSearchInput] = useState("");
    const [measurementUnit, setMeasurementUnit] = useState("imperial");
    const [errorMessage, setErrorMessage] = useState("");
    const [forecasts, setForecasts] = useState([
        {
            id: nextId,
            description: "default dummy text - delete me",
            humidity: 70,
            minTemp: 45,
            maxTemp: 54,
            icon: "10n",
            windSpeed: 56,
            cityName: "Lima",
            coordinates: {
                latitude: -75.121,
                longitude: -67.009
            },
            fiveDayForecast: [
                {
                    "minTemp": 65.8,
                    "maxTemp": 72.0,
                    "description": "Overcast clouds",
                    "validDate": "2023-11-01"
                },
                {
                    "minTemp": 65.7,
                    "maxTemp": 72.7,
                    "description": "Overcast clouds",
                    "validDate": "2023-11-02"

                }
            ]
        }/**, {
            id: nextId++,
            description: "alt dummy text - delete me",
            humidity: 12,
            minTemp: 45,
            maxTemp: 54,
            icon: "01n",
            windSpeed: 56,
            cityName: "Boston",
            coordinates: {
                latitude: -75.009,
                longitude: -67.890
            },
            fiveDayForecast: [
                {
                    minTemp: 65.8,
                    maxTemp: 72.0,
                    description: "Overcast clouds",
                    validDate: "2023-11-01"
                },
                {
                    minTemp: 65.7,
                    maxTemp: 72.7,
                    description: "Overcast clouds",
                    validDate: "2023-11-02"
                }
                ]
        }*/
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
            fetch(`/api/forecast?` + queryParams + `&units=${measurementUnit}`)
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
                            cityName: data.cityName,
                            maxTemp: data.maxTemp,
                            icon: data.icon.substring(1),// for some reason they get retuned from API with a "c"- prefix so I remove that here
                            windSpeed: data.windSpeed,
                            coordinates: {
                                latitude: data.coord.latitude,
                                longitude: data.coord.longitude
                            },
                            fiveDayForecast: data.fiveDayForecast
                    }
                    ]);
                    // Clear the error message if there was one because we got a successful response back
                    setErrorMessage("");
                }).catch(error => {
                    setErrorMessage(error.toString());
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
            <div>
                {errorMessage &&
                    <div className={"red-text"}>
                        Error: {errorMessage}
                    </div>
                }
                {forecasts.map((forecast) => {
                    return(
                        <table>
                            <tr>
                                <td className={"table-h-table-r bold"}>City:</td>
                                <td className={"table-h-table-r bold"}>Description:</td>
                                <td className={"table-h-table-r bold"}>Humidity:</td>
                                <td className={"table-h-table-r bold"}>Windspeed:</td>
                                <td className={"table-h-table-r bold"}>Icon:</td>
                                <td className={"table-h-table-r bold"}>Minimum temperature:</td>
                                <td className={"table-h-table-r bold"}>Maximum temperature:</td>
                                <td className={"table-h-table-r bold"}>Latitude:</td>
                                <td className={"table-h-table-r bold"}>Longitude:</td>
                            </tr>
                            <tr className={"table-h-table-r"}>
                                <td className={"table-h-table-r"}>{forecast.cityName}</td>
                                <td className={"table-h-table-r"}>{forecast.description}</td>
                                <td className={"table-h-table-r"}>{forecast.humidity}</td>
                                <td className={"table-h-table-r"}>{forecast.windSpeed}</td>
                                <td className={"table-h-table-r"}><img src={`https://openweathermap.org/img/wn/${forecast.icon}@2x.png`} /></td>
                                <td className={"table-h-table-r"}>{forecast.minTemp}</td>
                                <td className={"table-h-table-r"}>{forecast.maxTemp}</td>
                                <td className={"table-h-table-r"}>{forecast.coordinates.latitude}</td>
                                <td className={"table-h-table-r"}> {forecast.coordinates.longitude}</td>
                                {forecast.fiveDayForecast.map((miniForecast) => {
                                    return (
                                        <tr className={"table-h-table-r"}>
                                            <td className={"table-h-table-r"}>Date: {miniForecast.validDate}</td>
                                            <td className={"table-h-table-r"}>Min temp:{miniForecast.minTemp}</td>
                                            <td className={"table-h-table-r"}>Max temp:{miniForecast.maxTemp}</td>
                                            <td className={"table-h-table-r"}>Description:{miniForecast.description}</td>
                                        </tr>
                                    )
                                })}
                            </tr>
                        </table>

                    )})}
            </div>
    </div>
};

export default SearchBar;