package nemosofts.driving.exam.utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.ads.mediation.facebook.FacebookExtras;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.activity.CustomAdsActivity;
import nemosofts.driving.exam.activity.LoginActivity;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.interfaces.InterAdListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Helper {

    private final Context ctx;
    private final AdConsent adConsent;
    private InterAdListener interAdListener;

    public Helper(Context ctx) {
        this.ctx = ctx;
        adConsent = new AdConsent(ctx);
    }

    public Helper(Context ctx, InterAdListener interAdListener) {
        this.ctx = ctx;
        this.interAdListener = interAdListener;
        adConsent = new AdConsent(ctx);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void initializeAds() {
        if(Callback.adNetwork.equals(Callback.AD_TYPE_ADMOB)) {
            MobileAds.initialize(ctx, initializationStatus -> {
            });
        }
        if(Callback.adNetwork.equals(Callback.AD_TYPE_FACEBOOK)) {
            AudienceNetworkAds.initialize(ctx);
        }
        if(Callback.adNetwork.equals(Callback.AD_TYPE_STARTAPP)) {
            if(!Callback.startapp_id.equals("")) {
                StartAppSDK.init(ctx, Callback.startapp_id, false);
                StartAppAd.disableSplash();
            }
        }
        if(Callback.adNetwork.equals(Callback.AD_TYPE_APPLOVIN)) {
            if(!AppLovinSdk.getInstance(ctx).isInitialized()) {
                AppLovinSdk.initializeSdk(ctx);
                AppLovinSdk.getInstance(ctx).setMediationProvider("max");
                AppLovinSdk.getInstance(ctx).getSettings().setTestDeviceAdvertisingIds(Arrays.asList("bb6822d9-18de-41b0-994e-41d4245a4d63", "749d75a2-1ef2-4ff9-88a5-c50374843ac6"));
            }
        }
        if(Callback.adNetwork.equals(Callback.AD_TYPE_UNITY)) {
            if(!Callback.unity_ads_id.equals("")) {
                UnityAds.initialize(ctx, Callback.unity_ads_id, false);
            }
        }
        if(Callback.adNetwork.equals(Callback.AD_TYPE_IRONSOURCE)) {
            IronSource.init((Activity) ctx, Callback.iron_ads_id, () -> {
            });
        }
    }

    private void showPersonalizedAds(@NonNull LinearLayout linearLayout) {
        AdView adView = new AdView(ctx);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, new Bundle())
                .addNetworkExtrasBundle(FacebookAdapter.class, new FacebookExtras().build())
                .build();
        adView.setAdUnitId(Callback.ad_banner_id);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                if (adConsent.isAdsShow()){
                    adConsent.setAdsCount();
                }else {
                    linearLayout.removeView(adView);
                }
                super.onAdClicked();
            }
        });
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    private void showNonPersonalizedAds(@NonNull LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        AdView adView = new AdView(ctx);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .addNetworkExtrasBundle(FacebookAdapter.class, new FacebookExtras().build())
                .build();
        adView.setAdUnitId(Callback.ad_banner_id);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                if (adConsent.isAdsShow()){
                    adConsent.setAdsCount();
                }else {
                    linearLayout.removeView(adView);
                }
                super.onAdClicked();
            }
        });
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if (isNetworkAvailable() && Callback.isBannerAd && adConsent.isAdsShow()) {
            switch (Callback.adNetwork) {
                case Callback.AD_TYPE_ADMOB:
                    if (ConsentInformation.getInstance(ctx).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                        showNonPersonalizedAds(linearLayout);
                    } else {
                        showPersonalizedAds(linearLayout);
                    }
                    break;
                case Callback.AD_TYPE_FACEBOOK:
                    com.facebook.ads.AdView adView;
                    adView = new com.facebook.ads.AdView(ctx, Callback.ad_banner_id, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                    com.facebook.ads.AdListener adListener  = new com.facebook.ads.AdListener() {
                        @Override
                        public void onError(com.facebook.ads.Ad ad, AdError adError) {
                            // document why this method is empty
                        }

                        @Override
                        public void onAdLoaded(com.facebook.ads.Ad ad) {
                            // document why this method is empty
                        }

                        @Override
                        public void onAdClicked(com.facebook.ads.Ad ad) {
                            if (adConsent.isAdsShow()){
                                adConsent.setAdsCount();
                            }else {
                                linearLayout.removeView(adView);
                            }
                        }

                        @Override
                        public void onLoggingImpression(com.facebook.ads.Ad ad) {
                            // document why this method is empty
                        }
                    };
                    adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
                    linearLayout.addView(adView);
                    break;
                case Callback.AD_TYPE_STARTAPP:
                    Banner startAppBanner = new Banner(ctx);
                    startAppBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    startAppBanner.setBannerListener(new com.startapp.sdk.ads.banner.BannerListener() {
                        @Override
                        public void onReceiveAd(View view) {
                            // document why this method is empty
                        }
                        @Override
                        public void onFailedToReceiveAd(View view) {
                            linearLayout.removeView(startAppBanner);
                        }
                        @Override
                        public void onImpression(View view) {
                            // document why this method is empty
                        }
                        @Override
                        public void onClick(View view) {
                            if (adConsent.isAdsShow()){
                                adConsent.setAdsCount();
                            }else {
                                linearLayout.removeView(startAppBanner);
                            }
                        }
                    });
                    linearLayout.addView(startAppBanner);
                    startAppBanner.loadAd();
                    break;
                case Callback.AD_TYPE_APPLOVIN:
                    MaxAdView adView_app = new MaxAdView(Callback.ad_banner_id, ctx);
                    int heightPx = ctx.getResources().getDimensionPixelSize(R.dimen.banner_height);
                    adView_app.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx));
                    linearLayout.addView(adView_app);
                    adView_app.loadAd();
                    break;
                case Callback.AD_TYPE_UNITY:
                    BannerView bannerView = new BannerView((Activity) ctx, "banner", new UnityBannerSize(320, 50));
                    bannerView.setListener(new BannerView.Listener() {
                        @Override
                        public void onBannerLoaded(BannerView bannerAdView) {
                            super.onBannerLoaded(bannerAdView);
                        }

                        @Override
                        public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                            super.onBannerFailedToLoad(bannerAdView, errorInfo);
                        }

                        @Override
                        public void onBannerClick(BannerView bannerAdView) {
                            if (adConsent.isAdsShow()){
                                adConsent.setAdsCount();
                            }else {
                                linearLayout.removeView(bannerView);
                            }
                            super.onBannerClick(bannerAdView);
                        }

                        @Override
                        public void onBannerLeftApplication(BannerView bannerAdView) {
                            super.onBannerLeftApplication(bannerAdView);
                        }
                    });
                    bannerView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                    linearLayout.addView(bannerView);
                    bannerView.load();
                    break;
                case Callback.AD_TYPE_IRONSOURCE:
                    IronSourceBannerLayout banner  = IronSource.createBanner((Activity) ctx, ISBannerSize.BANNER);
                    banner.setBannerListener(new com.ironsource.mediationsdk.sdk.BannerListener() {
                        @Override
                        public void onBannerAdLoaded() {
                            // document why this method is empty
                        }
                        @Override
                        public void onBannerAdLoadFailed(IronSourceError error) {
                            linearLayout.setVisibility(View.GONE);
                        }
                        @Override
                        public void onBannerAdClicked() {
                            if (adConsent.isAdsShow()){
                                adConsent.setAdsCount();
                            } else {
                                linearLayout.removeView(banner);
                            }
                        }
                        @Override
                        public void onBannerAdScreenPresented() {
                            // document why this method is empty
                        }
                        @Override
                        public void onBannerAdScreenDismissed() {
                            // document why this method is empty
                        }
                        @Override
                        public void onBannerAdLeftApplication() {
                            // document why this method is empty
                        }
                    });
                    linearLayout.addView(banner);
                    IronSource.loadBanner(banner);
                    break;
            }
        }
    }

    public void showInter(final int pos, final String type) {
        Callback.customAdCount = Callback.customAdCount + 1;
        if (Callback.isCustomAds && Callback.customAdCount % Callback.customAdShow == 0){
            ctx.startActivity(new Intent(ctx, CustomAdsActivity.class));
        } else if (Callback.isInterAd && adConsent.isAdsShow()) {
            Callback.adCount = Callback.adCount + 1;
            if (Callback.adCount % Callback.adInterstitialShow == 0) {
                switch (Callback.adNetwork) {
                    case Callback.AD_TYPE_ADMOB:
                        final AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(ctx);
                        if (adManagerInterAdmob.getAd() != null) {
                            adManagerInterAdmob.getAd().setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdDismissedFullScreenContent();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                                    AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdFailedToShowFullScreenContent(adError);
                                }

                                @Override
                                public void onAdClicked() {
                                    adConsent.setAdsCount();
                                    super.onAdClicked();
                                }
                            });
                            adManagerInterAdmob.getAd().show((Activity) ctx);
                        } else {
                            AdManagerInterAdmob.setAd(null);
                            adManagerInterAdmob.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Callback.AD_TYPE_FACEBOOK:
                        final AdManagerInterFB adManagerInterFB = new AdManagerInterFB(ctx);
                        if(adManagerInterFB.getAd() != null && adManagerInterFB.getAd().isAdLoaded()) {
                            adManagerInterFB.getAd().loadAd(adManagerInterFB.getAd().buildLoadAdConfig()
                                    .withAdListener(new InterstitialAdListener() {
                                        @Override
                                        public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {
                                            // document why this method is empty
                                        }

                                        @Override
                                        public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                                            AdManagerInterFB.setAd(null);
                                            adManagerInterFB.createAd();
                                            interAdListener.onClick(pos, type);
                                        }

                                        @Override
                                        public void onError(com.facebook.ads.Ad ad, AdError adError) {
                                            interAdListener.onClick(pos, type);
                                        }

                                        @Override
                                        public void onAdLoaded(com.facebook.ads.Ad ad) {
                                            // document why this method is empty
                                        }

                                        @Override
                                        public void onAdClicked(com.facebook.ads.Ad ad) {
                                            adConsent.setAdsCount();
                                        }

                                        @Override
                                        public void onLoggingImpression(com.facebook.ads.Ad ad) {
                                            // document why this method is empty
                                        }
                                    })
                                    .build());
                            adManagerInterFB.getAd().show();
                        } else {
                            AdManagerInterFB.setAd(null);
                            adManagerInterFB.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Callback.AD_TYPE_STARTAPP:
                        final AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(ctx);
                        if (adManagerInterStartApp.getAd() != null && adManagerInterStartApp.getAd().isReady()) {
                            adManagerInterStartApp.getAd().showAd(new AdDisplayListener() {
                                @Override
                                public void adHidden(Ad ad) {
                                    AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void adDisplayed(Ad ad) {
                                    // document why this method is empty
                                }

                                @Override
                                public void adClicked(Ad ad) {
                                    adConsent.setAdsCount();
                                }

                                @Override
                                public void adNotDisplayed(Ad ad) {
                                    AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                        } else {
                            AdManagerInterStartApp.setAd(null);
                            adManagerInterStartApp.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Callback.AD_TYPE_APPLOVIN:
                        final AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(ctx);
                        if (adManagerInterApplovin.getAd() != null && adManagerInterApplovin.getAd().isReady()) {
                            adManagerInterApplovin.getAd().setListener(new MaxAdListener() {
                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    // document why this method is empty
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                    // document why this method is empty
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                    adConsent.setAdsCount();
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                            adManagerInterApplovin.getAd().showAd();
                        } else {
                            AdManagerInterStartApp.setAd(null);
                            adManagerInterApplovin.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Callback.AD_TYPE_UNITY:
                        UnityAds.load("interstitial", new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                UnityAds.show((Activity)ctx, "interstitial", new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                                    @Override
                                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                                        interAdListener.onClick(pos, type);
                                    }

                                    @Override
                                    public void onUnityAdsShowStart(String placementId) {
                                        // document why this method is empty
                                    }

                                    @Override
                                    public void onUnityAdsShowClick(String placementId) {
                                        adConsent.setAdsCount();
                                    }

                                    @Override
                                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                                        // document why this method is empty
                                    }
                                });
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                                interAdListener.onClick(pos, type);
                            }
                        });
                        break;
                    case Callback.AD_TYPE_IRONSOURCE:
                        if (IronSource.isInterstitialReady()) {
                            IronSource.setInterstitialListener(new InterstitialListener() {
                                @Override
                                public void onInterstitialAdReady() {
                                    // document why this method is empty
                                }

                                @Override
                                public void onInterstitialAdLoadFailed(IronSourceError error) {
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialAdOpened() {
                                    // document why this method is empty
                                }

                                @Override
                                public void onInterstitialAdClosed() {
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialAdShowFailed(IronSourceError error) {
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialAdClicked() {
                                    adConsent.setAdsCount();
                                }

                                @Override
                                public void onInterstitialAdShowSucceeded() {
                                    // document why this method is empty
                                }
                            });
                            IronSource.showInterstitial();
                        }else {
                            interAdListener.onClick(pos, type);
                        }
                        IronSource.init((Activity) ctx, Callback.iron_ads_id, IronSource.AD_UNIT.INTERSTITIAL);
                        IronSource.loadInterstitial();
                        break;
                    default:
                        interAdListener.onClick(pos, type);
                        break;
                }
            } else {
                interAdListener.onClick(pos, type);
            }
        } else {
            interAdListener.onClick(pos, type);
        }
    }

    public Boolean isAdsShowNative(){
        return adConsent.isAdsShow();
    }

    public RequestBody callAPI(String helper_name, int page, String itemID, String catID, String searchText, String reportMessage, String userID, String name, String email, String mobile, String gender, String password, String authID, String loginType, File file) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(gson);
        jsObj.addProperty("helper_name", helper_name);
        jsObj.addProperty("application_id", ctx.getPackageName());

        if (Callback.METHOD_APP_DETAILS.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_LOGIN.equals(helper_name)){
            jsObj.addProperty("user_email", email);
            jsObj.addProperty("user_password", password);
            jsObj.addProperty("auth_id", authID);
            jsObj.addProperty("type", loginType);
        } else if (Callback.METHOD_REGISTER.equals(helper_name)){
            jsObj.addProperty("user_name", name);
            jsObj.addProperty("user_email", email);
            jsObj.addProperty("user_phone", mobile);
            jsObj.addProperty("user_gender", gender);
            jsObj.addProperty("user_password", password);
            jsObj.addProperty("auth_id", authID);
            jsObj.addProperty("type", loginType);
        } else if (Callback.METHOD_PROFILE.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_ACCOUNT_DELETE.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_EDIT_PROFILE.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("user_name", name);
            jsObj.addProperty("user_email", email);
            jsObj.addProperty("user_phone", mobile);
            jsObj.addProperty("user_password", password);
        } else if (Callback.METHOD_USER_IMAGES_UPDATE.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("type", loginType);
        } else if (Callback.METHOD_FORGOT_PASSWORD.equals(helper_name)){
            jsObj.addProperty("user_email", email);
        } else if (Callback.METHOD_NOTIFICATION.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_REMOVE_NOTIFICATION.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
        } else if (Callback.METHOD_REPORT.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("report_title", searchText);
            jsObj.addProperty("report_msg", reportMessage);
        }

        else if (Callback.METHOD_CAT.equals(helper_name)){
            jsObj.addProperty("page", String.valueOf(page));
        } else if (Callback.METHOD_CAT_ID.equals(helper_name)){
            jsObj.addProperty("lan_id", itemID);
            jsObj.addProperty("cat_id", catID);
            jsObj.addProperty("page", String.valueOf(page));
        } else if (Callback.METHOD_QUIZ.equals(helper_name)){
            jsObj.addProperty("lan_id", itemID);
        }

        switch (helper_name) {
            case Callback.METHOD_REGISTER:
            case Callback.METHOD_USER_IMAGES_UPDATE: {
                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (file != null) {
                    builder.addFormDataPart("image_data", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
                }
                return builder.addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString())).build();
            }
            default:
                return new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString())).build();
        }
    }

    public void clickLogin() {
        SharedPref sharePref = new SharedPref(ctx);
        if (sharePref.isLogged()) {
            logout((Activity) ctx, sharePref);
            Toast.makeText(ctx, ctx.getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(ctx, LoginActivity.class);
            intent.putExtra("from", "app");
            ctx.startActivity(intent);
        }
    }

    public void logout(Activity activity, @NonNull SharedPref sharePref) {
        if (sharePref.getLoginType().equals(Callback.LOGIN_TYPE_GOOGLE)) {
            FirebaseAuth.getInstance().signOut();
        }
        sharePref.setIsAutoLogin(false);
        sharePref.setIsLogged(false);
        sharePref.setLoginDetails("", "", "", "", "", "", "", false, "", Callback.LOGIN_TYPE_NORMAL);
        Intent intent1 = new Intent(ctx, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", "");
        ctx.startActivity(intent1);
        activity.finish();
    }

    @SuppressLint("ObsoleteSdkInt")
    public String getPathImage(Uri uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;
            } else {
                if (uri == null) {
                    return null;
                }
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String data = cursor.getString(column_index);
                    cursor.close();
                    return data;
                }
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (uri == null) {
                return null;
            }
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String data = cursor.getString(column_index);
                cursor.close();
                return data;
            }
            return uri.getPath();
        }
    }

    public void getVerifyDialog(String title, String message) {
        Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_verify);
        dialog.setCancelable(false);

        TextView tv_title = dialog.findViewById(R.id.tv_dialog_title);
        TextView tv_message = dialog.findViewById(R.id.tv_dialog_message);
        tv_title.setText(title);
        tv_message.setText(message);

        dialog.findViewById(R.id.iv_dialog_close).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.tv_dialog_done).setOnClickListener(view -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void getInvalidUserDialog(String message) {
        Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_verify);
        dialog.setCancelable(false);

        TextView tv_title = dialog.findViewById(R.id.tv_dialog_title);
        TextView tv_message = dialog.findViewById(R.id.tv_dialog_message);
        tv_title.setText(ctx.getString(R.string.invalid_user));
        tv_message.setText(message);

        dialog.findViewById(R.id.iv_dialog_close).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.tv_dialog_done).setOnClickListener(view -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public int getColumnWidth(int column, int grid_padding) {
        Resources r = ctx.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, grid_padding, r.getDisplayMetrics());
        return (int) ((getScreenWidth() - ((column + 1) * padding)) / column);
    }
}
