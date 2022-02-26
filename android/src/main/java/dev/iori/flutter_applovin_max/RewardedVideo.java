package dev.iori.flutter_applovin_max;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;


import io.flutter.Log;

public class RewardedVideo implements MaxRewardedAdListener, MaxAdRevenueListener {
    private MaxRewardedAd rewardedAd;
    private int retryAttempt;

    public void Init(String unitId) {
        rewardedAd = MaxRewardedAd.getInstance(unitId, FlutterApplovinMaxPlugin.getInstance().activity);
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
    }

    public void Show() {
        try {
            if (rewardedAd != null && rewardedAd.isReady() && FlutterApplovinMaxPlugin.getInstance().activity != null)
                rewardedAd.showAd();
        } catch (Exception e) {
            Log.e("AppLovin", e.toString());
        }
    }

    public boolean IsLoaded() {
        return rewardedAd.isReady();
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        retryAttempt = 0;
        FlutterApplovinMaxPlugin.getInstance().Callback("AdLoaded");

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        FlutterApplovinMaxPlugin.getInstance().Callback("AdDisplayed");

    }

    @Override
    public void onAdHidden(MaxAd ad) {
        FlutterApplovinMaxPlugin.getInstance().Callback("AdHidden");
        rewardedAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {
        FlutterApplovinMaxPlugin.getInstance().Callback("AdClicked");
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        FlutterApplovinMaxPlugin.getInstance().Callback("AdLoadFailed");
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        FlutterApplovinMaxPlugin.getInstance().Callback("AdDisplayFailed");
    }

    @Override
    public void onRewardedVideoStarted(MaxAd ad) {
        FlutterApplovinMaxPlugin.getInstance().Callback("RewardedVideoStarted");

    }

    @Override
    public void onRewardedVideoCompleted(MaxAd ad) {
        FlutterApplovinMaxPlugin.getInstance().Callback("RewardedVideoCompleted");

    }

    @Override
    public void onAdRevenuePaid(MaxAd ad) {
        FlutterApplovinMaxPlugin.getInstance().Callback("AdRevenuePaid");
    }

    @Override
    public void onUserRewarded(MaxAd ad, MaxReward reward) {
        Log.d("onUserRewarded","reward" + reward);
        FlutterApplovinMaxPlugin.getInstance().Callback("UserRewarded");

    }
}
