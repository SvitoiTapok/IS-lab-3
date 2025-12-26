const API_BASE_URL = 'http://localhost:8080/api';

const humanService = {
    getHumans: async (page, size, sortBy, sortOrder) => {
        try {
            const response = await fetch(`${API_BASE_URL}/getHumans?page=${page}&size=${size}&sortBy=${sortBy}&sortOrder=${sortOrder}`);
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
    addHuman: async (humanData) => {
        try {
            const response = await fetch(`${API_BASE_URL}/addHuman`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(humanData)
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
    patchHuman: async (humanId, humanData) => {
        try {
            const response = await fetch(`${API_BASE_URL}/updateHuman/${humanId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(humanData)
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
    getCitiesByHuman: async (id) => {
        try {
            const response = await fetch(`${API_BASE_URL}/getCitiesByHumanId?id=${id}`, {
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
    deleteHuman: async (humanId) => {
        try {
            const response = await fetch(`${API_BASE_URL}/deleteHuman/${humanId}`, {
                method: 'DELETE',
            });
            if (!response.ok) {
                // if(response.status===429)thro
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

export default humanService;