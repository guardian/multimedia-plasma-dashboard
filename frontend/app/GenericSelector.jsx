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
        console.log("selector " + this.props.internalName + " changed to " + event.target.value);

        const newValue = event.target.value; //need to do this in order to access event.target in the callback below, as the event has been freed then
        this.setState({currentlySelected: newValue}, ()=>{
            this.props.onSelectorChange(newValue);  //only update the parent when we've finished updating our own state
        });
    }

    render(){
        return <span>
            <label htmlFor={this.props.internalName}>{this.props.label}</label>
            <select id={this.props.internalName} onChange={this.selectorChanged} defaultValue={this.state.currentlySelected}>
            {this.valueList.map(value=>
                <option value={value.internal}>{value.external}</option>
            )}
        </select>
        </span>
    }
}

export default GenericSelector;
