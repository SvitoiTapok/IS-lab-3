import React, {useEffect, useState} from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import MainTable from "./tables/Table";
import HumanCreator from "./tables/HumanCreator";
import CoordCreator from "./tables/CoordCreator";
import {BrowserRouter, Link, NavLink, Route, Routes} from 'react-router-dom';
import './util/navigation.css'
import CityCreator from "./tables/CityCreator";
import CityModificator from "./modificators/CityModificator";
import QueryManager from "./tables/QueryManager";
import {ErrorProvider} from "./util/ErrorContext";
import HumanModificator from "./modificators/HumanModificator";
import CoordsModificator from "./modificators/CoordsModificator";
import TestComponent from "./util/TestComponent";
import ImportResultStory from "./tables/ImportResultStory";
//import reportWebVitals from './reportWebVitals';
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>

        <ErrorProvider>
            <BrowserRouter>
                <nav className="navigation">
                    <NavLink
                        to="/"
                        className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}
                    >
                        Main
                    </NavLink>
                    <NavLink
                        to="/human"
                        className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}
                    >
                        Human
                    </NavLink>
                    <NavLink
                        to="/coord"
                        className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}
                    >
                        Coords
                    </NavLink>
                    <NavLink
                        to="/city"
                        className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}
                    >
                        Add New City
                    </NavLink>
                    <NavLink
                        to="/query"
                        className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}
                    >
                        Query
                    </NavLink>
                    {/*<NavLink*/}
                    {/*    to="/test"*/}
                    {/*    className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}*/}
                    {/*>*/}
                    {/*    Test*/}
                    {/*</NavLink>*/}
                    <NavLink to="/import"
                             className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}>
                        ImportStory
                    </NavLink>
                </nav>
                <Routes>
                    <Route path="/" element={<MainTable/>}/>
                    <Route path="/human" element={<HumanCreator/>}/>
                    <Route path="/coord" element={<CoordCreator/>}/>
                    <Route path="/city" element={<CityCreator/>}/>
                    <Route path="/edit-city" element={<CityModificator/>}/>
                    <Route path="/edit-human" element={<HumanModificator/>}/>
                    <Route path="/edit-coord" element={<CoordsModificator/>}/>
                    <Route path="/query" element={<QueryManager/>}/>
                    <Route path="/import" element={<ImportResultStory/>}/>
                    {/*под коммент*/}
                    {/*<Route path="/test" element={<TestComponent/>}/>*/}
                </Routes>
            </BrowserRouter>
        </ErrorProvider>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
//reportWebVitals();
