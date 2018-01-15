import React from 'react';
import PropTypes from 'prop-types';

class AtomEntry extends React.Component {
    static propTypes = {
        onSelectorChange: PropTypes.func.isRequired,
        label: PropTypes.string.isRequired,
        internalName: PropTypes.string.isRequired
    };

    constructor(props){
        super(props);
        this.state = {
            currentText: ""
        };
        this.selectorChanged = this.selectorChanged.bind(this);
    }

    selectorChanged(event){
        this.setState({currentText: event.target.value}, ()=>{
            this.props.onSelectorChange(event.target.value);  //only update the parent when we've finished updating our own state
        });
    }

    render(){
        return <span>
            <label htmlFor={this.props.internalName}>{this.props.label}</label>
            <input id={this.props.internalName} onChange={this.selectorChanged}/>
        </span>
    }
}

export default AtomEntry;