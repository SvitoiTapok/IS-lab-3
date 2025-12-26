import React, {useEffect, useMemo, useState} from "react";
import editIcon from "../imgs/edit-icon.png";
import deleteIcon from "../imgs/delete-icon.png";
import {
    flexRender,
    getCoreRowModel,
    getFilteredRowModel,
    getPaginationRowModel,
    getSortedRowModel,
    useReactTable
} from "@tanstack/react-table";
import {useNavigate} from "react-router-dom";
import {useError} from "../util/ErrorContext";
import humanService from "../services/HumanService";
import ImportService from "../services/ImportService";

const ImportResultStory = () => {
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
    const {showError, showNotification} = useError();
    // const [cityData, setCityData] = useState([])
    // const [curId, setCurId] = useState(0)
    // const [cityDataVisible, setCityDataVisible] = useState(false);
    //
    // const [selectorHumans, setSelectorHumans] = useState([]);
    // const [selectorPage, setSelectorPage] = useState(0);
    // const [selectorHasMore, setSelectorHasMore] = useState(true);
    useEffect(() => {
        callServer()
        const intervalId = setInterval(() => {
            callServer();
        }, 5000)

        return () => clearInterval(intervalId);
    }, [pagination.pageIndex, pagination.pageSize, sorting]);
    const callServer = async () => {
        let sortBy = 'id';
        let sortOrder = 'asc';

        if (sorting.length > 0) {
            sortBy = sorting[0].id;
            sortOrder = sorting[0].desc ? 'desc' : 'asc';
        }

        await ImportService.getImports(
            pagination.pageIndex,
            pagination.pageSize,
            sortBy,
            sortOrder
        )
            .then(data => {
                setData(data)
                console.log(data.content)
            })
            .catch(err => {
                showError(err.toString());
            });
    }

const columns = useMemo(
    () => [
        {
            accessorKey: 'id',
            header: 'ID',
            cell: info => parseInt(info.getValue()),
            enableSorting: true,
        },
        {
            accessorKey: 'status',
            header: 'Статус',
            cell: info => parseInt(info.getValue()),
            enableSorting: true,
        },
        {
            accessorKey: 'description',
            header: 'Описание',
            cell: info => info.getValue(),
            enableSorting: true,
        },
        {
            accessorKey: 'creationDate',
            header: 'Дата создания',
            cell: info => info.getValue(),
            enableSorting: true,
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
        <span>История импортов</span>
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
    </div>
);
};

export default ImportResultStory;