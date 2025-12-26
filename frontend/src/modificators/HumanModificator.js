import React, {useState, useMemo, useEffect} from 'react';
import '../tables/humanCreator.css';
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
import {useLocation, useNavigate} from "react-router-dom";
import {wait} from "@testing-library/user-event/dist/utils";
import {useError} from "../util/ErrorContext";

const climats = ["RAIN_FOREST",
    "TROPICAL_SAVANNA",
    "HUMIDCONTINENTAL"];
/* global BigInt */

const HumanModificator = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const human = location.state?.human || null;


    const [name, setName] = useState(human?.name || '');
    const {showError, showNotification} = useError();

    if (!human) {
        return (
            <div>
                <h2>Ошибка: человек не выбран</h2>
                <button onClick={() => navigate('/')} className="create-button">Назад</button>
            </div>
        );
    }


    const getName = () => {
        const vname = name;
        if (vname.trim() === '') {
            showError('Имя человека должно быть не пустой строкой')
            return null;
        }
        return vname
    }


    const updateHuman = async () => {
        const name = getName()
        if (name == null) return null
        try {
            await humanService.patchHuman(human.id, {
                name: name,
            });
            console.log("ww")
            showNotification("Человек успешно обновлен")

        } catch (err) {
            showError(err.toString());
            return;
        }
        navigate('/human')

    }

    return (
        <div className="creator">
            <span>
                Изменение Человека с id: {human.id}
            </span>
            <div className="input-group">
                <table className="w-full">
                    <tbody>
                    <tr>
                        <td>
                            Имя человека:
                        </td>
                        <td>
                            <input
                                placeholder="Введите имя..."
                                value={name}
                                onChange={(e) => {
                                    setName(e.target.value);
                                }}
                                className="name-input"/>
                        </td>
                    </tr>
                    </tbody>
                </table>

            </div>
            <div>
                <button onClick={updateHuman} className="create-button">
                    Обновить человека
                </button>
            </div>
        </div>
    );
};

export default HumanModificator;