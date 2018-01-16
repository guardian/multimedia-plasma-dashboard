import React from 'react';
import SortableTable from 'react-sortable-table';
import moment from 'moment';
import PropTypes from 'prop-types';
import FilterButton from './FilterButton.jsx';

class DataTable extends React.Component {
    static propTypes = {
        dateFormat: PropTypes.string,
        inputData: PropTypes.object.isRequired,
        userFilterActivated: PropTypes.func.isRequired,
        userFilterDeactivated: PropTypes.func.isRequired,
        atomToolDomain: PropTypes.string.isRequired
    };

    constructor(props){
        super(props);
    }

    render(){
        const dateFormat = this.props.dateFormat ? this.props.dateFormat : "MMMM Do YYYY, h:mm:ss a";

        const columns = [
            {
                header: "Date Created",
                headerProps: {className: "dashboardheader"},
                key: "dateCreated",
                render: (date)=>moment(date).format(dateFormat),
                defaultSorting: 'DESC'
            },
            {
                header: "Atom ID",
                headerProps: {className: "dashboardheader"},
                key: "AtomID",
                render: (atomid)=><a href={"https://"+ this.props.atomToolDomain + "/videos/" + atomid} target="_blank">{atomid}</a>
            },
            {
                header: "User",
                headerProps: {className: "dashboardheader"},
                key: "userEmail",
                render: (user)=><span><FilterButton fieldName="userEmail" values={user} type="plus"
                                                    onActivate={this.props.userFilterActivated}
                                                    onDeactivate={this.props.userFilterDeactivated}
                                                    isActive={this.props.hasUserFilter}
                                        />
                    {user}
                                </span>
            },
            {
                header: "Date Updated",
                headerProps: {className: "dashboardheader"},
                key: "dateUpdated",
                render: (date)=>moment(date).format(dateFormat)
            }
        ];

        const style = {
            borderCollapse: "collapse"
        };

        const iconStyle = {
            color: '#aaa',
            paddingLeft: '5px',
            paddingRight: '5px'
        };

        return <SortableTable data={this.props.inputData} columns={columns} style={style} iconStyle={iconStyle}/>
    }
}

export default DataTable;