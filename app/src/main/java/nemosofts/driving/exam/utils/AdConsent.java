package nemosofts.driving.exam.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nemosofts.driving.exam.callback.Callback;

public class AdConsent {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String TAG_CURRENT_DATE_TIME = "current_date";
    private static final String TAG_ADS_COUNT = "ad_count";
    private static final String TAG_ADS_SHOW = "is_ads_show";

    public AdConsent(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences("ads_consent", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @SuppressLint("SimpleDateFormat")
    public void setCurrentDate(){
        if (Callback.isAdsLimits) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
            String currentDateTime = simpleDateFormat.format(calendar.getTime());
            String getSavedTime = getCurrentDate();
            if (currentDateTime.compareTo(getSavedTime) > 0) {
                editor.putString(TAG_CURRENT_DATE_TIME, currentDateTime);
                editor.putBoolean(TAG_ADS_SHOW, true);
                editor.putInt(TAG_ADS_COUNT, 0);
                editor.apply();
            }
        }
    }

    private String getCurrentDate() {
        return sharedPreferences.getString(TAG_CURRENT_DATE_TIME, "00/00");
    }

    public void setAdsCount() {
        if (Callback.isAdsLimits){
            int adCount = getAdsCount() + 1;
            if (adCount % Callback.adCountClick == 0) {
                editor.putBoolean(TAG_ADS_SHOW, false);
            }
            editor.putInt(TAG_ADS_COUNT, adCount);
            editor.apply();
        }
    }

    private int getAdsCount() {
        return sharedPreferences.getInt(TAG_ADS_COUNT, 0);
    }

    public Boolean isAdsShow() {
        return sharedPreferences.getBoolean(TAG_ADS_SHOW, true);
    }

}
