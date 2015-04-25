package com.example.rzhu.sepratebill;

import android.content.Context;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by rzhu on 4/24/2015.
 */
public class AtmTextView extends TextView {
    private String text;
    private static final DecimalFormat precision = new DecimalFormat("00.00");

    public AtmTextView(Context context) {
        super(context);
        text = "";
    }

    public void enterText(String s){
        text.replace(".","");
        text += s;
        try {
            double value = Double.parseDouble(text);
            value = value/100;
            text = String.valueOf(value);
            setText(precision.format(text));
        }
        catch (NumberFormatException e){
            text = "00.0" + s;
            setText(text);
        }

    }

    public void deleteText(){

    }

}
