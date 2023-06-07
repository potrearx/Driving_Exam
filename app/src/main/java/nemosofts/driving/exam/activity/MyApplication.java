package nemosofts.driving.exam.activity;

import android.content.Context;
import android.os.StrictMode;

import androidx.multidex.MultiDex;
import androidx.nemosofts.lk.BaseApplication;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import nemosofts.driving.exam.BuildConfig;
import nemosofts.driving.exam.R;
import nemosofts.driving.exam.utils.DBHelper;
import nemosofts.driving.exam.utils.Helper;
import nemosofts.driving.exam.utils.SharedPref;

public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAnalytics.getInstance(getApplicationContext());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        try {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            dbHelper.onCreate(dbHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(getString(R.string.onesignal_app_id));

        new SharedPref(getApplicationContext()).getAdDetails();
        new Helper(getApplicationContext()).initializeAds();
    }

    @Override
    public String setProductID() {
        return "33907205";
    }

    @Override
    public String setApplicationID() {
        return BuildConfig.APPLICATION_ID;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
