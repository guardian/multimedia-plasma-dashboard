import React from 'react';
import SortableTable from 'react-sortable-table';
import moment from 'moment';
import PropTypes from 'prop-types';

class DataTable extends React.Component {
    static propTypes = {
        dateFormat: PropTypes.string,
        inputData: PropTypes.object.isRequired
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
                key: "AtomID"
            },
            {
                header: "User",
                headerProps: {className: "dashboardheader"},
                key: "userEmail"
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