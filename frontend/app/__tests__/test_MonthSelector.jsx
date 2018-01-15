import React from 'react';
import {shallow,mount} from 'enzyme';
import MonthSelector from '../MonthSelector.jsx';
import sinon from 'sinon';

describe("MonthSelector", ()=>{
    test("should prepare entries for Jan 2017 until told to", ()=>{
        const rendered=shallow(<MonthSelector toYear={2018} toMonth={4}/>);

        expect(rendered.instance().valueList).toEqual([
            {"external": "Jan 2017", "internal": "2017_1"},
            {"external": "Feb 2017", "internal": "2017_2"},
            {"external": "Mar 2017", "internal": "2017_3"},
            {"external": "Apr 2017", "internal": "2017_4"},
            {"external": "May 2017", "internal": "2017_5"},
            {"external": "Jun 2017", "internal": "2017_6"},
            {"external": "July 2017", "internal": "2017_7"},
            {"external": "Aug 2017", "internal": "2017_8"},
            {"external": "Sep 2017", "internal": "2017_9"},
            {"external": "Oct 2017", "internal": "2017_10"},
            {"external": "Nov 2017", "internal": "2017_11"},
            {"external": "Dec 2017", "internal": "2017_12"},
            {"external": "Jan 2018", "internal": "2018_1"},
            {"external": "Feb 2018", "internal": "2018_2"},
            {"external": "Mar 2018", "internal": "2018_3"},
            {"external": "Apr 2018", "internal": "2018_4"},
            ]);
    })
});