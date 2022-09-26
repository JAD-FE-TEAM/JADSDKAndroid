package com.jd.ad.demo.expressad.feed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.view.HeadBarLayout;

public class EFeedAdRecyclerActivity extends BaseActivity {

    private static final String AD_ID = "8126";

    private SeekBar mSeekWidthBar;  //高度，宽度的 seekBar
    private SeekBar mSeekHeightBar;

    private TextView mSeekWidthBarTv; //用于显示当前 SeekBar 进度情况
    private TextView mSeekHeightBarTv;

    private TextView mSelectWHRatioTv;

    private EditText mPlacementEt;

    private float mScale = 1.5f;
    private float whRation = 1.5f;
    private boolean isChecked = false;
    private TextView mScaleTv1, mScaleTv2, mScaleTv3, mScaleTv4;
    private View mScaleView1, mScaleView2;
    private int mChooseOri = RecyclerView.VERTICAL;
    private int mChooseManager;
    private static final int CHOOSE_LINEAR_LAYOUT_MANAGER = 0;
    private static final int CHOOSE_GRID_LAYOUT_MANAGER = 1;
    private static final int CHOOSE_STAGGERED_GRID_LAYOUT_MANAGER = 2;

    private float expressViewWidthDp;
    private float expressViewHeightDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_e_feed_recycle_activity);

        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initViews();
        initRadioGroup();
    }

    private void initRadioGroup() {
        RadioGroup radioGroupManager = findViewById(R.id.manager);
        RadioGroup radioGroupOri = findViewById(R.id.orientation);

        radioGroupManager.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.grid:
                        mChooseManager = CHOOSE_GRID_LAYOUT_MANAGER;
                        break;
                    case R.id.staggered:
                        mChooseManager = CHOOSE_STAGGERED_GRID_LAYOUT_MANAGER;
                        break;
                    default:
                        mChooseManager = CHOOSE_LINEAR_LAYOUT_MANAGER;
                        break;
                }
            }
        });

        radioGroupOri.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.horizontal) {
                    mChooseOri = RecyclerView.HORIZONTAL;
                } else {
                    mChooseOri = RecyclerView.VERTICAL;
                }
            }
        });
    }

    private void initViews() {
        HeadBarLayout headBarLayout = findViewById(R.id.head_bar);
        headBarLayout.setTitle(R.string.e_feed_recyclerview);
        headBarLayout.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPlacementEt = findViewById(R.id.et_code);
        mPlacementEt.setText(AD_ID);

        mSeekWidthBar = findViewById(R.id.seek_width_bar);
        mSeekWidthBarTv = findViewById(R.id.seek_width_bar_progress);

        mSelectWHRatioTv = findViewById(R.id.width_div_height);

        mSeekHeightBar = findViewById(R.id.seek_height_bar);
        mSeekHeightBarTv = findViewById(R.id.seek_height_bar_progress);

        mScaleView1 = findViewById(R.id.scale1);
        mScaleView2 = findViewById(R.id.scale2);

        mSeekWidthBar.setProgress(100);
        mSeekWidthBar.setMax(100);

        initScaleView(0); //默认选择 1280 * 720 的广告图片

        resetDes(mSeekWidthBar.getProgress(), mSeekWidthBar, mSeekWidthBarTv, true);
        resetDes(mSeekHeightBar.getProgress(), mSeekHeightBar, mSeekHeightBarTv, false);

        mSeekWidthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resetDes(progress, seekBar, mSeekWidthBarTv, true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekHeightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resetDes(progress, seekBar, mSeekHeightBarTv, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mScaleView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScaleView(0);
            }
        });
        mScaleView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initScaleView(1);
            }
        });

        CheckBox mCheckbox = findViewById(R.id.checkbox);
        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EFeedAdRecyclerActivity.this.isChecked = isChecked;
                if (!isChecked) {
                    mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleTv1.setClickable(false);
                    mScaleTv2.setClickable(false);
                    mScaleTv3.setClickable(false);
                    mScaleTv4.setClickable(false);
                    mSeekHeightBar.setEnabled(true);
                    mSeekWidthBar.setEnabled(true);
                    mScaleView1.setEnabled(true);
                    mScaleView2.setEnabled(true);
                    initScaleView(0);
                } else {
                    mScaleTv1.setClickable(true);
                    mScaleTv2.setClickable(true);
                    mScaleTv3.setClickable(true);
                    mScaleTv4.setClickable(true);
                    mSeekHeightBar.setEnabled(false);
                    mSeekWidthBar.setEnabled(false);
                    mScaleView1.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView2.setBackgroundResource(R.drawable.btn_border_normal);
                    mScaleView1.setEnabled(false);
                    mScaleView2.setEnabled(false);
                    mScale = whRation;
                    createScale();
                }
            }
        });

        mScaleTv1 = findViewById(R.id.tv_scale1);
        mScaleTv2 = findViewById(R.id.tv_scale2);
        mScaleTv3 = findViewById(R.id.tv_scale3);
        mScaleTv4 = findViewById(R.id.tv_scale4);

        mScaleTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 1.2f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
                createScale();
            }
        });
        mScaleTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 1.8f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
                createScale();
            }
        });
        mScaleTv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_clicked);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
                mScale = 2.8f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
                createScale();
            }
        });
        mScaleTv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
                mScaleTv4.setBackgroundResource(R.drawable.btn_border_clicked);
                mScale = 3.2f;
                @SuppressLint("DefaultLocale")
                String whRatio = String.format("选择的宽高比 = %.2f", mScale);
                mSelectWHRatioTv.setText(whRatio);
                mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
                createScale();
            }
        });

        mScaleTv1.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv2.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv3.setBackgroundResource(R.drawable.btn_border_normal);
        mScaleTv4.setBackgroundResource(R.drawable.btn_border_normal);
        if (isChecked) {
            mScaleTv1.setClickable(true);
            mScaleTv2.setClickable(true);
            mScaleTv3.setClickable(true);
            mScaleTv4.setClickable(true);
        } else {
            mScaleTv1.setClickable(false);
            mScaleTv2.setClickable(false);
            mScaleTv3.setClickable(false);
            mScaleTv4.setClickable(false);
        }
        Button adLoadBtn = findViewById(R.id.load_ad_btn);
        adLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeID = !TextUtils.isEmpty(mPlacementEt.getText()) ? mPlacementEt.getText().toString() : "";
                Intent intent = new Intent(EFeedAdRecyclerActivity.this, EFeedAdRecyclerShowActivity.class);
                intent.putExtra("expressViewWidthDp", expressViewWidthDp);
                intent.putExtra("expressViewHeightDp", expressViewHeightDp);
                intent.putExtra("codeID", codeID);
                intent.putExtra("mChooseManager", mChooseManager);
                intent.putExtra("mChooseOri", mChooseOri);
                startActivity(intent);
            }
        });
    }

    public float[] getProgressWH(float scale) {
        float screenWidthDp = ScreenUtils.getScreenWidthDip(this);
        int proH = (int) (screenWidthDp / scale);
        float proW = proH * scale;
        float[] proWH = new float[]{proW, proH};
        return proWH;
    }

    private void resetDes(int progress, SeekBar seekBar, TextView desTv, boolean isWidth) {
        String format = "宽度/总宽度 = %ddp / %ddp = %.2f";
        int totalSize = ScreenUtils.getScreenWidthDip(this);
        if (!isWidth) {
            format = "高度/总高度 = %ddp / %ddp = %.2f";
            totalSize = ScreenUtils.getScreenHeightDip(this);
        }

        float ratio = progress * 1.0f / seekBar.getMax();
        int selectSize = (int) (ratio * totalSize);

        @SuppressLint("DefaultLocale")
        String value = String.format(format, selectSize, totalSize, ratio);
        desTv.setText(value);

        int wProgress = mSeekWidthBar.getProgress();
        int wMax = mSeekWidthBar.getMax();
        float widthDp = wProgress * 1.0f / wMax * ScreenUtils.getScreenWidthDip(this);

        int hProgress = mSeekHeightBar.getProgress();
        if (hProgress == 0) {
            new DemoDialog(EFeedAdRecyclerActivity.this, "Error", "0不可以做分母", new DemoDialog.dialogCallback() {
                @Override
                public void dismissCallback() {
                    initScaleView(0);
                }
            });
        }
        int hMax = mSeekHeightBar.getMax();
        float heightDp = hProgress * 1.0f / hMax * ScreenUtils.getScreenHeightDip(this);

        whRation = widthDp / heightDp;
        @SuppressLint("DefaultLocale")
        String whRatio = String.format("选择的宽高比 = %.2f", whRation);
        mSelectWHRatioTv.setText(whRatio);

        // 信息流广告的宽高比(开发者更具自己情况具体设置)，符合宽高比区间： [1.2 - 1.8] 或 [2.8 - 3.2]
        boolean isPic1 = whRation >= 1.2 && whRation <= 1.8;   //对应图片尺寸 1280 * 720
        boolean isPic2 = whRation >= 2.8 && whRation <= 3.2;  //对应图片尺寸 480 * 320

        if (isPic1) {
            mScaleView1.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            mScaleView1.setBackgroundResource(R.drawable.btn_border_normal);
        }

        if (isPic2) {
            mScaleView2.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            mScaleView2.setBackgroundResource(R.drawable.btn_border_normal);
        }

        if (isPic1 || isPic2) {
            mSelectWHRatioTv.setTextColor(getResources().getColor(R.color.color_normal));
        } else {
            mSelectWHRatioTv.setTextColor(Color.RED);
        }

        createScale();
    }

    private void createScale() {
        if (isChecked) {
            float[] proWH = getProgressWH(mScale);
            expressViewWidthDp = proWH[0];
            expressViewHeightDp = proWH[1];
        } else {
            expressViewWidthDp = (int) (mSeekWidthBar.getProgress() * 1.0f / mSeekWidthBar.getMax()
                    * ScreenUtils.getScreenWidthDip(this));
            expressViewHeightDp = (int) (mSeekHeightBar.getProgress() * 1.0f / mSeekHeightBar.getMax()
                    * ScreenUtils.getScreenHeightDip(this));
        }
    }

    // 信息流强制定位宽高区间，利用宽高比计算会产生区间错乱
    private void initScaleView(int choosePic) {
        mSeekWidthBar.setProgress(100);
        int screenWidthDp = ScreenUtils.getScreenWidthDip(this);
        if (choosePic == 1) {
            int validHeightDp = (int) (screenWidthDp * 1.0f / 3.0);
            int initHeightProgress1 = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress1);
            mScaleView2.setBackgroundResource(R.drawable.btn_border_clicked);
        } else {
            int validHeightDp = (int) (screenWidthDp * 1.0f / 1.5);
            int initHeightProgress = (int) (validHeightDp * 1.0f / ScreenUtils.getScreenHeightDip(this) * 100);
            mSeekHeightBar.setProgress(initHeightProgress);
            mScaleView1.setBackgroundResource(R.drawable.btn_border_clicked);
            @SuppressLint("DefaultLocale")
            String whRatio = String.format("选择的宽高比 = %.2f", whRation);
            mSelectWHRatioTv.setText(whRatio);
        }
    }
}