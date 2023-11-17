import React from 'react';
import './App.css';
import AppNavbar from './AppNavbar';
import SearchBar from "./SearchBar";

const Home = () => {
    return (
        <div>
            <AppNavbar/>
            <SearchBar/>
        </div>
    );
}

export default Home;