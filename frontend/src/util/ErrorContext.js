import React, {createContext, useContext, useState} from 'react';

const ErrorContext = createContext();

export const ErrorProvider = ({children}) => {
    const [errors, setErrors] = useState([]);

    const showError = async (message) => {
        const id = Date.now()
        const type = "err"
        setErrors([...errors, {id, message, type}]);
        setTimeout(() => hideError(), 3005);
    };
    const showNotification = async (message) => {
        const id = Date.now()
        const type = "notif"
        setErrors([...errors, {id, message, type}]);
        setTimeout(() => hideError(), 3005);
    };
    const hideError = async () =>{
        setErrors(errors => errors.filter(e => { return Date.now() - e.id < 3000}));
    }



    return (
        <ErrorContext.Provider value={{showError, hideError, showNotification}}>
            {children}

            <div style={{
                position: 'fixed',
                top: '20px',
                right: '20px',
                zIndex: 1000
            }}>
                {errors.map((error) => (
                        <div style={{
                            marginLeft: '10px',
                            marginBottom: '10px',
                            color: 'white',
                            padding: '15px',
                            borderRadius: '5px',
                            background: error.type === "err" ? "red" : "green",
                            cursor: 'pointer'

                        }}
                             key={error.id}>
                            {error.message}
                            <button
                                onClick={() => hideError(error.id)}
                                style={{marginLeft: '10px', background: 'white', border: 'none'}}
                            >
                                ×
                            </button>
                        </div>

                    )
                )}
            </div>
        </ErrorContext.Provider>
    );
};

export const useError = () => {
    return useContext(ErrorContext);
};