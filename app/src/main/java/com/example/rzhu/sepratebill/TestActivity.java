package com.example.rzhu.sepratebill;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;


public class TestActivity extends Activity {
    private final static String TAG = "MainActivity";
    private static final DecimalFormat precision = new DecimalFormat("00.00");

    private static final String DEFAULT_TIP = "SP_DEFAULT_TIP";
    private final int STATE_TIP = 1;
    private final int STATE_BILL = 2;

    private SharedPreferences prefs;
    private int statusBarHeight;
    private int currentState;
    private boolean resultRevealed;
    private boolean customTip = false;

    private float billAmount = 0f;
    private float tipAmount = 0f;
    private float totalAmount = 0f;
    private float tipPercent;
    private int numOfShare = 1;
    private float splitAmount = 0f;

    private RelativeLayout displayLayout;
    private View cover;
    private AtmTextView tvBill, tvTip;
    private TextView tvTotal, tvShare, tvSplit;

    private TableLayout numPad, sharePad;

    private LinearLayout tipCol;
    private Button tip20, tip15, tip10, tip0, toggleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_portrait);
        }else{
            setContentView(R.layout.activity_main_landscape);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentState = STATE_BILL;
        tipPercent = prefs.getFloat(DEFAULT_TIP, 0.15f);
        findViews();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_portrait);
        }else{
            setContentView(R.layout.activity_main_landscape);
        }
        findViews();
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume " + currentState);
        int backgroundColor = currentState==STATE_BILL ?
                getResources().getColor(R.color.bill_pad) : getResources().getColor(R.color.tip_pad);

        statusBarHeight = getStatusBarHeight();
        if (currentState == STATE_BILL) {
            if (!customTip) {
                tipPercent = prefs.getFloat(DEFAULT_TIP, 0.15f);
                tipCol.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tipPercent == 0.2f) {
                            tip20.performClick();
                        } else if (tipPercent == 0.15f) {
                            tip15.performClick();
                        } else if (tipPercent == 0.10f) {
                            tip10.performClick();
                        } else if (tipPercent == 0f) {
                            tip0.performClick();
                        }
                    }
                });
            }

            toggleBtn.setBackground(getResources().getDrawable(R.drawable.bill_to_tip_btn, getTheme()));
            toggleBtn.setText(getString(R.string.edit_tip));
        }else{
            toggleBtn.setBackground(getResources().getDrawable(R.drawable.tip_to_bill_btn, getTheme()));
            toggleBtn.setText(getString(R.string.edit_bill));
        }

        numPad.setBackgroundColor(backgroundColor);
        for (int i = 0; i < numPad.getChildCount(); i++) {
            View parentRow = numPad.getChildAt(i);
            if(parentRow instanceof TableRow) {
                for (int j = 0; j < ((TableRow)parentRow).getChildCount(); j++) {
                    Drawable rippleDrawable = currentState==STATE_BILL ?
                            getResources().getDrawable(R.drawable.ripple_bill, getTheme()) :
                            getResources().getDrawable(R.drawable.ripple_tip, getTheme());
                    ((TableRow)parentRow).getChildAt(j).setBackground(rippleDrawable);
                }
            }
        }

        if(billAmount!=0f) tvBill.setText(precision.format(billAmount));
        if(tipAmount!=0f) tvTip.setText(precision.format(tipAmount));
        if(totalAmount!=0f) tvTotal.setText(precision.format(totalAmount));
        tvShare.setText(numOfShare+"");
        if(splitAmount!=0) tvSplit.setText(precision.format(splitAmount));
    }

    private void findViews() {
        displayLayout = (RelativeLayout) findViewById(R.id.display_layout);
        cover = displayLayout.findViewById(R.id.cover);
        tvBill = (AtmTextView)displayLayout.findViewById(R.id.tv_bill_result);
        tvTip = (AtmTextView)displayLayout.findViewById(R.id.tv_tip_result);
        tvTotal = (TextView)displayLayout.findViewById(R.id.tv_total_result);
        tvShare = (TextView)displayLayout.findViewById(R.id.tv_share_result);
        tvSplit = (TextView)displayLayout.findViewById(R.id.tv_split_amount);

        numPad = (TableLayout) findViewById(R.id.num_pad);

        sharePad = (TableLayout) findViewById(R.id.share_pad);

        tipCol = (LinearLayout) findViewById(R.id.ll_tip_col);
        tip0 = (Button) tipCol.findViewById(R.id.tip_0);
        tip10 = (Button) tipCol.findViewById(R.id.tip_10);
        tip15 = (Button) tipCol.findViewById(R.id.tip_15);
        tip20 = (Button) tipCol.findViewById(R.id.tip_20);
        toggleBtn = (Button) tipCol.findViewById(R.id.tip_btn);
    }

    public void numPadClicked(View v) {
        Log.d(TAG, "Clicked number: " + v.getTag());
        int n = Integer.parseInt((String) v.getTag());
        switch (currentState){
            case STATE_BILL:
                if (billAmount<999.99) {
                    billAmount *= 10f;
                    billAmount += 0.01f*n;
                    tvBill.enterText((String)v.getTag());
                    updateTip();
                }
                break;
            case STATE_TIP:
                if (tipAmount<999.99) {
                    if(!customTip){
                        clearTipSelectState();
                        customTip = true;
                    }
                    tipAmount *= 10f;
                    tipAmount += 0.01f*n;
                    tvTip.enterText((String)v.getTag());
                    updateTotal();
                }
                break;
        }
    }

    public void numPadClearClicked(View v) {
        Log.d(TAG, "Clicked clear");
        switch (currentState){
            case STATE_BILL:
                billAmount = 0f;
                tvBill.setText("00.00");
                updateTip();
                break;
            case STATE_TIP:
                if(!customTip){
                    clearTipSelectState();
                    customTip = true;
                }
                tipAmount = 0f;
                tvTip.setText("00.00");
                updateTotal();
                break;
        }
    }

    public void numPadBackClicked(View v) {
        Log.d(TAG, "Clicked back");
        switch (currentState){
            case STATE_BILL:
                if (billAmount>=0.01) {
                    billAmount /= 10f;
                    if (billAmount<0.01)
                        billAmount = 0f;
                    tvBill.deleteText();
                    updateTip();
                }
                break;
            case STATE_TIP:
                if (tipAmount>=0.01) {
                    if(!customTip){
                        clearTipSelectState();
                        customTip = true;
                    }
                    tipAmount /= 10f;
                    if (tipAmount<0.01)
                        tipAmount = 0f;
                    tvTip.deleteText();
                    updateTotal();
                }
                break;
        }
    }


    public void setTipPercent(View v) {
        int viewId = v.getId();
        switch(viewId) {
            case R.id.tip_20:
                tipPercent = 0.2f;
                break;
            case R.id.tip_15:
                tipPercent = 0.15f;
                break;
            case R.id.tip_10:
                tipPercent = 0.1f;
                break;
            case R.id.tip_0:
                tipPercent = 0.0f;
                break;
            default:
                toggleBillTip();
                return;
        }
        if (currentState == STATE_TIP) {
            toggleBillTip();
        }
        customTip = false;

        clearTipSelectState();
        v.setSelected(true);

        prefs.edit().putFloat(DEFAULT_TIP, tipPercent).apply();
        updateTip();

    }

    private void updateTip() {
        if(!customTip) {
            tipAmount = billAmount * tipPercent;
            tipAmount = Math.round(tipAmount *= 100f);
            tipAmount /= 100f;
            tvTip.setText(precision.format(tipAmount));
        }
        updateTotal();
    }

    private void updateTotal() {
        totalAmount = billAmount + tipAmount;
        totalAmount = Math.round(totalAmount *= 100f);
        totalAmount /= 100f;
        tvTotal.setText(precision.format(totalAmount));
        updateSplit();
    }

    private void updateSplit() {
        splitAmount = totalAmount/numOfShare;
        splitAmount = Math.round(splitAmount *= 100f);
        splitAmount /= 100f;
        if (splitAmount!=0) {
            tvSplit.setText(precision.format(splitAmount));
        }else{
            tvSplit.setText("");
        }
    }


    private void clearTipSelectState() {
        tip0.setSelected(false);
        tip10.setSelected(false);
        tip15.setSelected(false);
        tip20.setSelected(false);
    }

    private void toggleBillTip() {
        Log.i(TAG, "flipping "+currentState);
        currentState = currentState==STATE_BILL ? STATE_TIP : STATE_BILL;
        final int backgroundColor = currentState==STATE_BILL ?
                getResources().getColor(R.color.bill_pad) : getResources().getColor(R.color.tip_pad);
        final String toggleStr = currentState==STATE_BILL ?
                getString(R.string.edit_tip) : getString(R.string.edit_bill);
        final Drawable toggleDrawable = currentState==STATE_BILL ?
                getResources().getDrawable(R.drawable.bill_to_tip_btn, getTheme()) :
                getResources().getDrawable(R.drawable.tip_to_bill_btn, getTheme());


        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_out);
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_in);

        flipOut.setTarget(numPad);
        flipIn.setTarget(numPad);
        flipIn.setStartDelay(200l);
        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                toggleBtn.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                numPad.setBackgroundColor(backgroundColor);
                for (int i = 0; i < numPad.getChildCount(); i++) {
                    View parentRow = numPad.getChildAt(i);
                    if(parentRow instanceof TableRow) {
                        for (int j = 0; j < ((TableRow)parentRow).getChildCount(); j++) {
                            Drawable rippleDrawable = currentState==STATE_BILL ?
                                    getResources().getDrawable(R.drawable.ripple_bill, getTheme()) :
                                    getResources().getDrawable(R.drawable.ripple_tip, getTheme());
                            ((TableRow)parentRow).getChildAt(j).setBackground(rippleDrawable);
                        }
                    }
                }
            }
        });

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                toggleBtn.setText(toggleStr);
                toggleBtn.setBackground(toggleDrawable);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                toggleBtn.setEnabled(true);
            }
        });

        flipOut.start();
        flipIn.start();
    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
