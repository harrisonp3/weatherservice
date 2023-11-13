import React, {useState} from 'react'

let nextId = 0;
const SearchBar = () => {
    const [searchInput, setSearchInput] = useState("");
    const [forecasts, setForecasts] = useState([
        {
            id: nextId,
            description: "default dummy text - delete me",
            humidity: 70
        }, {
            id: nextId++,
            description: "alt dummy text - delete me",
            humidity: 12
        }
    ]);
    const [hpaup, setHpaup] = useState("TESTING123");

    const countries = [

        { name: "Belgium", continent: "Europe" },
        { name: "India", continent: "Asia" },
        { name: "Bolivia", continent: "South America" },
        { name: "Ghana", continent: "Africa" },
        { name: "Japan", continent: "Asia" },
        { name: "Canada", continent: "North America" },
        { name: "New Zealand", continent: "Australasia" },
        { name: "Italy", continent: "Europe" },
        { name: "South Africa", continent: "Africa" },
        { name: "China", continent: "Asia" },
        { name: "Paraguay", continent: "South America" },
        { name: "Usa", continent: "North America" },
        { name: "France", continent: "Europe" },
        { name: "Botswana", continent: "Africa" },
        { name: "Spain", continent: "Europe" },
        { name: "Senegal", continent: "Africa" },
        { name: "Brazil", continent: "South America" },
        { name: "Denmark", continent: "Europe" },
        { name: "Mexico", continent: "South America" },
        { name: "Australia", continent: "Australasia" },
        { name: "Tanzania", continent: "Africa" },
        { name: "Bangladesh", continent: "Asia" },
        { name: "Portugal", continent: "Europe" },
        { name: "Pakistan", continent: "Asia" },

    ];
    const handleChange = (e) => {
        e.preventDefault();
        setSearchInput(e.target.value);
        console.log(forecasts);
    };

    const handleKeyUp = (e) => {
        e.preventDefault();
        if (e.keyCode === 13) {
            console.log("attempting fetch 2.0");
            fetch(`/api/forecast/city?city=${searchInput}`)
                .then(response => {
                    console.log(response.data);
                    console.log(response);
                    return response.json();
                })
                .then(data => {
                    console.log(data);
                    setForecasts([
                        ...forecasts,
                        { id: nextId++, humidity: data.humidity, description: data.description }
                    ]);//@todo hpaup the forecasts.map rednering below didn't work until i wrapped the data here in a array
                }).catch(error => {
                    //handle error
                });
        }
    };

    /**
     * setArtists( // Replace the state
     *   [ // with a new array
     *     ...artists, // that contains all the old items
     *     { id: nextId++, name: name } // and one new item at the end
     *   ]
     * );
     */

    // if (searchInput.length > 0) {
    //     console.log("in the block");
    //     countries.filter((country) => {
    //         console.log(country);
    //         console.log("match is " + country.name.match(searchInput));
    //         return country.name.match(searchInput);
    //     });
    // }


    return <div>
        <input
            type="search"
            placeholder="Search here"
            onChange={handleChange}
            onKeyUp={handleKeyUp}
            value={searchInput} />
        <table>
            <tr>
                Dummy text - hpaup delete me... {hpaup}
                {forecasts.map((forecast) => {
                    return(
                        <tr>
                            <td>{forecast.description}</td>
                            <td>{forecast.humidity}</td>
                        </tr>
                    )})}
            </tr>
            <tr>
                <th>Country</th>
                <th>Continent</th>
                <th>{forecasts.humidity}</th>
            </tr>
            {countries.map((country, index) => {
                return(
                    <div>
                        <tr>
                            <td>{country.name}</td>
                            <td>{country.continent}</td>
                        </tr>
                    </div>
                )})}
        </table>
    </div>
};

export default SearchBar;