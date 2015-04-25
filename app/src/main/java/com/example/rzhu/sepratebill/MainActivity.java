package com.example.rzhu.sepratebill;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView tip_20,tip_15,tip_10,tip_0,tip_btn;
    private TextView bill_clear, bill_del, bill_0, bill_1, bill_2, bill_3, bill_4, bill_5, bill_6, bill_7, bill_8, bill_9;
    private TextView tv_tip_result,tv_total_result;
    private AtmTextView tv_bill_result;


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

        tv_bill_result = (AtmTextView) findViewById(R.id.tv_bill_result);
        tv_tip_result = (TextView) findViewById(R.id.tv_tip_result);
        tv_total_result = (TextView) findViewById(R.id.tv_total_result);

        View.OnClickListener billPadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                int value = Integer.parseInt(((TextView) v).getText().toString());
                switch (id){
                    case(R.id.bill_clear):

                        break;
                    case(R.id.bill_del):
                        break;
                    default:
                        tv_bill_result.enterText(((TextView) v).getText().toString());
                        break;
                }
            }
        };
    }




}
