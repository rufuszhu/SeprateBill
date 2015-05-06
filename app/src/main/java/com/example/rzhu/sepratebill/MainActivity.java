package com.example.rzhu.sepratebill;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final DecimalFormat precision = new DecimalFormat("00.00");
    private static final String DEFAULT_TIP = "DEFAULT_TIP";
    private final int STATE_TIP = 1;
    private final int STATE_BILL = 2;
    private Button tip_20, tip_15, tip_10, tip_notip, toggleBtn;
    private TextView bill_clear, bill_del, bill_0, bill_1, bill_2, bill_3, bill_4, bill_5, bill_6, bill_7, bill_8, bill_9;
    private TextView tip_clear, tip_del, tip_0, tip_1, tip_2, tip_3, tip_4, tip_5, tip_6, tip_7, tip_8, tip_9;
    private TextView ppl_clear, ppl_del, ppl_0, ppl_1, ppl_2, ppl_3, ppl_4, ppl_5, ppl_6, ppl_7, ppl_8, ppl_9;
    private TextView tv_total_result, tv_share_num, tv_share_back, tv_share_minus, tv_share_plus, tv_split_amount, tv_share_result, tv_share_holder;
    private AtmTextView tv_bill_result, tv_tip_result;
    private TableLayout number_pad, share_num_pad, tip_number_pad;
    private LinearLayout ll_tip_col;
    private FrameLayout result_wrapper;
    private View cover;
    private SharedPreferences prefs;

    private double tipAmount;
    private double totalAmount;
    private double tipPercent;
    private int numOfShare;
    private double splitAmount;
    private int currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numOfShare = 1;
        currentState = STATE_BILL;

        bill_clear = (TextView) findViewById(R.id.tip_clear);
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

        tip_notip = (Button) findViewById(R.id.tip_notip);
        tip_10 = (Button) findViewById(R.id.tip_10);
        tip_15 = (Button) findViewById(R.id.tip_15);
        tip_20 = (Button) findViewById(R.id.tip_20);
        toggleBtn = (Button) findViewById(R.id.tip_btn);

        number_pad = (TableLayout) findViewById(R.id.number_pad);
        share_num_pad = (TableLayout) findViewById(R.id.share_num_pad);
        tip_number_pad = (TableLayout) findViewById(R.id.tip_number_pad);

        tv_bill_result = (AtmTextView) findViewById(R.id.tv_bill_result);
        tv_tip_result = (AtmTextView) findViewById(R.id.tv_tip_result);
        tv_total_result = (TextView) findViewById(R.id.tv_total_result);
        tv_share_num = (TextView) findViewById(R.id.tv_share_num);
        tv_share_back = (TextView) findViewById(R.id.tv_share_back);
        tv_share_minus = (TextView) findViewById(R.id.tv_share_minus);
        tv_share_plus = (TextView) findViewById(R.id.tv_share_plus);
        tv_split_amount = (TextView) findViewById(R.id.tv_split_amount);
        tv_share_result = (TextView) findViewById(R.id.tv_share_result);
        tv_share_holder = (TextView) findViewById(R.id.tv_share_holder);

        ll_tip_col = (LinearLayout) findViewById(R.id.ll_tip_col);

        result_wrapper = (FrameLayout) findViewById(R.id.result_wrapper);
        cover = findViewById(R.id.cover);

        tv_share_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    numOfShare = Integer.parseInt(tv_share_num.getText().toString());
                } catch (Exception e) {
                    numOfShare = 1;
                }

                tv_share_result.setText(numOfShare+"");

                int loc1[] = new int[2];
                int loc2[] = new int[2];

                tv_share_num.getLocationOnScreen(loc1);
                tv_share_result.getLocationOnScreen(loc2);

                Log.e(TAG, "loc1[0]: " + loc1[0]);
                Log.e(TAG, "loc1[1]: " + loc1[1]);
                Log.e(TAG, "loc2[0]: " + loc2[0]);
                Log.e(TAG, "loc2[1]: " + loc2[1]);


                float x1 = loc1[0];
                float y1 = loc1[1];


                float x3 = loc2[0];
                float y3 = loc2[1];

                tv_share_holder.setVisibility(View.VISIBLE);
                tv_share_holder.setText(numOfShare+"");
                tv_share_holder.setX(x1);
                tv_share_holder.setY(y1);

//                final Path path = new Path();
//                path.moveTo(x1, y1);
//
//                final float x2 = (x1 + x3) / 2;
//                final float y2 = y1;
//
//                path.quadTo(x2, y2, x3, y3);
//
//                ObjectAnimator anim = ObjectAnimator.ofFloat(tv_share_holder, View.X, View.Y, path);
//                anim.start();

                calculateSplitAmount();
            }
        });

        tv_tip_result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    tipAmount = Double.valueOf(tv_tip_result.getText().toString());
                } catch (Exception e) {
                    tipAmount = 0;
                }

                calculateTotal();
                calculateSplitAmount();
            }
        });

        tv_bill_result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (currentState != STATE_TIP)
                    calculateTipAmount();
                calculateTotal();
                calculateSplitAmount();
            }
        });

        final View.OnClickListener billPadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case (R.id.bill_clear):
                        clearWaveAnimation();
                        break;
                    case (R.id.bill_del):
                        tv_bill_result.deleteText();
                        break;
                    default:
                        tv_bill_result.enterText(((TextView) v).getText().toString());
                        break;
                }
            }
        };

        final View.OnClickListener tipPadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case (R.id.tip_clear):
                        clearWaveAnimation();
                        break;
                    case (R.id.tip_del):
                        tv_tip_result.deleteText();
                        break;
                    default:
                        tv_tip_result.enterText(((TextView) v).getText().toString());
                        break;
                }
            }
        };

        bill_del.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearWaveAnimation();
                return true;
            }
        });

        View.OnClickListener peoplePadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentShare = tv_share_num.getText().toString();
                String clickedNumber = ((TextView) v).getText().toString();
                if (currentShare.length() == 2) {
                    return;
                } else {
                    numOfShare = Integer.parseInt(currentShare + clickedNumber);
                }

                tv_share_num.setText(numOfShare + "");

            }
        };

        final View.OnClickListener tipPercentListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                String currentBillAmount = tv_bill_result.getText().toString();
                switch (id) {
                    case (R.id.tip_btn):
                        toggleBillTipBtn();
                        break;
                    default:
                        if (currentState == STATE_TIP) {
                            toggleBillTipBtn();
                        }

                        clearTipSelectState();
                        v.setSelected(true);

                        if(id == R.id.tip_notip){
                            tipAmount = 0;
                            tipPercent = 0;
                            tv_tip_result.clearText();
                        }else if (currentBillAmount.length() > 0) {
                            tipPercent = Double.parseDouble(chopLastChar(((TextView) v).getText().toString())) / 100d;
                            tipAmount = Double.parseDouble(currentBillAmount) * tipPercent;
                            tv_tip_result.setText(precision.format(tipAmount));
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(DEFAULT_TIP, (int) (tipPercent * 100));
                        editor.commit();
                        break;
                }
            }
        };

        //backspace for num of ppl to share
        tv_share_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentShare = tv_share_num.getText().toString();
                if (currentShare.length() == 2) {
                    numOfShare = Integer.parseInt(chopLastChar(currentShare));
                    tv_share_num.setText(numOfShare + "");
                } else {
                    numOfShare = 0;
                    tv_share_num.setText("");
                }
            }
        });

        //plus one people to share, maximun 99
        tv_share_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numOfShare++;
                if (numOfShare > 99)
                    numOfShare = 99;
                tv_share_num.setText(numOfShare + "");
            }
        });

        //minus one people to share, minimun 0
        tv_share_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numOfShare--;
                if (numOfShare < 1)
                    numOfShare = 1;
                tv_share_num.setText(numOfShare + "");
            }
        });
        for (int i = 0; i < number_pad.getChildCount(); i++) {
            TableRow row = (TableRow) number_pad.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                row.getChildAt(j).setOnClickListener(billPadListener);
            }
        }

        for (int i = 0; i < tip_number_pad.getChildCount(); i++) {
            TableRow row = (TableRow) tip_number_pad.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                row.getChildAt(j).setOnClickListener(tipPadListener);
            }
        }

        for (int i = 0; i < share_num_pad.getChildCount(); i++) {
            TableRow row = (TableRow) share_num_pad.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                row.getChildAt(j).setOnClickListener(peoplePadListener);
            }
        }


        for (int i = 0; i < ll_tip_col.getChildCount(); i++) {
            ll_tip_col.getChildAt(i).setOnClickListener(tipPercentListener);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(currentState == STATE_BILL) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            int savedTip = prefs.getInt(DEFAULT_TIP, 15);
            Log.e(TAG, "savedTip: " + savedTip);
            tipPercent = (double) prefs.getInt(DEFAULT_TIP, 15) / 100d;
            clearTipSelectState();
            ll_tip_col.post(new Runnable() {
                @Override
                public void run() {
                    if (tipPercent == 0.2) {
                        tip_20.performClick();
                    } else if (tipPercent == 0.15) {
                        tip_15.performClick();
                    } else if (tipPercent == 0.10) {
                        tip_10.performClick();
                    } else if (tipPercent == 0) {
                        tip_notip.performClick();
                    }
                }
            });
        }

    }

    private void calculateTotal() {
        //calculate total
        String currentBillAmount = tv_bill_result.getText().toString();
        if (currentBillAmount.length() > 0) {
            double billAmount = Double.parseDouble(currentBillAmount);
            totalAmount = billAmount + tipAmount;
            tv_total_result.setText(precision.format(totalAmount));
        }
    }

    private void calculateTipAmount() {
        String currentBillAmount = tv_bill_result.getText().toString();
        if (currentBillAmount.length() > 0) {
            double billAmount = Double.parseDouble(currentBillAmount);
            tipAmount = billAmount * tipPercent;
            tv_tip_result.setText(precision.format(tipAmount));
        }
    }

    private void calculateSplitAmount() {

        if (totalAmount != 0 && numOfShare != 0) {
            splitAmount = totalAmount / (double) numOfShare;
            tv_split_amount.setText(precision.format(splitAmount));
        }

    }

    private void clearTipSelectState() {
        tip_notip.setSelected(false);
        tip_10.setSelected(false);
        tip_15.setSelected(false);
        tip_20.setSelected(false);
    }

    private String chopLastChar(String str) {
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private void flipNumberPad() {
        final View newView;
        final View oldView;
        if (number_pad.getVisibility() == View.GONE) {
            newView = number_pad;
            oldView = tip_number_pad;
        } else {
            newView = tip_number_pad;
            oldView = number_pad;
        }

        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_out);
        flipOut.setTarget(oldView);
        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                toggleBtn.setOnClickListener(null);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                oldView.setVisibility(View.GONE);
                toggleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleBillTipBtn();
                    }
                });
            }
        });
        flipOut.start();

        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_in);
        newView.setVisibility(View.VISIBLE);
        flipIn.setTarget(newView);

        flipIn.start();
    }

    private void toggleBillTipBtn() {
        flipNumberPad();

        if (toggleBtn.getText().toString().equalsIgnoreCase(getString(R.string.tip))) {
            toggleBtn.setText(getString(R.string.bill));
            toggleBtn.setBackground(getDrawable(R.drawable.bill_btn_selector));
            clearTipSelectState();
            currentState = STATE_TIP;

        } else {
            toggleBtn.setText(getString(R.string.tip));
            toggleBtn.setBackground(getDrawable(R.drawable.tip_btn_selector));
            currentState = STATE_BILL;
        }

    }

    private void clearWaveAnimation() {
        int cx = 0;
        int cy = result_wrapper.getHeight();
        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(cover, cx, cy, 0, result_wrapper.getWidth() * 2);
        anim.setDuration(getResources().getInteger(R.integer.clear_reveal_time));
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cover.setVisibility(View.GONE);
                tv_bill_result.clearText();
                tv_tip_result.clearText();
                tv_total_result.setText("");
                tv_split_amount.setText("");
                totalAmount = 0;
                splitAmount = 0;
                tipAmount = 0;
            }
        });
        // make the view visible and start the animation
        cover.setVisibility(View.VISIBLE);
        anim.start();
    }

}
