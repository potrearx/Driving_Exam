package nemosofts.driving.exam.adapter;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdsManager;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.ads.mediation.facebook.FacebookExtras;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.item.ItemSigns;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.Helper;

public class AdapterSigns extends RecyclerView.Adapter {

    private final Helper helper;
    private final ArrayList<ItemSigns> arrayList;
    private final Context context;
    private final RecyclerItemClickListener recyclerViewClickListener;
    private final int VIEW_PROG = -1;
    private int columnWidth = 0;
    private Boolean isAdLoaded = false;
    List<NativeAd> mNativeAdsAdmob = new ArrayList<>();
    List<NativeAdDetails> nativeAdsStartApp = new ArrayList<>();
    private final ArrayList<com.facebook.ads.NativeAd> mNativeAdsFB = new ArrayList<>();
    private NativeAdsManager mNativeAdsManager;

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView iv_signs;

        private MyViewHolder(View view) {
            super(view);
            iv_signs = view.findViewById(R.id.iv_signs);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("StaticFieldLeak")
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout rl_native_ad;

        private ADViewHolder(View view) {
            super(view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }

    public AdapterSigns(Context context, ArrayList<ItemSigns> arrayList, RecyclerItemClickListener recyclerViewClickListener) {
        this.arrayList = arrayList;
        this.context = context;
        helper = new Helper(context);
        this.recyclerViewClickListener = recyclerViewClickListener;
        columnWidth = helper.getColumnWidth(3, 0);
        loadNativeAds();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_progressbar, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType >= 1000) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ads, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View  itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_signs, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            final ItemSigns item = arrayList.get(position);

            ((MyViewHolder) holder).iv_signs.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth));
            String imageurl = item.getImageThumb();
            if(imageurl.equals("")) {
                imageurl = "null";
            }

            Picasso.get()
                    .load(imageurl)
                    .placeholder(R.drawable.material_design_default)
                    .into(((MyViewHolder) holder).iv_signs);

            ((MyViewHolder) holder).iv_signs.setOnClickListener(v -> recyclerViewClickListener.onClickListener(item, holder.getAdapterPosition()));

        } else if (holder instanceof ADViewHolder) {
            if (isAdLoaded) {
                if (((ADViewHolder) holder).rl_native_ad.getChildCount() == 0) {
                    switch (Callback.adNetwork) {
                        case Callback.AD_TYPE_ADMOB:
                            if (!mNativeAdsAdmob.isEmpty()) {

                                int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);

                                NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                                populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                                ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                ((ADViewHolder) holder).rl_native_ad.addView(adView);

                                ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                            }
                            break;
                        case Callback.AD_TYPE_FACEBOOK:
                            NativeAdLayout fb_native_container = (NativeAdLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_fb, null);
                            com.facebook.ads.NativeAd ad;
                            if (mNativeAdsFB.size() >= 5) {
                                ad = mNativeAdsFB.get(new Random().nextInt(5));
                            } else {
                                ad = mNativeAdsManager.nextNativeAd();
                                mNativeAdsFB.add(ad);
                            }

                            LinearLayout adChoicesContainer = fb_native_container.findViewById(R.id.ad_choices_container);
                            AdOptionsView adOptionsView = new AdOptionsView(context, ad, fb_native_container);
                            adChoicesContainer.removeAllViews();
                            adChoicesContainer.addView(adOptionsView, 0);

                            // Create native UI using the ad metadata.
                            com.facebook.ads.MediaView nativeAdIcon = fb_native_container.findViewById(R.id.native_ad_icon);
                            TextView nativeAdTitle = fb_native_container.findViewById(R.id.native_ad_title);
                            com.facebook.ads.MediaView nativeAdMedia = fb_native_container.findViewById(R.id.native_ad_media);
                            TextView nativeAdSocialContext = fb_native_container.findViewById(R.id.native_ad_social_context);
                            TextView nativeAdBody = fb_native_container.findViewById(R.id.native_ad_body);
                            TextView sponsoredLabel = fb_native_container.findViewById(R.id.native_ad_sponsored_label);
                            Button nativeAdCallToAction = fb_native_container.findViewById(R.id.native_ad_call_to_action);

                            // Set the Text.
                            nativeAdTitle.setText(ad.getAdvertiserName());
                            nativeAdBody.setText(ad.getAdBodyText());
                            nativeAdSocialContext.setText(ad.getAdSocialContext());
                            nativeAdCallToAction.setVisibility(ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                            nativeAdCallToAction.setText(ad.getAdCallToAction());
                            sponsoredLabel.setText(ad.getSponsoredTranslation());

                            // Create a list of clickable views
                            List<View> clickableViews = new ArrayList<>();
                            clickableViews.add(nativeAdTitle);
                            clickableViews.add(nativeAdCallToAction);

                            // Register the Title and CTA button to listen for clicks.
                            ad.registerViewForInteraction(fb_native_container, nativeAdMedia, nativeAdIcon, clickableViews);

                            ((ADViewHolder) holder).rl_native_ad.addView(fb_native_container);

                            ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                            break;
                        case Callback.AD_TYPE_STARTAPP:
                            int i = new Random().nextInt(nativeAdsStartApp.size() - 1);

                            RelativeLayout nativeAdView = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_startapp, null);
                            populateStartAppNativeAdView(nativeAdsStartApp.get(i), nativeAdView);

                            ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                            ((ADViewHolder) holder).rl_native_ad.addView(nativeAdView);
                            ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                            break;
                        case Callback.AD_TYPE_APPLOVIN:
                            MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Callback.ad_native_id, context);
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                    nativeAdView.setPadding(0, 0, 0, 10);
                                    nativeAdView.setBackgroundColor(Color.WHITE);
                                    ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                                    ((ADViewHolder) holder).rl_native_ad.addView(nativeAdView);
                                    ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                }

                                @Override
                                public void onNativeAdClicked(final MaxAd ad) {
                                }
                            });
                            nativeAdLoader.loadAd();
                            break;
                        case Callback.AD_TYPE_UNITY:
                            BannerView bannerView = new BannerView((Activity) context, "banner", new UnityBannerSize(320, 50));
                            bannerView.setListener(new BannerView.Listener() {
                                @Override
                                public void onBannerLoaded(BannerView bannerAdView) {
                                    super.onBannerLoaded(bannerAdView);
                                    ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                                    super.onBannerFailedToLoad(bannerAdView, errorInfo);
                                    ((ADViewHolder) holder).rl_native_ad.setVisibility(View.GONE);
                                }

                                @Override
                                public void onBannerClick(BannerView bannerAdView) {
                                    super.onBannerClick(bannerAdView);
                                }

                                @Override
                                public void onBannerLeftApplication(BannerView bannerAdView) {
                                    super.onBannerLeftApplication(bannerAdView);
                                }
                            });
                            bannerView.load();
                            bannerView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                            ((ADViewHolder) holder).rl_native_ad.addView(bannerView);
                            break;
                        case Callback.AD_TYPE_IRONSOURCE:
                            IronSource.init((Activity) context, Callback.iron_ads_id, IronSource.AD_UNIT.BANNER);
                            IronSourceBannerLayout banner = IronSource.createBanner((Activity) context, ISBannerSize.BANNER);
                            ((ADViewHolder) holder).rl_native_ad.addView(banner);
                            banner.setBannerListener(new com.ironsource.mediationsdk.sdk.BannerListener() {
                                @Override
                                public void onBannerAdLoaded() {

                                }

                                @Override
                                public void onBannerAdLoadFailed(IronSourceError error) {
                                    ((ADViewHolder) holder).rl_native_ad.setVisibility(View.GONE);
                                }

                                @Override
                                public void onBannerAdClicked() {

                                }

                                @Override
                                public void onBannerAdScreenPresented() {

                                }

                                @Override
                                public void onBannerAdScreenDismissed() {

                                }

                                @Override
                                public void onBannerAdLeftApplication() {

                                }
                            });
                            IronSource.loadBanner(banner);
                            break;
                    }
                }
            }
        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void hideHeader() {
        try {
            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_PROG;
        } else if (arrayList.get(position) == null) {
            return 1000 + position;
        } else {
            return position;
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemSigns itemData, int position);
    }

    @SuppressLint("MissingPermission")
    private void loadNativeAds() {
        if (Callback.isNativeAd && helper.isAdsShowNative()) {
            switch (Callback.adNetwork) {
                case Callback.AD_TYPE_ADMOB:
                    AdLoader.Builder builder = new AdLoader.Builder(context, Callback.ad_native_id);
                    AdLoader adLoader = builder.forNativeAd(
                            nativeAd -> {
                                mNativeAdsAdmob.add(nativeAd);
                                isAdLoaded = true;
                            }).build();

                    // Load the Native Express ad.
                    Bundle extras = new Bundle();
                    if (ConsentInformation.getInstance(context).getConsentStatus() != ConsentStatus.PERSONALIZED) {
                        extras.putString("npa", "1");
                    }
                    AdRequest adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                            .addNetworkExtrasBundle(FacebookAdapter.class, new FacebookExtras().build())
                            .build();

                    adLoader.loadAds(adRequest, 5);
                    break;
                case Callback.AD_TYPE_FACEBOOK:
                    mNativeAdsManager = new NativeAdsManager(context, Callback.ad_native_id, 5);
                    mNativeAdsManager.setListener(new NativeAdsManager.Listener() {
                        @Override
                        public void onAdsLoaded() {
                            isAdLoaded = true;
                        }

                        @Override
                        public void onAdError(AdError adError) {

                        }
                    });
                    mNativeAdsManager.loadAds();
                    break;
                case Callback.AD_TYPE_STARTAPP:
                    StartAppNativeAd nativeAd = new StartAppNativeAd(context);
                    nativeAd.loadAd(new NativeAdPreferences()
                            .setAdsNumber(3)
                            .setAutoBitmapDownload(true)
                            .setPrimaryImageSize(2), new AdEventListener() {
                        @Override
                        public void onReceiveAd(@NonNull Ad ad) {
                            nativeAdsStartApp.addAll(nativeAd.getNativeAds());
                            isAdLoaded = true;
                        }

                        @Override
                        public void onFailedToReceiveAd(Ad ad) {
                        }
                    });
                    break;
                case Callback.AD_TYPE_APPLOVIN:
                case Callback.AD_TYPE_UNITY:
                case Callback.AD_TYPE_IRONSOURCE:
                    isAdLoaded = true;
                    break;
            }
        } else {
            isAdLoaded = false;
        }
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    private void populateStartAppNativeAdView(@NonNull NativeAdDetails nativeAdDetails, @NonNull RelativeLayout nativeAdView) {
        ImageView icon = nativeAdView.findViewById(R.id.icon);
        TextView title = nativeAdView.findViewById(R.id.title);
        TextView description = nativeAdView.findViewById(R.id.description);
        Button button = nativeAdView.findViewById(R.id.button);

        icon.setImageBitmap(nativeAdDetails.getImageBitmap());
        title.setText(nativeAdDetails.getTitle());
        description.setText(nativeAdDetails.getDescription());
        button.setText(nativeAdDetails.isApp() ? "Install" : "Open");
    }

    public void destroyNativeAds() {
        try {
            for (int i = 0; i < mNativeAdsAdmob.size(); i++) {
                mNativeAdsAdmob.get(i).destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}