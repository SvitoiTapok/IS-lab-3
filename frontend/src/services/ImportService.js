import humanService from "./HumanService";

const API_BASE_URL = 'http://localhost:8080/api';
//import {useError} from "../util/ErrorContext";
const ImportService = {

    getImports: async (page, size, sortBy, sortOrder) => {
        try {
            const response = await fetch(`${API_BASE_URL}/getImports?page=${page}&size=${size}&sortBy=${sortBy}&sortOrder=${sortOrder}`);
            if (!response.ok) {
                throw new Error(await response.text());
            }
            return await response.json();
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }
    },
    download: async (id) => {
        try {
            const response = await fetch(`${API_BASE_URL}/download/${id}`);
            if (!response.ok) {
                throw new Error(await response.text());
            }
            // Получаем имя файла из заголовков
            const contentDisposition = response.headers.get('Content-Disposition');
            let downloadName="file.json"
            console.log('All headers:');
            for (const [key, value] of response.headers.entries()) {
                console.log(`${key}: ${value}`);
            }
            if (contentDisposition) {
                const match = contentDisposition.match(/filename="(.+)"/);
                console.log(match)
                if (match && match[1]) {
                    downloadName = match[1];
                }
            }

            const blob = await response.blob();

            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = downloadName;
            document.body.appendChild(link);
            link.click();

            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }
    },
}
export default ImportService;