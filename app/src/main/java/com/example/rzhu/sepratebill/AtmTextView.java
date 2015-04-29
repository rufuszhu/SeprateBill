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
        text = text.replace(".","");
        text += s;
        try {
            Log.e(TAG,"text: " + text);
            double value = (double) Integer.parseInt(text);

            value = value/100d;
            Log.e(TAG,"value: " + value);
            text = precision.format(value);
            setText(text);
        }
        catch (NumberFormatException e){
            text = "00.0" + s;
            setText(text);
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
            text = "00.00";
            setText(text);
        }
    }

    public void clearText(){
        text = "00.00";
        setText(text);
    }

    private String chopLastChar(String str) {
        if (str.length() > 0 && str.charAt(str.length()-1)=='x') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

}
