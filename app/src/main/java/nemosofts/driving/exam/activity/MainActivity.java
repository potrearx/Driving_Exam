package nemosofts.driving.exam.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.nemosofts.lk.ProCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.asyncTask.LoadAbout;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.dialog.ExitDialog;
import nemosofts.driving.exam.ifSupported.IsRTL;
import nemosofts.driving.exam.ifSupported.IsScreenshot;
import nemosofts.driving.exam.interfaces.AboutListener;
import nemosofts.driving.exam.utils.AdConsent;
import nemosofts.driving.exam.utils.AdManagerInterAdmob;
import nemosofts.driving.exam.utils.AdManagerInterApplovin;
import nemosofts.driving.exam.utils.AdManagerInterFB;
import nemosofts.driving.exam.utils.AdManagerInterStartApp;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.Helper;
import nemosofts.driving.exam.utils.SharedPref;

public class MainActivity extends ProCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Helper helper;
    private SharedPref sharedPref;
    private MenuItem menu_login;
    private MenuItem menu_profile;
    private ReviewManager manager;
    private ReviewInfo reviewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        sharedPref.getThemeDetails();
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        AdConsent adConsent = new AdConsent(this);
        adConsent.setCurrentDate();

        helper = new Helper(this);
        helper = new Helper(this, (position, type) -> {
            switch (type) {
                case "cat":
                    startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                    break;
                case "quiz":
                    startActivity(new Intent(MainActivity.this, QuizDetails.class));
                    break;
                case "old_result":
                    startActivity(new Intent(MainActivity.this, OldResultsActivity.class));
                    break;
                case "quiz_paper":
                    startActivity(new Intent(MainActivity.this, QuizPaperActivity.class));
                    break;
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setToolbarNavigationClickListener(view -> drawer.openDrawer(GravityCompat.START));
        if (ApplicationUtil.isDarkMode()) {
            toggle.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        } else {
            toggle.setHomeAsUpIndicator(R.drawable.ic_menu_black);
        }
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);

        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        menu_login = menu.findItem(R.id.nav_login);
        menu_profile = menu.findItem(R.id.nav_profile);

        changeLoginName();
        loadAboutData();

        findViewById(R.id.ll_signs).setOnClickListener(view -> helper.showInter(0,"cat"));
        findViewById(R.id.ll_exam).setOnClickListener(view -> helper.showInter(0,"quiz"));
        findViewById(R.id.btn_result).setOnClickListener(view -> helper.showInter(0,"old_result"));
        findViewById(R.id.ll_questions).setOnClickListener(view -> helper.showInter(0,"quiz_paper"));

        findViewById(R.id.iv_notifications).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NotificationActivity.class)));

        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changeLoginName() {
        if (menu_login != null) {
            if (sharedPref.isLogged()) {
                menu_profile.setVisible(true);
                menu_login.setTitle(getResources().getString(R.string.logout));
                menu_login.setIcon(getResources().getDrawable(R.drawable.ic_logout));

            } else {
                menu_profile.setVisible(false);
                menu_login.setTitle(getResources().getString(R.string.login));
                menu_login.setIcon(getResources().getDrawable(R.drawable.ic_login));
            }
        }
    }
    public void loadAboutData() {
        if (helper.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(MainActivity.this, new AboutListener() {
                @Override
                public void onStart() {
                    // TODO document why this method is empty
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        helper.initializeAds();
                        sharedPref.setAdDetails(Callback.isBannerAd, Callback.isInterAd, Callback.isNativeAd, Callback.adNetwork,
                                Callback.ad_publisher_id, Callback.startapp_id, Callback.unity_ads_id, Callback.iron_ads_id,
                                Callback.ad_banner_id, Callback.ad_inter_id, Callback.ad_native_id, Callback.adInterstitialShow, Callback.adNativeShow);

                        if (Callback.isInterAd) {
                            switch (Callback.adNetwork) {
                                case Callback.AD_TYPE_ADMOB:
                                    AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(getApplicationContext());
                                    adManagerInterAdmob.createAd();
                                    break;
                                case Callback.AD_TYPE_FACEBOOK:
                                    AdManagerInterFB adManagerInterFB = new AdManagerInterFB(getApplicationContext());
                                    adManagerInterFB.createAd();
                                    break;
                                case Callback.AD_TYPE_STARTAPP:
                                    AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(getApplicationContext());
                                    adManagerInterStartApp.createAd();
                                    break;
                                case Callback.AD_TYPE_APPLOVIN:
                                    AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(MainActivity.this);
                                    adManagerInterApplovin.createAd();
                                    break;
                            }
                        }
                    }
                }
            });
            loadAbout.execute();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_translation:
                startActivity(new Intent(MainActivity.this, LanguageActivity.class));
                break;
            case R.id.nav_road_signs:
                startActivity(new Intent(MainActivity.this, SignsActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.nav_settings:
                overridePendingTransition(0, 0);
                overridePendingTransition(0, 0);
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                finish();
                break;
            case R.id.nav_login:
                helper.clickLogin();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        changeLoginName();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (reviewInfo != null){
            Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
            flow.addOnCompleteListener(task1 -> new ExitDialog(this));
        } else {
            new ExitDialog(this);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected int setApplicationThemes() {
        return new SharedPref(this).getModeTheme();
    }

}