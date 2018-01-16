import React from 'react';
import DataTable from './DataTable.jsx';
import ControlsBanner from './ControlsBanner.jsx';
import ErrorMessage from './ErrorMessage.jsx';
import axios from 'axios';

class MainPage extends React.Component {
    constructor(props){
        super(props);

        this.state = {
            searchType: "date",
            monthFilter: null,
            userFilter: null,
            atomFilter: null,
            rawData: [],
            axiosError: null,
            systemConfig: {AtomToolDomain: "unknown"}
        };

        this.searchTypeUpdate = this.searchTypeUpdate.bind(this);
        this.monthFilterUpdate = this.monthFilterUpdate.bind(this);
        this.userFilterActivated = this.userFilterActivated.bind(this);
        this.userFilterDeactivated = this.userFilterDeactivated.bind(this);
        this.atomFilterUpdate = this.atomFilterUpdate.bind(this);
    }

    loadData(){
        const params = {
            month: this.state.monthFilter ? this.state.monthFilter : null
        };

        const param_string = Object.keys(params)
            .filter(key=>params[key])
            .reduce((acc,key)=>acc + "&" + key + "=" + params[key], "");

        const endpoint = this.state.userFilter ? "forUser/" + this.state.userFilter : "all";

        axios.get("/unattached-atoms/" + endpoint + "?" + param_string.slice(1)).then(response=>{
            this.setState({rawData: response.data, axiosError: null});
        }).catch(error=> {
                console.error(error);
                this.setState({axiosError: error});
            });
    }

    loadConfig(){
        return axios.get("/systemconfig").then(response=>{
            this.setState({systemConfig: response.data})
        }).catch(error=>{
            console.error(error);
            this.setState({axiosError: error});
        });
    }

    componentWillMount(){
        this.loadConfig().then(()=>this.loadData());
    }

    searchTypeUpdate(newValue){

    }

    monthFilterUpdate(newValue){
        console.log("monthFilterUpdate: " + newValue);

        this.setState({monthFilter: newValue, rawData: []},()=>this.loadData());
    }

    userFilterActivated(fieldname, newValue){
        console.log("user filter activated");
        this.setState({userFilter: newValue, rawData: []}, ()=>this.loadData());
    }

    userFilterDeactivated(fieldname, newValue){
        console.log("user filter deactivated");
        this.setState({userFilter: null, rawData: []}, ()=>this.loadData());
    }

    atomFilterUpdate(newValue){

    }

    render(){
        return(<div>
            <ControlsBanner searchTypeChanged={this.searchTypeUpdate}
                            monthChanged={this.monthFilterUpdate}
                            userChanged={this.userFilterUpdate}
                            atomEntryChanged={this.atomFilterUpdate}/>
            <ErrorMessage axiosError={this.state.axiosError}/>
            <DataTable inputData={this.state.rawData}
                       userFilterDeactivated={this.userFilterDeactivated}
                       userFilterActivated={this.userFilterActivated}
                       hasUserFilter={this.state.userFilter!==null}
                       atomToolDomain={this.state.systemConfig.AtomToolDomain}
            />
        </div>)
    }
}

export default MainPage;
