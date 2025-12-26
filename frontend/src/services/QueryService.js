const API_BASE_URL = 'http://localhost:8080/api';

const queryService = {
    countCitiesAboveSeaLevel: async (meters) => {
        try {
            const response = await fetch(`${API_BASE_URL}/countAboveSeaLevel?meters=${meters}`);
            if (!response.ok) throw new Error(await response.text());
            return await response.json();
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }
    },

    getCitiesWithPopulationLessThan: async (population) => {
        try {
            const response = await fetch(`${API_BASE_URL}/citiesWithPopulationLessThan?population=${population}`);
            if (!response.ok) throw new Error(await response.text());
            return await response.json();
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }
    },

    getUniqueTelephoneCodes: async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/uniqueTelephoneCodes`);
            if (!response.ok) throw new Error(await response.text());
            return await response.json();
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }
    },

    calculateRoute: async (fromCityId, toCityId) => {
        try {
            const response = await fetch(`${API_BASE_URL}/calculateRoute?fromCityId=${fromCityId}&toCityId=${toCityId}`);
            if (!response.ok) throw new Error(await response.text());
            return await response.json();
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }
    },


    calculateMaxMinPopulationRoute: async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/maxMinPopulationRoute`);
            if (!response.ok) throw new Error(await response.text());
            return await response.json();
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Ошибка соединения с сервером.');
            }
            throw error;
        }
    },

};

export default queryService;