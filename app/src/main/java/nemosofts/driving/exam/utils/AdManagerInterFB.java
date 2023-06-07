package nemosofts.driving.exam.utils;

import android.content.Context;

import com.facebook.ads.InterstitialAd;

import nemosofts.driving.exam.callback.Callback;

public class AdManagerInterFB {

    static InterstitialAd interAd;
    private final Context ctx;

    public AdManagerInterFB(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        interAd = new InterstitialAd(ctx, Callback.ad_inter_id);
        interAd.loadAd();
    }

    public InterstitialAd getAd() {
        return interAd;
    }

    public static void setAd(InterstitialAd interstitialAd) {
        interAd = interstitialAd;
    }
}