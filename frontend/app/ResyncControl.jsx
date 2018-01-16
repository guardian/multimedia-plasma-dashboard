import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

class ResyncControl extends React.Component {
    static propTypes = {
        atomId: PropTypes.string.isRequired
    };

    constructor(props){
        super(props);
        this.state = {
            loading: false
        };

        this.resyncClicked = this.resyncClicked.bind(this);
    }

    resyncClicked(event){
        this.setState({loading: true});

        axios.put("/pluto-resync/" + this.props.atomId).then(response=>{
            this.setState({loading: false});
        }).catch(error=>console.error(error));

    }

    render(){
        if(this.state.loading){
            return <img style="width: 32px" src="/images/loading.svg"/>
        } else {
            return <button onClick={this.resyncClicked}>Resync to pluto</button>
        }
    }
}

export default ResyncControl;