import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import fileDownload from 'react-file-download';

class DownloadOption extends React.Component {
    static propTypes = {
        downloadUrl: PropTypes.string.isRequired
    };

    constructor(props) {
        super(props);
        this.requestDownload = this.requestDownload.bind(this);
        this.state={
            loading:false
        }
    }

    requestDownload(event){
        this.setState({loading: true});
        axios.get(this.props.downloadUrl,{headers: {'Accept': 'text/plain'}}).then(response=>{
            this.setState({loading: false}, ()=> fileDownload(response.data,"unattached_atoms.txt"));
        }).catch(error=>{
            console.error(error);
            this.setState({loading:false});
        })
    }

    render(){
        if(this.state.loading){
            return <span><img src="/assets/images/loading.svg" style={{width: "32px", marginRight:"1em"}}/>Downloading data...</span>
        } else {
            return <a className="headinglink" onClick={this.requestDownload}>Download this data as text...</a>
        }
    }
}

export default DownloadOption;