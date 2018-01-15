import React from 'react';
import SearchTypeSelector from './SearchTypeSelector.jsx';
import MonthSelector from './MonthSelector.jsx';
import AtomEntry from './AtomEntry.jsx';
import PropTypes from 'prop-types';

const MODE_DATE="date";
const MODE_USER="user";

class ControlsBanner extends React.Component {
    static propTypes = {
        searchTypeChanged: PropTypes.func.isRequired,
        monthChanged: PropTypes.func.isRequired,
        userChanged: PropTypes.func.isRequired,
        atomEntryChanged: PropTypes.func.isRequired
    };

    constructor(props){
        super(props);
        this.state = {
            mode: MODE_DATE
        };
        this.searchTypeSelectorChanged = this.searchTypeSelectorChanged.bind(this);
    }

    searchTypeSelectorChanged(newValue){
        this.setState({mode: newValue});
        this.props.searchTypeChanged(newValue);
    }

    relevantCentreSelector(){
        if(this.state.mode===MODE_DATE){
            return <MonthSelector onSelectorChange={this.props.monthChanged} label="Month" internalName="id_month_selector"/>
        } else {
            return <p className="error">invalid mode</p>
        }
    }

    render() {
        return <div className="controls-banner">
            <SearchTypeSelector onSelectorChange={this.searchTypeSelectorChanged} label="Search by" internalName="id_search_type_selector"/>
            {this.relevantCentreSelector()}
            <AtomEntry onSelectorChange={this.props.atomEntryChanged} label="Find atom by ID" internalName="id_atom_entry"/>
        </div>
    }
}

export default ControlsBanner;