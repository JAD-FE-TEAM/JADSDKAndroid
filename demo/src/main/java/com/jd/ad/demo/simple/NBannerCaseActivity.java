package com.jd.ad.demo.simple;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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

public class NBannerCaseActivity extends BaseActivity {

    private static final String AD_ID = "2532";
    private static final String AD_TAG = "Banner";

    private float mExpressViewWidthDp = 360;
    private float mExpressViewHeightDp = 175;

    /**
     * 广告容器
     */
    private ViewGroup mAdContainer;

    /**
     * 广告对象
     */
    private JADNative mJADNative;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_simple_n_banner_activity);
        initView();
    }

    private void initView() {
        mAdContainer = findViewById(R.id.ad_container);
        if (ThreadChooseUtils.isMainThread(NBannerCaseActivity.this)) {
            loadAndShowBanner(mExpressViewWidthDp, mExpressViewHeightDp);
        } else {
            DemoExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    loadAndShowBanner(mExpressViewWidthDp, mExpressViewHeightDp);
                }
            });
        }
    }

    /**
     * 请求广告
     */
    public void loadAndShowBanner(float expressWidthDp, float expressHeightDp) {
        String codeID = AD_ID;
        /* Step1:创建自渲染广告参数，包括广告位id、图片宽高、是否支持 deepLink
         * 注意:
         * 1、宽高比 必须满足 媒体方在后台配置的广告尺寸比例，否则影响广告填充
         * 2、宽高大小单位为dp
         */
        JADSlot slot = new JADSlot.Builder()
                .setSlotID(codeID)
                .setImageSize(expressWidthDp, expressHeightDp)
                .setAdType(JADSlot.AdType.BANNER)
                .build();
        //Step2:加载自渲染相关广告数据，监听加载回调
        mJADNative = new JADNative(slot);
        mJADNative.loadAd(new JADNativeLoadListener() {
            @Override
            public void onLoadSuccess() {
                if (!ActivityUtils.isActivityAvailable(NBannerCaseActivity.this)) {
                    return;
                }

                showToast(getString(R.string.ad_load_success, AD_TAG));
                logD(getString(R.string.ad_load_success, AD_TAG));
                // 获取竞价价格
                if (mJADNative != null) {
                    int price = mJADNative.getJADExtra().getPrice();
                    logD(getString(R.string.ad_data_price, AD_TAG, price));
                }

                //Step3:媒体创建自渲染视图
                View adView = inflateAdView();
                ViewGroup.LayoutParams lp = adView.getLayoutParams();
                if (lp == null) {
                    lp = new ViewGroup.LayoutParams(ScreenUtils.dip2px(getApplicationContext(), expressWidthDp),
                            ScreenUtils.dip2px(getApplicationContext(), expressHeightDp));
                } else {
                    lp.width = ScreenUtils.dip2px(getApplicationContext(), expressWidthDp);
                    lp.height = ScreenUtils.dip2px(getApplicationContext(), expressHeightDp);
                }
                adView.setLayoutParams(lp);
                mAdContainer.removeAllViews();
                mAdContainer.addView(adView);

            }

            @Override
            public void onLoadFailure(int code, @NonNull String error) {
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
    private View inflateAdView() {
        final ViewGroup adView = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_native_banner, null);
        final ImageView imageView = adView.findViewById(R.id.jad_image);
        View closeView = adView.findViewById(R.id.jad_close);
        ImageView logoView = adView.findViewById(R.id.jad_logo);
        logoView.setImageBitmap(JADNativeWidget.getLogo(this));
        if (mJADNative != null
                && mJADNative.getDataList() != null
                && !mJADNative.getDataList().isEmpty()
                && mJADNative.getDataList().get(0) != null) {

            JADMaterialData data = mJADNative.getDataList().get(0);
            if (data.getImageUrls() != null && !data.getImageUrls().isEmpty()) {
                ImageLoader.loadImage(NBannerCaseActivity.this, data.getImageUrls().get(0), imageView);
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
                public void onClick(@NonNull View view) {
                    showToast(getString(R.string.ad_click, AD_TAG));
                    logD(getString(R.string.ad_click, AD_TAG));
                }

                @Override
                public void onClose(View view) {
                    showToast(getString(R.string.ad_dismiss, AD_TAG));
                    logD(getString(R.string.ad_dismiss, AD_TAG));
                    //Step5:在回调中进行响应点击和关闭的操作
                    if (view != null && view.getId() == R.id.jad_close) {
                        ViewParent parent = adView.getParent();
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).removeView(adView);
                        }
                    }
                }
            });
        }
        return adView;
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
