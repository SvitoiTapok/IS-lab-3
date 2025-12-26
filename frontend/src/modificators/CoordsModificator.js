import React, {useState, useMemo, useEffect} from 'react';
import '../tables/humanCreator.css';
import "../util/pagination.css"
import humanService from "../services/HumanService";
import {useLocation, useNavigate} from "react-router-dom";
import {useError} from "../util/ErrorContext";
import coordService from "../services/CoordinatesService";

/* global BigInt */

const CoordsModificator = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const coords = location.state?.coords || null;


    const [x, setX] = useState(coords?.x.toString() || '');
    const [y, setY] = useState(coords?.y.toString() || '');
    const {showError, showNotification} = useError();

    if (!coords) {
        return (
            <div>
                <h2>Ошибка: Координаты не выбран</h2>
                <button onClick={() => navigate('/')} className="create-button">Назад на главную</button>
            </div>
        );
    }


    const getX = () => {
        const floatX = parseFloat(x.replace(',', '.'));
        if (isNaN(floatX)) {
            showError('X должен быть корректным числом с плавающей запятой')
            return null;
        }
        return x
    }
    const getY = () => {
        const floatY = parseFloat(y.replace(',', '.'));
        if (isNaN(floatY)) {
            showError('Y должен быть корректным числом')
            return;
        }
        if (floatY <= -563) {
            showError('Y должен быть больше -563')
            return null;
        }
        return y
    }


    const updateCoords = async () => {
        const x = getX()
        const y = getY()
        if (x == null || y == null) return null
        try {
            await coordService.patchCoord(coords.id, {
                x: x,
                y: y,
            });
            showNotification("Координаты успешно обновлены")
        } catch (err) {
            showError(err.toString());
            return;
        }
        navigate('/coord')

    }

    return (
        <div className="creator">
            <span>
                Изменение Координаты с id: {coords.id}
            </span>
            <div className="input-group">
                <table className="w-full">
                    <tbody>
                    <tr>
                        <td>
                            X:
                        </td>
                        <td>
                            <input
                                placeholder="Введите число..."
                                value={x}
                                onChange={(e) => {
                                    setX(e.target.value);
                                }}
                                className="name-input"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Y:
                        </td>
                        <td>
                            <input
                                placeholder="Введите число..."
                                value={y}
                                onChange={(e) => {
                                    setY(e.target.value);
                                }}
                                className="name-input"/>
                        </td>
                    </tr>
                    </tbody>
                </table>

            </div>
            <div>
                <button onClick={updateCoords} className="create-button">
                    Обновить Координаты
                </button>
            </div>
        </div>
    );
};

export default CoordsModificator;