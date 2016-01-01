package com.example.dell.smartedu;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by kamya batra on 2/01/2016.
 */
public class ModelViewHolder {

    private CheckBox checkBox;
    private TextView textView;

    public ModelViewHolder()
    {
    }

    public ModelViewHolder(TextView textView, CheckBox checkBox)
    {
        this.checkBox = checkBox;
        this.textView = textView;
    }

    public CheckBox getCheckBox()
    {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox)
    {
        this.checkBox = checkBox;
    }

    public TextView getTextView()
    {
        return textView;
    }

    public void setTextView(TextView textView)
    {
        this.textView = textView;
    }
}
