import React from 'react';
import {shallow,mount} from 'enzyme';
import AtomEntry from '../AtomEntry.jsx';
import sinon from 'sinon';

describe("AtomEntry", ()=>{
    test("should render an input with the provided entries and a label", ()=>{
        const mockChange = sinon.spy();
        const rendered=shallow(<AtomEntry onSelectorChange={mockChange} label="test entry" internalName="id_test_entry"/>);

        const input = rendered.find("input");

        expect(input.props().id).toEqual("id_test_entry");

        const label = rendered.find("label");
        expect(label.props().htmlFor).toEqual("id_test_entry");
        expect(label.text()).toEqual("test entry");
    });

    test("should update state and call the callback when the entry changes", ()=>{
        const mockChange = sinon.spy();
        const rendered=shallow(<AtomEntry onSelectorChange={mockChange} label="test entry" internalName="id_test_entry"/>);

        const input = rendered.find("input");
        expect(rendered.instance().state.currentText).toEqual("");

        input.simulate("change",{target: {value: "hello there"}});

        expect(rendered.instance().state.currentText).toEqual("hello there");

        expect(mockChange.calledOnce).toBeTruthy();
        expect(mockChange.calledWith("hello there")).toBeTruthy();
    })
});