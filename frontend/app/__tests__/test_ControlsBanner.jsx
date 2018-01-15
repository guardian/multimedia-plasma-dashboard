import React from 'react';
import {shallow,mount} from 'enzyme';
import ControlsBanner from '../ControlsBanner.jsx';
import sinon from 'sinon';

describe("ControlsBanner", ()=>{
    test("should render controls", ()=>{
        const searchTypeCB = sinon.spy();
        const genericCB = sinon.spy();

        const rendered=mount(<ControlsBanner
            searchTypeChanged={searchTypeCB} monthChanged={genericCB} userChanged={genericCB} atomEntryChanged={genericCB}/>);

        expect(rendered.find("SearchTypeSelector")).toBeTruthy();
        expect(rendered.find("MonthSelector")).toBeTruthy();
        expect(rendered.find("AtomEntry")).toBeTruthy();

        expect(rendered.instance().state.mode).toEqual("date");
    });


    test("should switch central controls on mode change", ()=>{
        const searchTypeCB = sinon.spy();
        const genericCB = sinon.spy();

        const rendered=mount(<ControlsBanner
            searchTypeChanged={searchTypeCB} monthChanged={genericCB} userChanged={genericCB} atomEntryChanged={genericCB}/>);

        const modeSelector = rendered.find("#id_search_type_selector");
        expect(rendered.instance().state.mode).toEqual("date");

        modeSelector.simulate("change",{target: {value: "user"}});
        rendered.update();

        expect(searchTypeCB.calledOnce).toBeTruthy();

        expect(rendered.instance().state.mode).toEqual("user");

    });
});