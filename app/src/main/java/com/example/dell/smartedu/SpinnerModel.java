package com.example.dell.smartedu;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class SpinnerModel{
    private int selected;
    private ArrayAdapter adapter;


    public SpinnerModel(Context parent,ArrayList<String> list) {
        adapter = new ArrayAdapter(parent,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public ArrayAdapter getAdapter() {
        return adapter;
    }

    public String getText() {
        return (String) adapter.getItem(selected);
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }


}