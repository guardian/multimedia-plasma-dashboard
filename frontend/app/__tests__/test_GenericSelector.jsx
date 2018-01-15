import React from 'react';
import {shallow,mount} from 'enzyme';
import GenericSelector from '../GenericSelector.jsx';
import sinon from 'sinon';

class TestSelector extends GenericSelector {
    constructor(props){
        super(props);
        this.valueList = [
            {internal: "option_1", external: "Option 1"},
            {internal: "option_2", external: "Option 2"}
        ];
        this.state.currentlySelected = "option_2";
    }
}
describe("GenericSelector", ()=>{
    test("should render a select with the provided entries and a label", ()=>{
        const mockChange = sinon.spy();
        const rendered=shallow(<TestSelector onSelectorChange={mockChange} label="test entry" internalName="id_test_entry"/>);

        const selector = rendered.find("select");
        expect(selector.childAt(0).props().value).toEqual("option_1");
        expect(selector.childAt(0).text()).toEqual("Option 1");

        expect(selector.childAt(1).props().value).toEqual("option_2");
        expect(selector.childAt(1).text()).toEqual("Option 2");

        expect(selector.props().defaultValue).toEqual("option_2");
        expect(selector.children().length).toEqual(2);

        expect(selector.props().id).toEqual("id_test_entry");

        const label = rendered.find("label");
        expect(label.props().htmlFor).toEqual("id_test_entry");
        expect(label.text()).toEqual("test entry");
    });

    test("should update statae and call the callback when the entry changes", ()=>{
        const mockChange = sinon.spy();
        const rendered=shallow(<TestSelector onSelectorChange={mockChange} label="test entry" internalName="id_test_entry"/>);

        const selector = rendered.find("select");
        expect(rendered.instance().state.currentlySelected).toEqual("option_2");

        selector.simulate("change",{target: {value: "option_1"}});

        expect(rendered.instance().state.currentlySelected).toEqual("option_1");

        expect(mockChange.calledOnce).toBeTruthy();
        expect(mockChange.calledWith("option_1")).toBeTruthy();
    })
});