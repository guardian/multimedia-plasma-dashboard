import React from 'react';
import DataTable from './DataTable.jsx';
import ControlsBanner from './ControlsBanner.jsx';
import axios from 'axios';

class MainPage extends React.Component {
    constructor(props){
        super(props);

        this.state = {
            searchType: "date",
            monthFilter: null,
            userFilter: null,
            atomFilter: null,
            rawData: []
        };

        this.searchTypeUpdate = this.searchTypeUpdate.bind(this);
        this.monthFilterUpdate = this.monthFilterUpdate.bind(this);
        this.userFilterUpdate = this.userFilterUpdate.bind(this);
        this.atomFilterUpdate = this.atomFilterUpdate.bind(this);
    }

    loadData(){
        axios.get("/unattached-atoms/all").then(response=>{
            this.setState({rawData: response.data});
        }).catch(error=>console.error(error));
    }

    componentWillMount(){
        this.loadData();
    }
    searchTypeUpdate(newValue){

    }

    monthFilterUpdate(newValue){

    }

    userFilterUpdate(newValue){

    }

    atomFilterUpdate(newValue){

    }

    render(){
        return(<div>
            <ControlsBanner searchTypeChanged={this.searchTypeUpdate}
                            monthChanged={this.monthFilterUpdate}
                            userChanged={this.userFilterUpdate}
                            atomEntryChanged={this.atomFilterUpdate}/>
            <DataTable inputData={this.state.rawData}/>
        </div>)
    }
}

export default MainPage;
