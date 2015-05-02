package com.example.rzhu.sepratebill;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by rzhu on 4/24/2015.
 */
public class AtmTextView extends TextView {
    private static final String TAG = AtmTextView.class.getSimpleName();
    private String text;
    private static final DecimalFormat precision = new DecimalFormat("00.00");

    public AtmTextView(Context context) {
        super(context);
        text = "";
    }

    public AtmTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        text = "";

    }

    public AtmTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        text = "";
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

    public void enterText(String s){
        //disallow value bigger than 9999.99
        if(text.length()<=6) {
            text = text.replace(".", "");
            text += s;
            try {
                double value = (double) Integer.parseInt(text);

                value = value / 100d;
                text = precision.format(value);
                setText(text);
            } catch (NumberFormatException e) {
                text = "00.0" + s;
                setText(text);
            }
        }
    }

    public void deleteText(){
        text = text.replace(".","");
        text = chopLastChar(text);
        try {
            double value = (double) Integer.parseInt(text);
            value = value/100d;

            text = precision.format(value);
            setText(text);
        }
        catch (NumberFormatException e){
            clearText();
        }
    }

    public void clearText(){
        text = "";
        setText(text);
    }

    public void setText(String newText){
        text = newText;
        super.setText(newText);
    }

    private String chopLastChar(String str) {
        if (str.length() > 0) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

}
