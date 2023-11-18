import React, { useState } from 'react';
import UnitSelector from "./UnitSelector";
import { Table } from 'reactstrap';

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

    return <div className={"body-font-styling"}>
        <UnitSelector
            onSelectionChange={setMeasurementUnit}
        />
        <input className={"styled-input"}
               aria-label={"city-input"}
               type="search"
               placeholder="Search here by city"
               onChange={handleCityChange}
               onKeyUp={handleKeyUp}
               value={citySearchInput} /> OR
        <input className={"styled-input"}
               aria-label={"latitude-input"}
               type="search"
               placeholder="Latitude"
               onChange={handleLatitudeChange}
               onKeyUp={handleKeyUp}
               value={latSearchInput} />
        <input className={"styled-input"}
               aria-label={"longitude-input"}
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
                        <div>
                            <Table responsive variant={"dark"} bordered aria-label={"main-forecast-table"}>
                                <tbody>
                                <tr className={"table-title"}><td>Today:</td></tr>
                                <tr>
                                    <td className={"bold"}>City</td>
                                    <td className={"bold"}>Description</td>
                                    <td className={"bold"}>Humidity</td>
                                    <td className={"bold"}>Windspeed</td>
                                    <td className={"bold"}>Looks like</td>
                                    <td className={"bold"}>Min. temp.</td>
                                    <td className={"bold"}>Max. temp.</td>
                                    <td className={"bold"}>Latitude</td>
                                    <td className={"bold"}>Longitude</td>
                                 </tr>
                                 <tr className={""}>
                                     <td className={""}>{forecast.cityName}</td>
                                     <td className={""}>{forecast.description}</td>
                                     <td className={""}>{forecast.humidity}</td>
                                     <td className={""}>{forecast.windSpeed}</td>
                                     <td className={""}><img src={`https://openweathermap.org/img/wn/${forecast.icon}@2x.png`} /></td>
                                     <td className={""}>{forecast.minTemp}°</td>
                                     <td className={""}>{forecast.maxTemp}°</td>
                                     <td className={""}>{forecast.coordinates.latitude}</td>
                                     <td className={""}> {forecast.coordinates.longitude}</td>
                                 </tr>
                                </tbody>
                            </Table>
                            <Table responsive bordered variant={"dark"} aria-label={"secondary-forecast-table"}>
                                <tbody>
                                <tr className={"table-title"}><td>Coming up:</td></tr>
                                <tr>
                                    <td className={"bold"}>Date</td>
                                    <td className={"bold"}>Min. temp.</td>
                                    <td className={"bold"}>Max. temp.</td>
                                    <td className={"bold"}>Description</td>
                                </tr>
                                {forecast.fiveDayForecast.map((miniForecast) => {
                                    return (
                                        <tr className={""}>
                                            <td className={""}>{miniForecast.validDate}</td>
                                            <td className={""}>{miniForecast.minTemp}°</td>
                                            <td className={""}>{miniForecast.maxTemp}°</td>
                                            <td className={""}>{miniForecast.description}</td>
                                        </tr>
                                    )
                                })}
                                </tbody>
                            </Table>
                        </div>
                    )})}
            </div>
    </div>
};

export default SearchBar;