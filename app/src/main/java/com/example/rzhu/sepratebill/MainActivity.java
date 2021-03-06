package com.example.rzhu.sepratebill;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";
    private static final DecimalFormat precision = new DecimalFormat("00.00");

    private static final int RESULT_TEXT_DELAY = 1000;
    private static final int VIBRATION_LENGTH = 100;
    private static final String DEFAULT_TIP = "SP_DEFAULT_TIP";
    private final int STATE_TIP = 1;
    private final int STATE_BILL = 2;

    private SharedPreferences prefs;
    private Handler mHandler;
    private Vibrator mVibrator;
    private boolean hasVibrator;

    private int statusBarHeight;
    private int currentState;
    private boolean resultRevealed = false;
    private boolean customTip = false;

    private float billAmount = 0f;
    private float tipAmount = 0f;
    private float totalAmount = 0f;
    private float tipPercent;
    private int numOfShare;
    private float splitAmount = 0f;

    private RelativeLayout displayLayout;
    private View cover;
    private AtmTextView tvBill, tvTip;
    private TextView tvTotal, tvShare, tvSplit;

    private TableLayout numPad;
    private TextView tvSharePadNum;

    private LinearLayout tipCol;
    private Button tip20, tip15, tip10, tip0, toggleBtn;

    private TextView tvHoverNumOfShare;
    private TypefaceTextView tvShareNumClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_portrait);
        }else{
            setContentView(R.layout.activity_main_landscape);
        }

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        hasVibrator = mVibrator.hasVibrator();

        mHandler = new Handler();
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
                hasVibrator = false;
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
                        }else{
                            clearTipSelectState();
                        }

                        hasVibrator = mVibrator.hasVibrator();
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
        if (numOfShare!=0){
            tvShare.setText(numOfShare+"");
            tvSharePadNum.setText(numOfShare+"");
            tvShareNumClear.setVisibility(View.VISIBLE);
        }else{
            tvShare.setText("1");
            tvShareNumClear.setVisibility(View.INVISIBLE);
        }

        if(splitAmount!=0) {
            tvSplit.setVisibility(View.VISIBLE);
            tvSplit.setText(precision.format(splitAmount));
        }else{
            tvSplit.setVisibility(View.INVISIBLE);
        }

        mHandler.removeCallbacks(showTextDelayTask);

        if(resultRevealed){
            cover.setVisibility(View.GONE);
        }else{
            cover.setVisibility(View.VISIBLE);
        }
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
        tvSharePadNum = (TextView) findViewById(R.id.tv_share_num);

        tipCol = (LinearLayout) findViewById(R.id.ll_tip_col);
        tip0 = (Button) tipCol.findViewById(R.id.tip_0);
        tip10 = (Button) tipCol.findViewById(R.id.tip_10);
        tip15 = (Button) tipCol.findViewById(R.id.tip_15);
        tip20 = (Button) tipCol.findViewById(R.id.tip_20);
        toggleBtn = (Button) tipCol.findViewById(R.id.tip_btn);

        tvHoverNumOfShare = (TextView) findViewById(R.id.tv_share_holder);
        tvShareNumClear =(TypefaceTextView) findViewById(R.id.tv_share_clear);
    }

    public void numPadClicked(View v) {
        activateVibrator();
        int n = Integer.parseInt((String) v.getTag());
        switch (currentState){
            case STATE_BILL:
                if (!resultRevealed){
                    revealAnimation();
                }
                if (billAmount<999.99) {
                    billAmount *= 10f;
                    billAmount += 0.01f*n;
                    textUpdateAnimation(tvBill);
                    tvBill.enterText((String)v.getTag());
                    updateTip();
                }
                break;
            case STATE_TIP:
                if (!resultRevealed){
                    revealAnimation();
                }
                if (tipAmount<999.99) {
                    if(!customTip){
                        clearTipSelectState();
                        customTip = true;
                    }
                    tipAmount *= 10f;
                    tipAmount += 0.01f*n;
                    textUpdateAnimation(tvTip);
                    tvTip.enterText((String)v.getTag());
                    updateTotal();
                }
                break;
        }
    }

    public void numPadClearClicked(View v) {
        activateVibrator();
        switch (currentState){
            case STATE_BILL:
                clearWaveAnimation();
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
        activateVibrator();
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
        activateVibrator();
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
        if(billAmount!=0){
            textUpdateAnimation(tvTip);
        }
    }

    public void shareBtnClicked(View v) {
        if (!resultRevealed){
            revealAnimation();
        }
        activateVibrator();
        switch (v.getId()){
            case R.id.tv_share_minus:
                if (numOfShare>1) {
                    numOfShare--;
                }else {
                    return;
                }
                break;
            case R.id.tv_share_plus:
                if (numOfShare<99) {
                    if (numOfShare==0){
                        numOfShare++;
                        showTextAnimation(tvShareNumClear);
                    }
                    numOfShare++;
                }else {
                    return;
                }
                break;
            case R.id.tv_share_clear:
                hideTextAnimation((TextView) v);
                numOfShare = 0;
                break;
        }
        if (numOfShare!=0){
            tvSharePadNum.setText(numOfShare+"");
            startFlyShareNumberAnimation();
        }else{
            tvSharePadNum.setText(" ");
            tvShare.setText("1");
        }

        updateTotal();
    }

    public void setNumOfShare(View v) {
        if (!resultRevealed){
            revealAnimation();
        }
        activateVibrator();
        int n = Integer.parseInt((String)v.getTag());

        numOfShare -= Math.ceil(numOfShare / 10)*10;
        numOfShare = numOfShare*10+n;

        tvSharePadNum.setText(numOfShare+"");
        showTextAnimation(tvShareNumClear);
        startFlyShareNumberAnimation();
        updateTotal();
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
        if (numOfShare!=-0) {
            splitAmount = totalAmount / numOfShare;
        }else{
            splitAmount = totalAmount;
        }
        splitAmount = Math.round(splitAmount *= 100f);
        splitAmount /= 100f;
        if (splitAmount!=0) {
            if (tvSplit.getVisibility()!=View.VISIBLE) {
                tvSplit.setText(precision.format(splitAmount));
                showTextAnimation(tvSplit);
            }else{
                mHandler.removeCallbacks(showTextDelayTask);
                mHandler.postDelayed(showTextDelayTask, RESULT_TEXT_DELAY);
            }
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

    private void textUpdateAnimation(TextView tv){
        tv.setScaleX(0.9f);
        tv.setScaleY(0.9f);
        tv.animate().scaleX(1).scaleY(1).alpha(1).setDuration(500)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void showTextAnimation(final TextView tv) {
        if (tv.getVisibility()!=View.VISIBLE) {
            Log.d(TAG, "show text");
            tv.setVisibility(View.VISIBLE);
            tv.setScaleX(0);
            tv.setScaleY(0);
            tv.setAlpha(0);
            tv.animate().scaleX(1).scaleY(1).alpha(1).setDuration(800)
                    .setInterpolator(new BounceInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            tv.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();
        }
    }

    private void hideTextAnimation(final TextView tv) {
        if (tv.getVisibility()==View.VISIBLE) {
            Log.d(TAG, "hide text");
            tv.animate().scaleX(0).scaleY(0).alpha(0).setDuration(600)
                    .setInterpolator(new AnticipateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {}

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            tv.setVisibility(View.INVISIBLE);
                            tv.setScaleX(1);
                            tv.setScaleY(1);
                            tv.setAlpha(1);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {}

                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    }).start();
        }
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

    private void revealAnimation() {
        if (!resultRevealed) {
            resultRevealed = true;
            cover.setVisibility(View.GONE);
            displayLayout.setVisibility(View.INVISIBLE);
            int cx = displayLayout.getWidth();
            int cy = displayLayout.getHeight();
            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(displayLayout, cx / 2, cy / 2, 0, displayLayout.getWidth());
            anim.setDuration(getResources().getInteger(R.integer.clear_reveal_time));
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            // make the view visible and start the animation
            displayLayout.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private void clearWaveAnimation() {
        //cover is visible means it is animating
        if (resultRevealed) {
            resultRevealed = false;
            int cx = 0;
            int cy = displayLayout.getHeight();
            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(cover, cx, cy, 0, displayLayout.getWidth() * 2);
            anim.setDuration(getResources().getInteger(R.integer.clear_reveal_time));
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cover.setVisibility(View.VISIBLE);
                    tvBill.clearText();
                    tvTip.clearText();
                    tvTotal.setText("00.00");
                    tvSplit.setText("");
                    billAmount = 0f;
                    tipAmount = 0f;
                    totalAmount = 0f;
                    splitAmount = 0f;
                }
            });
            // make the view visible and start the animation
            cover.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private void startFlyShareNumberAnimation() {
        if (resultRevealed) {
            tvShare.setVisibility(View.INVISIBLE);
            tvShare.setText(numOfShare + "");

            int locStart[] = new int[2];
            int locEnd[] = new int[2];

            tvSharePadNum.getLocationOnScreen(locStart);
            tvShare.getLocationOnScreen(locEnd);

            float x1 = locStart[0];
            float y1 = locStart[1] - statusBarHeight;

            float x3 = locEnd[0];
            float y3 = locEnd[1] - statusBarHeight;

            tvHoverNumOfShare.setVisibility(View.VISIBLE);
            tvHoverNumOfShare.setText(numOfShare + "");
            tvHoverNumOfShare.setTextSize(TypedValue.COMPLEX_UNIT_SP, pixelsToSp(tvSharePadNum.getTextSize()));

            tvHoverNumOfShare.setX(x3);
            tvHoverNumOfShare.setY(y3);

            final Path path = new Path();
            path.moveTo(x1, y1);

            final float x2 = (x1 + x3) / 2;
            final float y2 = y1;

            path.quadTo(x2, y2, x3, y3);

            ObjectAnimator moveAnimation = ObjectAnimator.ofFloat(tvHoverNumOfShare, View.X, View.Y, path);
            moveAnimation.setAutoCancel(true);
            moveAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    tvHoverNumOfShare.setVisibility(View.GONE);
                    tvShare.setVisibility(View.VISIBLE);
                }
            });

            moveAnimation.start();
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public float pixelsToSp(float px) {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    private void activateVibrator() {
        if(hasVibrator)
            mVibrator.vibrate(VIBRATION_LENGTH);
    }

    private Runnable showTextDelayTask = new Runnable() {
        @Override
        public void run() {
            if (tvSplit!=null) {
                String oldStr = (String) tvSplit.getText();
                String newStr = precision.format(splitAmount);
                if (!oldStr.equals(newStr)){
                    tvSplit.setVisibility(View.INVISIBLE);
                    tvSplit.setText(newStr);
                    showTextAnimation(tvSplit);
                }
            }
        }
    };
}
