import React from 'react';
import GenericSelector from './GenericSelector.jsx';
import PropTypes from 'prop-types';

class SearchTypeSelector extends GenericSelector {
    static propTypes = {
        onSelectorChange: PropTypes.func.isRequired,
        label: PropTypes.string.isRequired,
        internalName: PropTypes.string.isRequired
    };

    constructor(props){
        super(props);
        this.state = {
            currentlySelected: 'date'
        };
        this.valueList = [
            {internal: "date", external: "Date"},
            {internal: "user", external: "User"}
        ]
    }
}

export default SearchTypeSelector;
