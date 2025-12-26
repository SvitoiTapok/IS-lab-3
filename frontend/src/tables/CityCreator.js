import React, {useState, useMemo, useEffect} from 'react';
import './humanCreator.css';
import Long from 'long';
import {
    flexRender,
    getCoreRowModel,
    getFilteredRowModel, getPaginationRowModel,
    getSortedRowModel,
    useReactTable
} from "@tanstack/react-table";
import "../util/pagination.css"
import coordinatesService from "../services/CoordinatesService";
import {DatePicker} from "antd";
import humanService from "../services/HumanService";
import cityService from "../services/CityService";
import {useError} from "../util/ErrorContext";

const climats = ["RAIN_FOREST",
    "TROPICAL_SAVANNA",
    "HUMIDCONTINENTAL"];
/* global BigInt */

const CityCreator = () => {

    const [name, setName] = useState('');
    const [coordinates, setCoordinates] = useState([]);
    const [coordinate, setCoordinate] = useState({id: 0, x: 0, y: 0});
    const [area, setArea] = useState('');
    const [population, setPopulation] = useState('');
    const [date, setDate] = useState(null);
    const [capital, setCapital] = useState(false);
    const [metersAbove, setMetersAbove] = useState('');
    const [populationDensity, setPopulationDensity] = useState('');
    const [telephoneCode, setTelephoneCode] = useState('');
    const [climate, setClimate] = useState(climats[0]);
    const [humans, setHumans] = useState([]);
    const [governor, setGovernor] = useState({id: 0, name: ""});

    const {showError, showNotification} = useError();
    const switch_state = () => {
        setCapital(!capital);
    }
    useEffect(() => {
        const initializeData = async () => {
            try {
                const [coordsData, humansData] = await Promise.all([
                    coordinatesService.getAllCoordinates(0, 10000000, 'id', 'asc'),
                    humanService.getHumans(0, 10000000, 'id', 'asc')
                ]);

                setCoordinates(coordsData.content);
                setHumans(humansData.content);

                if (coordsData.content.length > 0) {
                    setCoordinate(coordsData.content[0]);
                }
                if (humansData.content.length > 0) {
                    setGovernor(humansData.content[0]);
                }

            } catch (err) {
                showError(err.toString());
            }
        };

        initializeData();
        const intervalId = setInterval(() => {
            getAllCoords();
            getAllHumans()
        }, 5000);

        return () => clearInterval(intervalId);
    }, []);

    const coordsToString = (coord) => {
        if (coord == null)
            return ""
        return 'id:' + coord.id + ' (' + coord.x + ', ' + coord.y + ')'
    }
    const nameToString = (name) => {
        if (name == null)
            return ""
        return name
    }
    const getAllCoords = async () => {
        let sortBy = 'id';
        let sortOrder = 'asc';


        await coordinatesService.getAllCoordinates(
            0,
            10000000,
            sortBy,
            sortOrder
        )
            .then(data => {
                setCoordinates(data.content);

            })
            .catch(err => {
                showError(err.toString());
            });
    }
    const getAllHumans = async () => {
        let sortBy = 'id';
        let sortOrder = 'asc';


        await humanService.getHumans(
            0,
            10000000,
            sortBy,
            sortOrder
        )
            .then(data => {
                setHumans(data.content);
            })
            .catch(err => {
                showError(err.toString());
            });
    }


    const getName = () => {
        const vname = name;
        if (vname.trim() === '') {
            showError('Название города должно быть не пустой строкой')
            return null;
        }
        return vname
    }
    const getCoords = () => {
        if(coordinate.id===0){
            showError("Добавьте координаты!")
            return null
        }
        return coordinate;
    }
    const getArea = () => {
        try {
            const varea = BigInt(area);
            if (varea < 1) {
                showError('Площадь должна числом быть больше нуля')
                return null;
            }
            return varea.toString()
        } catch (e) {
            showError('Площадь должна быть натуральным числом')
            return null;
        }

    }
    const getPopulation = () => {
        const vpopulation = parseInt(population);
        if (isNaN(vpopulation)) {
            showError('Население должно быть натуральным числом')
            return null;
        }
        if (vpopulation < 1) {
            showError('Население должна быть больше нуля')
            return null;
        }
        return vpopulation
    }
    const getEstablishementDate = () => {
        if (date == null) {
            showError('Выберите дату')
            return null;
        }
        return date
    }
    const getCapital = () => {
        return capital
    }
    const getMetersAboveSeaLevel = () => {
        const vMeters = parseInt(metersAbove);
        console.log(vMeters)
        if (isNaN(vMeters)) {
            showError('Высота над уровнем моря должна быть целым числом')
            return null;
        }
        return vMeters
    }
    const getPopulationDensity = () => {
        try {
            const vpopulationDensity = BigInt(populationDensity);
            if (vpopulationDensity < 1) {
                showError('Плотность населения должна быть больше нуля')
                return null;
            }
            return vpopulationDensity.toString()
        } catch (e) {
            showError('Плотность населения должна быть натуральным числом')
            return null;
        }

    }
    const getTelephoneCode = () => {
        const vtelephone = parseInt(telephoneCode);
        if (isNaN(vtelephone)) {
            showError('Телефонный код должен быть натуральным числом')
            return null;
        }
        if (vtelephone < 1 || vtelephone > 100000) {
            showError('Телефонный код должен быть больше 0 и меньше 100000')
            return null;
        }
        return vtelephone
    }
    const getClimate = () => {
        return climate
    }
    const getGovernor = () => {
        if(governor.id===0){
            showError("Добавьте людей!")
            return null
        }
        return governor;
    }


    const validate = () => {

        const vname = getName()
        if (vname == null) return null
        const vcoords = getCoords()
        if (vcoords == null) return null
        const varea = getArea()
        if (varea == null) return null
        const vpopulation = getPopulation()
        if (vpopulation == null) return null
        const vEsDate = getEstablishementDate()
        if (vEsDate == null) return null
        const vcapital = getCapital()
        if (vcapital == null) return null
        const vmeters = getMetersAboveSeaLevel()
        if (vmeters == null) return null
        const vpopdest = getPopulationDensity()
        if (vpopdest == null) return null
        const vtelephone = getTelephoneCode()
        if (vtelephone == null) return null
        const vclimate = getClimate()
        const vgovernor = getGovernor()
        return [vname, vcoords, varea, vpopulation, vEsDate, vcapital, vmeters, vpopdest, vtelephone, vclimate, vgovernor]


    }
    const addNewCity = async () => {
        const data = validate()
        if (data == null) return null
        try {
            console.log(data)
            await cityService.addCity({
                name: data[0], coordinates: data[1], area: data[2],
                population: data[3], establishmentDate: data[4], capital: data[5], metersAboveSeaLevel: data[6],
                populationDensity: data[7], telephoneCode: data[8], climate: data[9], human: data[10]
            });
        } catch (err) {
            showError(err.toString());
            return;
        }
        setName('');
        setArea('');
        setPopulation('');
        setCapital(false);
        setMetersAbove('');
        setTelephoneCode('');
        setClimate(climats[0]);
        setPopulationDensity('')
        showNotification("Объект успешно добавлен")
    }
    return (
        <div className="creator">
            <span>
                Добавление нового города
            </span>
            <div className="input-group">
                <table className="w-full">
                    <tbody>
                    <tr>
                        <td>
                            Название города:
                        </td>
                        <td>
                            <input
                                placeholder="Введите название..."
                                value={name}
                                onChange={(e) => {
                                    setName(e.target.value);
                                }}
                                className="name-input"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Координаты города:
                        </td>
                        <td>
                            <select className="pagination-selector"
                                    value={coordinate.id}
                                    onChange={e => {
                                        const selectedCoord = coordinates.find(coord => coord.id === parseInt(e.target.value));
                                        setCoordinate(selectedCoord);

                                        // setCoordinate(e.target.value);
                                    }}
                                    onClick={() => getAllCoords()}
                            >
                                {coordinates.map(x => (
                                    <option key={x.id} value={x.id}>
                                        {coordsToString(x)}
                                    </option>
                                ))}
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Площадь города:
                        </td>
                        <td>
                            <input
                                placeholder="Введите число..."
                                value={area}
                                onChange={(e) => {
                                    setArea(e.target.value);
                                }}
                                className="name-input"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Население:
                        </td>
                        <td>
                            <input
                                placeholder="Введите число..."
                                value={population}
                                onChange={(e) => {
                                    setPopulation(e.target.value);
                                }}
                                className="name-input"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Дата основания:
                        </td>
                        <td>
                            <DatePicker
                                selected={date}
                                onChange={date => setDate(date)}
                                dateFormat="dd/MM/yyyy"
                                placeholderText="Выберите дату"
                                isClearable
                                showYearDropdown
                                scrollableYearDropdown
                            />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Столица:
                        </td>
                        <td>
                            <button onClick={switch_state}
                                    className={capital ? "create-button" : "create-button-negative"}>
                                {capital ? 'Да' : 'Нет'}
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Высота над уровнем моря:
                        </td>
                        <td>
                            <input
                                placeholder="Введите число..."
                                value={metersAbove}
                                onChange={(e) => {
                                    setMetersAbove(e.target.value);
                                }}
                                className="name-input"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Плотность населения:
                        </td>
                        <td>
                            <input
                                placeholder="Введите число..."
                                value={populationDensity}
                                onChange={(e) => {
                                    setPopulationDensity(e.target.value);
                                }}
                                className="name-input"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Телефонный код:
                        </td>
                        <td>
                            <input
                                placeholder="Введите число..."
                                value={telephoneCode}
                                onChange={(e) => {
                                    setTelephoneCode(e.target.value);
                                }}
                                className="name-input"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Климат:
                        </td>
                        <td>
                            <select className="pagination-selector"
                                    value={climate}
                                    onChange={e => {
                                        setClimate(e.target.value);
                                    }}
                            >
                                {climats.map(x => (
                                    <option key={x} value={x}>
                                        {x}
                                    </option>
                                ))}
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Губернатор города:
                        </td>
                        <td>
                            <select className="pagination-selector"
                                    value={governor.id}
                                    onChange={e => {
                                        const selectedHuman = humans.find(human => human.id.toString() === e.target.value);
                                        console.log(selectedHuman.id)
                                        setGovernor(selectedHuman);
                                    }}
                                    onClick={() => getAllHumans()}
                            >
                                {humans.map(x => (
                                    <option key={x.id} value={x.id}>
                                        {nameToString(x.name)}
                                    </option>
                                ))}
                            </select>
                        </td>
                    </tr>


                    </tbody>
                </table>

            </div>
            <div>
                <button onClick={addNewCity} className="create-button">
                    Добавить город
                </button>
            </div>
        </div>
    );
};

export default CityCreator;