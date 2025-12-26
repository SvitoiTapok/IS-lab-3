import React, {useEffect, useMemo, useRef, useState} from 'react';
import './humanCreator.css';
import {
    flexRender,
    getCoreRowModel,
    getFilteredRowModel,
    getPaginationRowModel,
    getSortedRowModel,
    useReactTable
} from "@tanstack/react-table";
import humanService from "../services/HumanService";
import "../util/pagination.css"
import {useError} from "../util/ErrorContext";
import editIcon from "../imgs/edit-icon.png";
import deleteIcon from "../imgs/delete-icon.png";
import {useNavigate} from "react-router-dom";
import cityService from "../services/CityService";

const HumanCreator = () => {
    const navigate = useNavigate();
    const [data, setData] = useState({
        content: [],
        totalElements: 0,
        totalPages: 0
    });
    const [name, setName] = useState('');
    const [sorting, setSorting] = useState([]);
    const [pagination, setPagination] = useState({
        pageIndex: 0,
        pageSize: 3,
    });
    const {showError, showNotification} = useError();
    const [cityData, setCityData] = useState([])
    const [curId, setCurId] = useState(0)
    const [cityDataVisible, setCityDataVisible] = useState(false);

    const [selectorHumans, setSelectorHumans] = useState([]);
    const [selectorPage, setSelectorPage] = useState(0);
    const [selectorHasMore, setSelectorHasMore] = useState(true);


    const scrollPositionsRef = useRef(new Map());

    const selectorPageSize = 20

    const callServer = async () => {
        let sortBy = 'id';
        let sortOrder = 'asc';

        if (sorting.length > 0) {
            sortBy = sorting[0].id;
            sortOrder = sorting[0].desc ? 'desc' : 'asc';
        }

        await humanService.getHumans(
            pagination.pageIndex,
            pagination.pageSize,
            sortBy,
            sortOrder
        )
            .then(data => {
                setData(data)
            })
            .catch(err => {
                showError(err.toString());
            });
    }

    const loadMoreSelectorHumans = async () => {
        if (!selectorHasMore) return;
        try {
            const data = await humanService.getHumans(
                selectorPage,
                selectorPageSize,
                "id",
                "asc"
            );
            console.log(data.content)
            setSelectorHumans(prev => [...prev, ...data.content]);
            if (selectorPage + 1 >= data.totalPages) {
                setSelectorHasMore(false);
            } else {
                setSelectorPage(prev => prev + 1);
            }
        } catch (e) {
            showError(e.toString());
        }
    };

    useEffect(() => {
        callServer()
    }, [pagination.pageIndex, pagination.pageSize, sorting])

    const addHuman = async () => {
        if (name.trim() === '') {
            showError('Имя человека должно быть не пустой строкой')
            return;
        }

        try {
            await humanService.addHuman({name: name});
            await callServer()
            showNotification("Человек успешно добавлен")
        } catch (err) {
            showError(err.toString());
            return;
        }
        setName('');
    }

    const handleDelete = async (humanId) => {
        try {
            humanService.getCitiesByHuman(humanId).then(data => {
                    if (data.length === 0) {
                        humanService.deleteHuman(humanId).then(() => callServer());
                        showNotification('Человек успешно удален');
                        table.setPageIndex(0);
                        setCityData([])
                        setCityDataVisible(false)
                        return;
                    }
                    setCurId(humanId)
                    setCityData(data)
                    setCityDataVisible(true)

                    setSelectorHumans([]);
                    setSelectorPage(0);
                    setSelectorHasMore(true);
                    loadMoreSelectorHumans();
                }
            ).catch(e =>
                showError(e.message)
            )
            callServer();

        } catch (err) {
            showError('Ошибка при удалении города: ' + err.toString());
        }
    };

    const handleEdit = (human) => {
        navigate('/edit-human', {
            state: {
                human: human,
            }
        });
    };

    const saveScrollPosition = (cityId, position) => {
        scrollPositionsRef.current.set(cityId, position);
    };

    const getScrollPosition = (cityId) => {
        return scrollPositionsRef.current.get(cityId) || 0;
    };

    const SelectWithObserver = ({city}) => {
        const selectRef = useRef(null);
        const [isLastItemVisible, setIsLastItemVisible] = useState(false);
        const observerRef = useRef(null);

        useEffect(() => {
            const savedScrollPos = getScrollPosition(city.id);
            if (selectRef.current && savedScrollPos > 0) {
                selectRef.current.scrollTop = savedScrollPos;
            }

            if (!selectRef.current || !selectorHasMore) return;

            console.log("Setting up observer for city:", city.id);

            observerRef.current = new IntersectionObserver(
                (entries) => {
                    entries.forEach(entry => {
                        if (entry.isIntersecting && entry.target.getAttribute('data-last-item') === 'true') {
                            setTimeout(()=> setIsLastItemVisible(true), 100)

                            const currentScrollPos = selectRef.current.scrollTop;
                            saveScrollPosition(city.id, currentScrollPos);
                            console.log(`Saving scroll position for city ${city.id}: ${currentScrollPos}`);

                            loadMoreSelectorHumans().then(() => {
                                const restoredScrollPos = getScrollPosition(city.id);
                                if (selectRef.current) {
                                    selectRef.current.scrollTop = restoredScrollPos-50;
                            }})
                        } else if (!entry.isIntersecting && entry.target.getAttribute('data-last-item') === 'true') {
                            setIsLastItemVisible(false);
                        }
                    });
                },
                {
                    root: selectRef.current,
                    threshold: 0.8,
                    rootMargin: '10px'
                }
            );

            const options = selectRef.current.querySelectorAll('option');
            if (options.length > 0) {
                const lastOption = options[options.length - 1];
                observerRef.current.observe(lastOption);
            }

            return () => {
                if (observerRef.current) {
                    observerRef.current.disconnect();
                }
            }
        }, [selectorHasMore, city.id]);

        const handleScroll = () => {
            if (selectRef.current) {
                const currentScrollPos = selectRef.current.scrollTop;
                saveScrollPosition(city.id, currentScrollPos);
            }
        };

        const handleHumanChange = (e) => {
            const selectedHuman = selectorHumans.find(human => human.id.toString() === e.target.value);
            if (selectedHuman) {
                const updatedCity = {...city, human: selectedHuman};
                cityService.patchCity(city.id, updatedCity)
                    .then(() => showNotification(`Город с id ${city.id} теперь привязан к ${selectedHuman.name}`))
                    .catch(err => showError(err.toString()));
            }
        };

        return (
            <div style={{
                display: 'flex',
                alignItems: 'center',
                marginBottom: '15px'
            }}>
                ID города: {city.id}, губернатор:
                <select
                    ref={selectRef}
                    className="pagination-selector"
                    value={city.human.id}
                    size={3}
                    style={{
                        width: '200px',
                        height: '100px',
                        padding: '8px',
                        border: '2px solid #000',
                        borderRadius: '4px',
                        backgroundColor: 'white',
                        fontSize: '14px',
                        marginLeft: '10px'
                    }}
                    onChange={handleHumanChange}
                    // onScroll={handleScroll}
                >
                    {selectorHumans.map((x, index) => (
                        <option
                            key={x.id}
                            value={x.id}
                            data-last-item={index === selectorHumans.length - 1 ? "true" : "false"}
                        >
                            {x.name} (ID: {x.id})
                        </option>
                    ))}
                </select>
                {isLastItemVisible && (
                    <span style={{
                        marginLeft: '10px',
                        fontSize: '12px',
                        color: '#007bff',
                        fontStyle: 'italic'
                    }}>
                        Загрузка следующих элементов...
                    </span>
                )}
            </div>
        );
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
                accessorKey: 'name',
                header: 'Name',
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
            sorting,
            pagination
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
            <span>Люди</span>
            <div className="input-group">
                <input
                    placeholder="Имя человека"
                    value={name}
                    onChange={(e) => {
                        setName(e.target.value);
                    }}
                    onKeyPress={(e) => {
                        if (e.key === 'Enter') {
                            addHuman();
                        }
                    }}
                    className="name-input"/>
                <button onClick={() => {
                    addHuman()
                }}
                        className="create-button">
                    Добавить человека
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

            {cityDataVisible && (
                <div className="creator">
                    В процессе попытки удаления Человека с id:{curId} возникли вопросы к зависимым городам, пожалуйста,
                    свяжите нижестоящие города с другими людьми
                    {cityData.map((city) => (
                        <SelectWithObserver key={city.id} city={city}/>
                    ))}
                </div>
            )}
        </div>
    );
};

export default HumanCreator;