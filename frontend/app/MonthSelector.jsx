import React from 'react';
import GenericSelector from './GenericSelector.jsx';
import PropTypes from 'prop-types';
import moment from 'moment';

class MonthSelector extends GenericSelector {
    static propTypes = {
        onSelectorChange: PropTypes.func.isRequired,
        label: PropTypes.string.isRequired,
        internalName: PropTypes.string.isRequired,
        toYear: PropTypes.number,
        toMonth: PropTypes.number
    };

    constructor(props){
        super(props);

        const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        let buildopts = [];

        const untilYear = this.props.toYear ? this.props.toYear : (new Date).getYear()+1900;
        const untilMonth = this.props.toMonth ? this.props.toMonth-1: (new Date).getMonth();

        for(let year=2017; year<=untilYear; ++year){
            for(let month=0; month<12; ++month){
                if(year===untilYear && month>untilMonth) break;
                buildopts.push({
                    internal: year.toString() + "_" + (month+1).toString(),
                    external: months[month] + " " + year.toString()
                });
            }
        }

        this.valueList = buildopts;

        this.state = {
            currentlySelected: untilYear.toString() + "_" + (untilMonth+1).toString()
        };
    }
}

export default MonthSelector;
