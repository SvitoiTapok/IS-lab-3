import React, {useState, useEffect, useMemo} from 'react';
import {useNavigate} from 'react-router-dom';
import {
    useReactTable,
    getCoreRowModel,
    getSortedRowModel,
    getFilteredRowModel,
    getPaginationRowModel,
    flexRender,
} from '@tanstack/react-table';
import './table.css';
import '../util/pagination.css';
import cityService from "../services/CityService";
import editIcon from '../imgs/edit-icon.png';
import deleteIcon from '../imgs/delete-icon.png';
import {useError} from "../util/ErrorContext";

const MainTable = () => {
    const navigate = useNavigate();
    const [data, setData] = useState({
        content: [],
        totalElements: 0,
        totalPages: 0
    });
    const [sorting, setSorting] = useState([]);
    const [pagination, setPagination] = useState({
        pageIndex: 0,
        pageSize: 3,
    });

    const [nameFilter, setNameFilter] = useState('')
    const [climateFilter, setClimateFilter] = useState('')
    const [humanFilter, setHumanFilter] = useState('')
    const {showError, showNotification} = useError();

    //lab2
    const [file, setFile] = useState(null)
    const [uploading, setUploading] = useState(null)


    const coordsToString = (coord) => {
        if (coord == null)
            return ""
        return 'id:' + coord.id + ' (' + coord.x + ', ' + coord.y + ')'
    }


    const callServer = async () => {
        let sortBy = 'id';
        let sortOrder = 'asc';

        if (sorting.length > 0) {
            sortBy = sorting[0].id;
            sortOrder = sorting[0].desc ? 'desc' : 'asc';
        }
        console.log("PI" + pagination.pageIndex)

        await cityService.getAllCities(
            pagination.pageIndex,
            pagination.pageSize,
            sortBy,
            sortOrder,
            nameFilter,
            climateFilter,
            humanFilter
        )
            .then(data => {
                console.log("basdf"+ pagination.pageIndex+ " " + pagination.pageSize)
                setData(data);
                //setPagination({pageIndex: data.pageable.pageNumber, pageSize: data.pageable.pageSize})

                console.log("basdf"+ data.pageable.pageNumber + " " + data.pageable.pageSize)
            })
            .catch(err => {
                showError(err.toString());
            });
    }
    useEffect(() => {
        console.log("UF" + pagination.pageIndex)
        callServer()
        const intervalId = setInterval(callServer, 5000)

        return () => clearInterval(intervalId);
    }, [pagination.pageIndex, pagination.pageSize, sorting, nameFilter, climateFilter, humanFilter]);

    const handleDelete = async (cityId) => {
        try {
            await cityService.deleteCity(cityId);
            table.setPageIndex(0);
            callServer().then(()=>showNotification('Город успешно удален'));

            showNotification('Город успешно удален');
        } catch (err) {
            showError('Ошибка при удалении города: ' + err.toString());
        }
    };

    const handleEdit = (city) => {
        navigate('/edit-city', {
            state: {
                city: city,
            }
        });
    };
    const columns = useMemo(
        () => [
            {
                accessorKey: 'id',
                header: 'ID',
                cell: info => info.getValue(),
            },
            {
                accessorKey: 'name',
                header: 'Название',
                cell: info => info.getValue(),
                enableSorting: true,
                enableColumnFilter: true
            },
            {
                accessorKey: 'coordinates',
                header: 'Координаты',
                cell: info => coordsToString(info.getValue()),
                enableSorting: true,
                enableColumnFilter: false
            },
            {
                accessorKey: 'creationDate',
                header: 'Дата создания записи',
                cell: info => new Date(info.getValue()).toLocaleString(),
                enableColumnFilter: false
            },
            {
                accessorKey: 'area',
                header: 'Площадь',
                cell: info => info.getValue(),
                enableSorting: true,
                enableColumnFilter: false
            },
            {
                accessorKey: 'population',
                header: 'Население',
                cell: info => info.getValue(),
                enableSorting: true,
                enableColumnFilter: false
            },
            {
                accessorKey: 'establishmentDate',
                header: 'Дата основания',
                cell: info => info.getValue(),
                enableSorting: true,
                enableColumnFilter: false
            },
            {
                accessorKey: 'capital',
                header: 'Столица',
                cell: info => {
                    if (info.getValue()) return "YES"; else return "NO"
                },
                enableSorting: true,
            },
            {
                accessorKey: 'metersAboveSeaLevel',
                header: 'Высота над уровнем моря',
                cell: info => info.getValue(),
                enableSorting: true,
            },
            {
                accessorKey: 'populationDensity',
                header: 'Плотность населения',
                cell: info => info.getValue(),
                enableSorting: true,
            },
            {
                accessorKey: 'telephoneCode',
                header: 'Телефонный код',
                cell: info => info.getValue(),
                enableSorting: true,
            },
            {
                accessorKey: 'climate',
                header: 'Климат',
                cell: info => info.getValue(),
                enableSorting: true,
                enableColumnFilter: true
            },
            {
                accessorKey: 'human',
                header: 'Губернатор',
                cell: info => info.getValue().name,
                enableSorting: true,
                enableColumnFilter: true
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


        ],
        []
    );

    const table = useReactTable({
        data: data.content,
        columns,
        state: {
            pagination,
            sorting,
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

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleUpload = async () => {
        // if (!file) return;

        setUploading(true);
        const formData = new FormData();
        formData.append('file', file);

        try {
            cityService.importCity(formData).then((result) =>
            {showNotification(result.toString()); callServer()}
            ).catch((result) => showError(result.toString()))
        } finally {
            setUploading(false);
        }
    };

    return (
        <div className="creator" id="city_creator">
            <span>Основная таблица</span>
            <div className="p-3">
                <span>Отфильтровать по имени</span>
                <input
                    placeholder="Фильтр по имени..."
                    value={nameFilter}
                    onChange={e => {
                        setNameFilter(e.target.value);
                    }}
                    className="name-input"
                />
            </div>
            <div className="p-3">
                <span>Отфильтровать по климату</span>
                <input
                    placeholder="Фильтр по климату..."
                    value={climateFilter}
                    onChange={e => {
                        setClimateFilter(e.target.value);
                    }}
                    className="name-input"
                />
            </div>
            <div className="p-3">
                <span>Отфильтровать по климату</span>
                <input
                    placeholder="Фильтр по имени губернатора..."
                    value={humanFilter}
                    onChange={e => {
                        setHumanFilter(e.target.value);
                    }}
                    className="name-input"
                />
            </div>
            <table className="w-full">
                <thead>
                {table.getHeaderGroups().map(headerGroup => (
                    <tr key={headerGroup.id}>
                        {headerGroup.headers.map(header => (
                            <th
                                key={header.id}
                                className="text-left border"
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
                        onClick={() => {table.setPageIndex(0)}}
                        disabled={!table.getCanPreviousPage()}
                >
                    {'<<'}
                </button>
                <button className="vectors"
                        onClick={() => {table.previousPage();}}
                        disabled={!table.getCanPreviousPage()}
                >
                    {'<'}
                    {/*{ return {pageIndex: prev.pageIndex-1, pageSize: prev.pageSize}})}}*/}
                </button>
                <button className="vectors"
                        onClick={() => {table.nextPage()}}
                        disabled={!table.getCanNextPage()}
                >
                    {'>'}
                </button>
                <button className="vectors"
                        onClick={() => {table.setPageIndex(table.getPageCount() - 1)}}
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
            <div>
                <input
                    type="file"
                    accept=".json,.xml,.csv"
                    onChange={handleFileChange}
                    className="custom-file-input"
                />
                <button onClick={handleUpload} disabled={uploading || !file} className={(uploading||!file)?"create-button-disabled":"create-button"}>
                    {uploading ? 'Загрузка...' : 'Импортировать'}
                </button>
            </div>
        </div>
    );
};

export default MainTable;