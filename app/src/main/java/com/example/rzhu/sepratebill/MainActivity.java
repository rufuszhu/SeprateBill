package com.example.rzhu.sepratebill;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final DecimalFormat precision = new DecimalFormat("00.00");
    private Button tip_20,tip_15,tip_10,tip_0,tip_btn;
    private TextView bill_clear, bill_del, bill_0, bill_1, bill_2, bill_3, bill_4, bill_5, bill_6, bill_7, bill_8, bill_9;
    private TextView ppl_clear, ppl_del, ppl_0, ppl_1, ppl_2, ppl_3, ppl_4, ppl_5, ppl_6, ppl_7, ppl_8, ppl_9;
    private TextView tv_tip_result,tv_total_result,tv_share_num,tv_share_back, tv_share_minus, tv_share_plus;
    private AtmTextView tv_bill_result;
    private TableLayout number_pad, share_num_pad;
    private LinearLayout ll_tip_col;

    private double tipAmount;
    private double totalAmount;
    private double tipPercent;
    private int numOfShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bill_clear = (TextView) findViewById(R.id.bill_clear);
        bill_del = (TextView) findViewById(R.id.bill_del);
        bill_0 = (TextView) findViewById(R.id.bill_0);
        bill_1 = (TextView) findViewById(R.id.bill_1);
        bill_2 = (TextView) findViewById(R.id.bill_2);
        bill_3 = (TextView) findViewById(R.id.bill_3);
        bill_4 = (TextView) findViewById(R.id.bill_4);
        bill_5 = (TextView) findViewById(R.id.bill_5);
        bill_6 = (TextView) findViewById(R.id.bill_6);
        bill_7 = (TextView) findViewById(R.id.bill_7);
        bill_8 = (TextView) findViewById(R.id.bill_8);
        bill_9 = (TextView) findViewById(R.id.bill_9);


        ppl_1 = (TextView) findViewById(R.id.ppl_1);
        ppl_2 = (TextView) findViewById(R.id.ppl_2);
        ppl_3 = (TextView) findViewById(R.id.ppl_3);
        ppl_4 = (TextView) findViewById(R.id.ppl_4);
        ppl_5 = (TextView) findViewById(R.id.ppl_5);
        ppl_6 = (TextView) findViewById(R.id.ppl_6);
        ppl_7 = (TextView) findViewById(R.id.ppl_7);
        ppl_8 = (TextView) findViewById(R.id.ppl_8);
        ppl_9 = (TextView) findViewById(R.id.ppl_9);

        tip_0 = (Button) findViewById(R.id.tip_0);
        tip_10 = (Button) findViewById(R.id.tip_10);
        tip_15 = (Button) findViewById(R.id.tip_15);
        tip_20 = (Button) findViewById(R.id.tip_20);

        number_pad = (TableLayout) findViewById(R.id.number_pad);
        share_num_pad = (TableLayout) findViewById(R.id.share_num_pad);

        tv_bill_result = (AtmTextView) findViewById(R.id.tv_bill_result);
        tv_tip_result = (TextView) findViewById(R.id.tv_tip_result);
        tv_total_result = (TextView) findViewById(R.id.tv_total_result);
        tv_share_num = (TextView) findViewById(R.id.tv_share_num);
        tv_share_back = (TextView) findViewById(R.id.tv_share_back);
        tv_share_minus = (TextView) findViewById(R.id.tv_share_minus);
        tv_share_plus = (TextView) findViewById(R.id.tv_share_plus);

        ll_tip_col = (LinearLayout) findViewById(R.id.ll_tip_col);

        tv_share_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_tip_result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculateTotal();
            }
        });

        tv_bill_result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculateTipAmount();

                calculateTotal();


            }
        });

        final View.OnClickListener billPadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id){
                    case(R.id.bill_clear):
                        tv_bill_result.clearText();
                        break;
                    case(R.id.bill_del):
                        tv_bill_result.deleteText();
                        break;
                    default:
                        tv_bill_result.enterText(((TextView) v).getText().toString());
                        break;
                }
            }
        };

        View.OnClickListener peoplePadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentShare = tv_share_num.getText().toString();
                String clickedNumber = ((TextView) v).getText().toString();
                if(currentShare.length()>1){
                    return;
                }
                else if(currentShare.length()>0){
                    if(currentShare.equalsIgnoreCase(getString(R.string.num_zero)))
                        numOfShare = Integer.parseInt(clickedNumber);
                    else
                        numOfShare = Integer.parseInt(currentShare + clickedNumber);
                }
                else{
                    numOfShare = 0;
                }
                tv_share_num.setText(numOfShare+"");

            }
        };

        final View.OnClickListener tipPercentListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                String currentBillAmount = tv_bill_result.getText().toString();
                switch (id){
                    case(R.id.tip_0):
                        clearTipSelectState();
                        v.setSelected(true);

                        if(currentBillAmount.length()>0) {
                            tipAmount = 0;
                            tv_tip_result.setText(precision.format(tipAmount));
                        }
                        break;
                    case (R.id.tip_btn):

                        break;
                    default:
                        clearTipSelectState();
                        v.setSelected(true);
                        if(currentBillAmount.length()>0) {
                            tipPercent = Double.parseDouble(chopLastChar(((TextView) v).getText().toString())) / 100d;
                            tipAmount = Double.parseDouble(currentBillAmount) * tipPercent;
                            tv_tip_result.setText(precision.format(tipAmount));
                        }
                        break;
                }
            }
        };

        //backspace for num of ppl to share
        tv_share_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentShare = tv_share_num.getText().toString();
                if(currentShare.length()>1){
                    numOfShare = Integer.parseInt(chopLastChar(currentShare));
                }
                else{
                    numOfShare = 0;
                }
                tv_share_num.setText(numOfShare+"");
            }
        });

        //plus one people to share, maximun 99
        tv_share_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numOfShare++;
                if(numOfShare>99)
                    numOfShare=99;
                tv_share_num.setText(numOfShare+"");
            }
        });

        //minus one people to share, minimun 0
        tv_share_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numOfShare--;
                if(numOfShare<0)
                    numOfShare=0;
                tv_share_num.setText(numOfShare+"");
            }
        });
        for(int i = 0; i < number_pad.getChildCount(); i++){
            TableRow row = (TableRow)number_pad.getChildAt(i);
            for(int j = 0; j < row.getChildCount(); j++){
                row.getChildAt(j).setOnClickListener(billPadListener);
            }
        }

        for(int i = 0; i < share_num_pad.getChildCount(); i++){
            TableRow row = (TableRow)share_num_pad.getChildAt(i);
            for(int j = 0; j < row.getChildCount(); j++){
                row.getChildAt(j).setOnClickListener(peoplePadListener);
            }
        }

        for(int i = 0; i < ll_tip_col.getChildCount(); i++){
            ll_tip_col.getChildAt(i).setOnClickListener(tipPercentListener);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        tip_15.post(new Runnable() {
            @Override
            public void run() {
                tip_15.setSelected(true);
                tipPercent = 0.15;
            }
        });
    }

    private void calculateTotal(){
        //calculate total
        String currentBillAmount = tv_bill_result.getText().toString();
        if(currentBillAmount.length()>0) {
            double billAmount = Double.parseDouble(currentBillAmount);
            totalAmount = billAmount + tipAmount;
            tv_total_result.setText(precision.format(totalAmount));
        }
    }


    private void calculateTipAmount(){

        String currentBillAmount = tv_bill_result.getText().toString();
        if(currentBillAmount.length()>0){
            double billAmount = Double.parseDouble(currentBillAmount);
            tipAmount = billAmount * tipPercent;
            tv_tip_result.setText(precision.format(tipAmount));
        }
    }


    private void clearTipSelectState(){
        tip_0.setSelected(false);
        tip_10.setSelected(false);
        tip_15.setSelected(false);
        tip_20.setSelected(false);
    }

    private String chopLastChar(String str) {
        if (str.length() > 0) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }


}
