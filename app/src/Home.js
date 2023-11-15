import React from 'react';
import './App.css';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';
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