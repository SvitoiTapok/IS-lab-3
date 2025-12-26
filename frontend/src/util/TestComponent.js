import React, { useRef, useState, useEffect } from 'react';

export const TestComponent = ({ children }) => {
    const selectRef = useRef(null);
    const selectRef2 = useRef(null);
    const [visibleOptions, setVisibleOptions] = useState(new Set());
    const [visibleOptions2, setVisibleOptions2] = useState(new Set());

    useEffect(() => {
        let observer = new IntersectionObserver(
            (entries) => {
                entries.forEach(entry => {
                    const optionValue = entry.target.getAttribute('data-value');
                    console.log(optionValue)
                    if(optionValue==="199"){
                        console.log("brug")
                    }
                });
            },
            {
                root: selectRef.current,
                threshold: 0.5,
                rootMargin: '0px'
            }
        );

        let observer2 = new IntersectionObserver(
            (entries) => {
                entries.forEach(entry => {
                    const optionValue = entry.target.getAttribute('data-value');
                    setVisibleOptions2(prev => {
                        const newSet = new Set(prev);
                        if (entry.isIntersecting) {
                            newSet.add(optionValue);
                        } else {
                            newSet.delete(optionValue);
                        }
                        return newSet;
                    });
                });
            },
            {
                root: selectRef2.current,
                threshold: 0.5,
                rootMargin: '0px'
            }
        );

        const options = selectRef.current?.querySelectorAll('.option-item');
        options?.forEach(option => {
            observer.observe(option);
        });

        const options2 = selectRef2.current?.querySelectorAll('.option-item');
        options2?.forEach(option => {
            observer2.observe(option);
        });

        return () => {
            observer.disconnect();
            observer2.disconnect(); // ✅ Добавляем очистку второго observer
        };
    }, []);

    return (
        <div>
            <select
                ref={selectRef}
                size={3}
                style={{ height: '150px', width: '200px' }}
            >
                {Array.from({ length: 200 }, (_, i) => (
                    <option
                        key={i}
                        value={i}
                        className="option-item"
                        data-value={i}
                        style={{
                            padding: '5px',
                            backgroundColor: visibleOptions.has(i.toString()) ? 'lightgreen' : 'transparent'
                        }}
                    >
                        Option {i + 1}
                    </option>
                ))}
            </select>

            <div style={{ marginTop: '10px' }}>
                <strong>Видимые опции 1:</strong>
                {Array.from(visibleOptions).join(', ')}
            </div>

            <select
                ref={selectRef2}
                multiple
                size={5}
                style={{ height: '150px', width: '200px', marginTop: '20px' }}
            >
                {Array.from({ length: 20 }, (_, i) => (
                    <option
                        key={i}
                        value={i}
                        className="option-item"
                        data-value={i}
                        style={{
                            padding: '5px',
                            backgroundColor: visibleOptions2.has(i.toString()) ? 'lightgreen' : 'transparent'
                        }}
                    >
                        Option {i + 1}
                    </option>
                ))}
            </select>

            <div style={{ marginTop: '10px' }}>
                <strong>Видимые опции 2:</strong>
                {Array.from(visibleOptions2).join(', ')}
            </div>
        </div>
    );
};

export default TestComponent;