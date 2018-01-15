import React from 'react';
import PropTypes from 'prop-types';

class GenericSelector extends React.Component {
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

        ];

        this.selectorChanged = this.selectorChanged.bind(this);
    }

    selectorChanged(event){
        this.setState({currentlySelected: event.target.value}, ()=>{
            this.props.onSelectorChange(event.target.value);  //only update the parent when we've finished updating our own state
        });
    }

    render(){
        return <span>
            <label htmlFor={this.props.internalName}>{this.props.label}</label>
            <select id={this.props.internalName} onChange={this.selectorChanged}>
            {this.valueList.map(value=>
                <option name={value.internal} selected={this.state.currentlySelected===value.internal}>{value.external}</option>
            )}
        </select>
        </span>
    }
}

export default GenericSelector;
