package com.jd.ad.demo.simple;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.interstitial.JADInterstitial;
import com.jd.ad.sdk.interstitial.JADInterstitialListener;

public class EInterstitialCaseActivity extends BaseActivity {
    private static final String AD_ID = "2534";
    private static final String AD_TAG = "Interstitial";

    private JADInterstitial mJADInterstitial;

    private float mExpressViewWidthDp = 360;
    private float mExpressViewHeightDp = 534;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_simple_e_inter_activity);
        initViews();
    }

    private void initViews() {
        String codeID = AD_ID;
        if (ThreadChooseUtils.isMainThread(EInterstitialCaseActivity.this)) {
            loadAdAndShow(codeID, mExpressViewWidthDp, mExpressViewHeightDp);
        } else {
            DemoExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    loadAdAndShow(codeID, mExpressViewWidthDp, mExpressViewHeightDp);
                }
            });
        }
    }

    /**
     * 点击按钮加载插屏广告
     */
    @SuppressLint("SetTextI18n")
    public void loadAdAndShow(String codeID, float expressWidthDp, float expressHeightDp) {

        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink
        /**
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(codeID) //广告位ID 必须正确 否则无广告返回
                .setSize(expressWidthDp, expressHeightDp) //单位必须为dp 必须正确 否则无广告返回
                .build();
        //Step2: 创建 JadInterstitial 对象，参数包括广告位参数和回调接口
        mJADInterstitial = new JADInterstitial(this, slot);
        //Step3: 加载 JadInterstitial
        mJADInterstitial.loadAd(new JADInterstitialListener() {

            @Override
            public void onLoadSuccess() {
                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                // 获取竞价价格
                if (mJADInterstitial != null) {
                    int price = mJADInterstitial.getExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }
            }

            @Override
            public void onLoadFailure(int code, @NonNull String error) {
                logE("JadInterstitial AD onAdLoadFailed [" + code + ", " + error + "]");
                new DemoDialog(EInterstitialCaseActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {

                    }
                });
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
            }

            @Override
            public void onRenderSuccess(@NonNull View view) {
                showToast(getString(R.string.ad_render_success, AD_TAG));
                logD(getString(R.string.ad_render_success, AD_TAG));

                //Step4: 在render成功之后调用show方法来展示广告
                if (!isFinishing()) {
                    mJADInterstitial.showAd(EInterstitialCaseActivity.this);
                }
            }

            @Override
            public void onRenderFailure(int code, @NonNull String error) {
                showToast(getString(R.string.ad_render_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_render_failed, AD_TAG, code, error));
            }

            @Override
            public void onExposure() {
                showToast(getString(R.string.ad_exposure, AD_TAG));
                logD(getString(R.string.ad_exposure, AD_TAG));
            }

            @Override
            public void onClick() {
                showToast(getString(R.string.ad_click, AD_TAG));
                logD(getString(R.string.ad_click, AD_TAG));
            }

            @Override
            public void onClose() {
                showToast(getString(R.string.ad_dismiss, AD_TAG));
                logD(getString(R.string.ad_dismiss, AD_TAG));
            }
        });


    }

}
