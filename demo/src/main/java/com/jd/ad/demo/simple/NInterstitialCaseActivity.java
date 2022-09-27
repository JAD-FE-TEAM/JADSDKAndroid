package com.jd.ad.demo.simple;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.jd.ad.demo.R;
import com.jd.ad.demo.base.BaseActivity;
import com.jd.ad.demo.utils.ActivityUtils;
import com.jd.ad.demo.utils.DemoExecutor;
import com.jd.ad.demo.utils.ImageLoader;
import com.jd.ad.demo.utils.ScreenUtils;
import com.jd.ad.demo.utils.ThreadChooseUtils;
import com.jd.ad.sdk.dl.addata.JADMaterialData;
import com.jd.ad.sdk.dl.model.JADSlot;
import com.jd.ad.sdk.nativead.JADNative;
import com.jd.ad.sdk.nativead.JADNativeInteractionListener;
import com.jd.ad.sdk.nativead.JADNativeLoadListener;
import com.jd.ad.sdk.nativead.JADNativeWidget;

import java.util.ArrayList;
import java.util.List;

public class NInterstitialCaseActivity extends BaseActivity {

    private static final String AD_ID = "2534";
    private static final String AD_TAG = "Interstitial";

    private float mExpressViewWidthDp = 360;
    private float mExpressViewHeightDp = 534;

    /**
     * 广告对象
     */
    private JADNative mJADNative;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_simple_n_inter_ad_activity);

        initViews();
    }

    private void initViews() {
        String codeID = AD_ID;

        if (ThreadChooseUtils.isMainThread(NInterstitialCaseActivity.this)) {
            loadAndShowAd(codeID, mExpressViewWidthDp, mExpressViewHeightDp);
        } else {
            DemoExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    loadAndShowAd(codeID, mExpressViewWidthDp, mExpressViewHeightDp);
                }
            });

        }
    }

    /**
     * 点击按钮加载插屏广告
     */
    @SuppressLint("SetTextI18n")
    public void loadAndShowAd(String codeID, float expressViewWidthDp, float expressViewHeightDp) {

        /*
         * Step1:创建自渲染广告参数，包括广告位id、图片宽高、是否支持 deepLink
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(codeID)
                .setImageSize(expressViewWidthDp, expressViewHeightDp)
                .setAdType(JADSlot.AdType.INTERSTITIAL)
                .build();

        //Step2:加载自渲染相关广告数据，监听加载回调
        mJADNative = new JADNative(slot);
        mJADNative.loadAd(new JADNativeLoadListener() {
            @Override
            public void onLoadSuccess() {
                if (!ActivityUtils.isActivityAvailable(NInterstitialCaseActivity.this)) {
                    return;
                }
                // 获取竞价价格
                if (mJADNative != null) {
                    int price = mJADNative.getJADExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }

                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                //Step3:媒体创建自渲染视图
                inflateAdView(expressViewWidthDp, expressViewHeightDp);
            }

            @Override
            public void onLoadFailure(int code, String error) {
                showToast(getString(R.string.ad_load_failed, AD_TAG, code, error));
                logD(getString(R.string.ad_load_failed, AD_TAG, code, error));
            }
        });
    }

    /**
     * 渲染广告视图
     *
     * @return 广告视图
     */
    private void inflateAdView(float expressViewWidth, float expressViewHeight) {
        ViewGroup adView = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_native_interstitial, null);
        ImageView logoView = adView.findViewById(R.id.jad_logo);
        logoView.setImageBitmap(JADNativeWidget.getLogo(this));

        final ImageView imageView = adView.findViewById(R.id.jad_native_insert_ad_img);
        View closeView = adView.findViewById(R.id.jad_close);

        final Dialog adDialog = new Dialog(this, R.style.jad_native_insert_dialog);
        adDialog.setCancelable(true);

        adDialog.setContentView(adView);
        if (mJADNative != null
                && mJADNative.getDataList() != null
                && !mJADNative.getDataList().isEmpty()
                && mJADNative.getDataList().get(0) != null) {


            JADMaterialData data = mJADNative.getDataList().get(0);
//            titleView.setText(data.getAdTitle());
            if (data.getImageUrls() != null && !data.getImageUrls().isEmpty()) {
                ImageLoader.loadImage(NInterstitialCaseActivity.this, data.getImageUrls().get(0), imageView);
            }

            List<View> list = new ArrayList<>();
            list.add(imageView);
            List<View> closeList = new ArrayList<>();
            closeList.add(closeView);
            /*
             * Step4: 注册需要监听的视图，包括整体的广告View、点击视图列表、关闭视图列表
             * 这里非常重要，不要在View的listener中做点击操作，否则影响计费
             */
            mJADNative.registerNativeView(this, adView, list, closeList, new JADNativeInteractionListener() {

                @Override
                public void onExposure() {
                    showToast(getString(R.string.ad_exposure, AD_TAG));
                    logD(getString(R.string.ad_exposure, AD_TAG));
                }

                @Override
                public void onClick(View view) {
                    showToast(getString(R.string.ad_click, AD_TAG));
                    logD(getString(R.string.ad_click, AD_TAG));
                }

                @Override
                public void onClose(View view) {
                    showToast(getString(R.string.ad_dismiss, AD_TAG));
                    logD(getString(R.string.ad_dismiss, AD_TAG));
                    //Step5:在回调中进行相应点击和关闭的操作
                    if (adDialog.isShowing()) {
                        adDialog.dismiss();
                    }
                }

            });
        }

        adDialog.show();
        adDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = adDialog.getWindow().getAttributes();
        lp.width = ScreenUtils.dip2px(this, expressViewWidth);
        lp.height = ScreenUtils.dip2px(this, expressViewHeight);
        adDialog.getWindow().setAttributes(lp);
        //注意：请不要在注册的view中再添加 OnClickListener
    }


    /**
     * 页面销毁时可对广告进行销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mJADNative != null) {
            this.mJADNative.destroy();
            this.mJADNative = null;
        }
    }
}
