import React, { useState } from 'react'
const options = ["I", "M", "S"];
const UnitSelector = (props) => {
    const [selected, setSelected] = useState(options[0]);

    return (
        <form>
            Units:
            <select
                value={selected}
                onChange={e => {
                    setSelected(e.target.value);
                    props.onSelectionChange(e.target.value);
                }}>
                {options.map((value) => (
                    <option value={value} key={value}>
                        {value}
                    </option>
                ))}
            </select>
        </form>
    );
};

export default UnitSelector;