package com.jd.ad.demo.simple;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.DemoDialog;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.banner.JADBanner;
import com.jd.ad.sdk.banner.JADBannerListener;
import com.jd.ad.sdk.dl.model.JADSlot;

public class EBannerCaseActivity extends BaseActivity {

    private static final String AD_ID = "2532"; //Demo 模板横幅默认广告位
    private static final String AD_TAG = "Banner";

    private float mExpressViewWidthDp = 360;
    private float mExpressViewHeightDp = 175;

    /**
     * 开发者提供的横幅广告容器，用于广告渲染成功后，把广告视图添加到此容器
     */
    private ViewGroup mAdContainer;

    /**
     * 广告对象
     */
    private JADBanner mJADBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_simple_e_banner_activity);
        initView();
    }


    private void initView() {
        mAdContainer = findViewById(R.id.ad_container);

        //判断是否在主线程
        if (ThreadChooseUtils.isMainThread(EBannerCaseActivity.this)) {
            loadAdAndShow(mExpressViewWidthDp, mExpressViewHeightDp);
        } else {
            DemoExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    loadAdAndShow(mExpressViewWidthDp, mExpressViewHeightDp);
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadAdAndShow(float expressWidthDp, float expressHeightDp) {
        //Step1: 创建广告位参数，参数包括广告位Id，宽高，是否支持DeepLink
        String codeID = AD_ID;

        /*
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(codeID) //广告位ID 必须正确 否则无广告返回
                .setSize(expressWidthDp, expressHeightDp)  //单位必须为dp 必须正确 否则无广告返回
                .setCloseButtonHidden(false) //是否关闭 关闭 按钮
                .build();
        //Step2: 创建 JADBanner，参数包括广告位参数和回调接口
        mJADBanner = new JADBanner(this, slot);
        //Step3: 加载广告
        mJADBanner.loadAd(new JADBannerListener() {
            @Override
            public void onLoadSuccess() {
                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                // 获取竞价价格
                if (mJADBanner != null && mJADBanner.getExtra() != null) {
                    int price = mJADBanner.getExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }
            }

            @Override
            public void onLoadFailure(int code, @NonNull String error) {
                new DemoDialog(EBannerCaseActivity.this, "Error LoadFailed", "错误码：" + code + "\n" + "错误信息：" + error, new DemoDialog.dialogCallback() {
                    @Override
                    public void dismissCallback() {

                    }
                });
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
            }

            @Override
            public void onRenderSuccess(@NonNull View adView) {
                //Step4: 在render成功之后调用, 将返回广告视图adView添加到自己广告容器 adContainer 视图中
                showToast(getString(R.string.ad_render_success, AD_TAG));
                logD(getString(R.string.ad_render_success, AD_TAG));
                if (!isFinishing()) {
                    mAdContainer.removeAllViews();
                    mAdContainer.addView(adView);
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
