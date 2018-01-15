import React from 'react';
import {shallow,mount} from 'enzyme';
import SearchTypeSelector from '../SearchTypeSelector.jsx';
import sinon from 'sinon';

describe("MonthSelector", ()=>{
    test("should prepare entries for Jan 2017 until told to", ()=>{
        const rendered=shallow(<SearchTypeSelector/>);

        expect(rendered.instance().valueList).toEqual([
            {internal: "date", external: "Date"},
            {internal: "user", external: "User"}
        ]);
    })
});