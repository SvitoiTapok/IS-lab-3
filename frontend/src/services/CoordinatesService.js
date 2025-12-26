const API_BASE_URL = 'http://localhost:8080/api';

const coordService = {
    getAllCoordinates: async (page, size, sortBy, sortOrder) => {
        try {
            const response = await fetch(`${API_BASE_URL}/getCoordinates?page=${page}&size=${size}&sortBy=${sortBy}&sortOrder=${sortOrder}`);
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
    addCoordinates: async (coordinatesData) => {
        try {
            const response = await fetch(`${API_BASE_URL}/addCoordinates`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(coordinatesData)
            });
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
    patchCoord: async (coordId, coordData) => {
        try {
            const response = await fetch(`${API_BASE_URL}/updateCoord/${coordId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(coordData)
            });
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
    getCitiesByCoord: async (id) => {
        try {
            const response = await fetch(`${API_BASE_URL}/getCitiesByCoordId?id=${id}`, {
                method: 'GET',
            });
            if (!response.ok) {
                throw new Error(await response.text());
            }
            return response.json();
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }
    },
    deleteCoord: async (coordId) => {
        try {
            const response = await fetch(`${API_BASE_URL}/deleteCoord/${coordId}`, {
                method: 'DELETE',
            });
            if (!response.ok) {
                throw new Error(await response.text());
            }
            return 0;
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }
    }

};

export default coordService;