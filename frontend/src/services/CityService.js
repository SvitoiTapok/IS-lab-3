const API_BASE_URL = 'http://localhost:8080/api';

const cityService = {
    getAllCities: async (page, size, sortBy, sortOrder, nameFilter, climateFilter, humanFilter) => {
        try {
            const response = await fetch(`${API_BASE_URL}/getCities?page=${page}&size=${size}&sortBy=${sortBy}&sortOrder=${sortOrder}&name=${nameFilter}&climate=${climateFilter}&human=${humanFilter}`);
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
    addCity: async (cityData) => {
        try {
            const response = await fetch(`${API_BASE_URL}/addCity`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(cityData)
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
    patchCity: async (cityId, cityData) => {
        try {
            const response = await fetch(`${API_BASE_URL}/updateCity/${cityId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(cityData)
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
    deleteCity: async (cityId) => {
        try {
            const response = await fetch(`${API_BASE_URL}/deleteCity/${cityId}`, {
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
    },

    importCity: async (formData) => {
        try {
        const response = await fetch('http://localhost:8080/api/importCity', {
            method: 'POST',
            body: formData,
        });

        if (response.ok) {
            return await response.text();
        } else {
            throw new Error(await response.text())
        }
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }

    }


};

export default cityService;