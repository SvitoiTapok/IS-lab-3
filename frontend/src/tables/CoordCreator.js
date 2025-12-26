import React, {useState, useMemo, useEffect} from 'react';
import './humanCreator.css';
import {
    flexRender,
    getCoreRowModel,
    getFilteredRowModel, getPaginationRowModel,
    getSortedRowModel,
    useReactTable
} from "@tanstack/react-table";
import "../util/pagination.css"
import "./table.css"
import coordinatesService from "../services/CoordinatesService";
import {useError} from "../util/ErrorContext";
import editIcon from "../imgs/edit-icon.png";
import deleteIcon from "../imgs/delete-icon.png";
import humanService from "../services/HumanService";
import cityService from "../services/CityService";
import {useNavigate} from "react-router-dom";

const CoordCreator = () => {
    const navigate = useNavigate();
    const [data, setData] = useState({
        content: [],
        totalElements: 0,
        totalPages: 0
    });
    const [x, setX] = useState('');
    const [y, setY] = useState('');
    const [sorting, setSorting] = useState([]);
    const [pagination, setPagination] = useState({
        pageIndex: 0,
        pageSize: 3,
    });
    const {showError, showNotification} = useError();
    const [cityData, setCityData] = useState([])
    const [curId, setCurId] = useState(0)
    const [coords, setCoords] = useState([])
    const [cityDataVisible, setCityDataVisible] = useState(false);

    const callServer = async () => {
        let sortBy = 'id';
        let sortOrder = 'asc';

        if (sorting.length > 0) {
            sortBy = sorting[0].id;
            sortOrder = sorting[0].desc ? 'desc' : 'asc';
        }

        await coordinatesService.getAllCoordinates(
            pagination.pageIndex,
            pagination.pageSize,
            sortBy,
            sortOrder
        )
            .then(data => {
                setData(data);
            })
            .catch(err => {
                showError(err.toString());
            });
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
                setCoords(data.content);
            })
            .catch(err => {
                showError(err.toString());
            });
    }
    useEffect(() => {
        callServer()
        getAllCoords()
        const intervalId = setInterval(() => {
            callServer();
            getAllCoords()
        }, 5000)

        return () => clearInterval(intervalId);
    }, [pagination.pageIndex, pagination.pageSize, sorting]);


    const addCoord = async () => {
        const floatX = parseFloat(x.replace(',', '.'));
        if (isNaN(floatX)) {
            showError('X должен быть корректным числом с плавающей запятой')
            return;
        }
        const floatY = parseFloat(y.replace(',', '.'));
        if (isNaN(floatY)) {
            showError('Y должен быть корректным числом')
            return;
        }
        if (floatY <= -563) {
            showError('Y должен быть больше -563')
            return;
        }

        try {
            await coordinatesService.addCoordinates({x: floatX, y: floatY});
            await callServer()
            showNotification("Координаты успешно добавлены")
        } catch (err) {
            showError(err.toString());
            return;
        }
        setX('');
        setY('');

    }
    const coordsToString = (coord) => {
        if (coord == null)
            return ""
        return 'id:' + coord.id + ' (' + coord.x + ', ' + coord.y + ')'
    }
    const handleDelete = async (coordId) => {
        try {
            coordinatesService.getCitiesByCoord(coordId).then(data => {
                    if (data.length === 0) {
                        coordinatesService.deleteCoord(coordId).then(()=>callServer());
                        showNotification('Координаты успешно удалены');
                        table.setPageIndex(0);

                        setCityData([])
                        setCityDataVisible(false)
                        return;
                    }
                    setCurId(coordId)
                    setCityData(data)
                    setCityDataVisible(true)

                }
            ).catch(e =>
                showError(e.message)
            )


        } catch (err) {
            showError('Ошибка при удалении города: ' + err.toString());
        }
    };

    const handleEdit = (coord) => {
        navigate('/edit-coord', {
            state: {
                coords: coord,
            }
        });
    };
    const columns = useMemo(
        () => [
            {
                accessorKey: 'id',
                header: 'ID',
                cell: info => parseInt(info.getValue()),
                enableSorting: true,
            },
            {
                accessorKey: 'x',
                header: 'X',
                cell: info => info.getValue(),
                enableSorting: true,

            },
            {
                accessorKey: 'y',
                header: 'Y',
                cell: info => info.getValue(),
                enableSorting: true,
            },
            {
                id: 'actions',
                header: 'Действия',
                cell: ({row}) => (
                    <div className="flex space-x-2">
                        <button
                            onClick={() => handleEdit(row.original)}
                            className="edit-button"
                            title="Редактировать"
                        >
                            <img
                                src={editIcon}
                                alt="Edit"
                                className="action-icon"
                            />
                        </button>
                        <button
                            onClick={() => handleDelete(row.original.id)}
                            className="delete-button"
                            title="Удалить"
                        >
                            <img
                                src={deleteIcon}
                                alt="Edit"
                                className="action-icon"
                            />
                        </button>
                    </div>
                ),
                enableSorting: false,
            },

        ], []);
    const table = useReactTable({
        data: data.content,
        columns,
        state: {
            pagination,
            sorting
        },
        onPaginationChange: setPagination,
        onSortingChange: setSorting,
        getCoreRowModel: getCoreRowModel(),
        getSortedRowModel: getSortedRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        manualPagination: true,
        pageCount: data.totalPages || 0,
    });
    return (
        <div className="creator">
            <span>
                Координаты
            </span>
            <div className="input-group">
                <input
                    placeholder="X"
                    value={x}
                    onChange={(e) => {
                        setX(e.target.value);
                    }}
                    className="name-input"/>
                <input
                    placeholder="Y"
                    value={y}
                    onChange={(e) => {
                        setY(e.target.value);
                    }}
                    className="name-input"/>
                <button onClick={() => {
                    addCoord()
                }}
                        className="create-button-coord">
                    Добавить координаты
                </button>
            </div>
            <table className="w-full">
                <thead>
                {table.getHeaderGroups().map(headerGroup => (
                    <tr key={headerGroup.id}>
                        {headerGroup.headers.map(header => (
                            <th
                                key={header.id}
                                className="text-left border"
                                style={{cursor: 'pointer'}}
                                onClick={header.column.getToggleSortingHandler()}
                            >
                                {flexRender(
                                    header.column.columnDef.header,
                                    header.getContext()
                                )}

                                {header.column.getIsSorted() === 'asc' ? '↑' :
                                    header.column.getIsSorted() === 'desc' ? '↓' : '↕'}
                            </th>
                        ))}
                    </tr>
                ))}
                </thead>
                <tbody>
                {table.getRowModel().rows.map(row => (
                    <tr key={row.id}>
                        {row.getVisibleCells().map(cell => (
                            <td key={cell.id} className="border">
                                {flexRender(cell.column.columnDef.cell, cell.getContext())}
                            </td>
                        ))}
                    </tr>
                ))}
                </tbody>
            </table>
            {/*Пагинация*/}
            <div>
                <button className="vectors"
                        onClick={() => table.setPageIndex(0)}
                        disabled={!table.getCanPreviousPage()}
                >
                    {'<<'}
                </button>
                <button className="vectors"
                        onClick={() => table.previousPage()}
                        disabled={!table.getCanPreviousPage()}
                >
                    {'<'}
                </button>
                <button className="vectors"
                        onClick={() => table.nextPage()}
                        disabled={!table.getCanNextPage()}
                >
                    {'>'}
                </button>
                <button className="vectors"
                        onClick={() => table.setPageIndex(table.getPageCount() - 1)}
                        disabled={!table.getCanNextPage()}
                >
                    {'>>'}
                </button>
            </div>
            <span className="page">
                    Страница{' '}
                {table.getState().pagination.pageIndex + 1} из {table.getPageCount()}
            </span>

            <select className="pagination-selector"
                    value={table.getState().pagination.pageSize}
                    onChange={e => {
                        table.setPageSize(parseInt(e.target.value));
                    }}
            >
                {[3, 5, 10].map(x => (
                    <option key={x} value={x}>
                        Показать {x}
                    </option>
                ))}
            </select>
            {cityDataVisible && <div className="creator">
                В процессе попытки удаления Координат с id:{curId} возникли вопросы к зависимым городам, пожалуйста,
                свяжите нижестоящие города с другими координатами
                {cityData.map(city => {
                    return (
                        <div>
                            ID города: {city.id}, координаты:
                            <select className="pagination-selector"
                                    value={city.coordinates.id}
                                    onChange={e => {
                                        city.coordinates = coords.find(coord => coord.id.toString() === e.target.value)
                                        cityService.patchCity(city.id, city)
                                    }}
                                    onClick={() => getAllCoords()}
                            >
                                {coords.map(x => (
                                    <option key={x.id} value={x.id}>
                                        {coordsToString(x)}
                                    </option>
                                ))}
                            </select>
                        </div>
                    )

                })}

            </div>};
        </div>

    );
};

export default CoordCreator;