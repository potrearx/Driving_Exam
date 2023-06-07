package nemosofts.driving.exam.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.nemosofts.lk.Envato;
import androidx.nemosofts.lk.ProSplashActivity;
import androidx.nemosofts.lk.asyncTask.LoadProduct;
import androidx.nemosofts.lk.interfaces.ProductListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nemosofts.driving.exam.BuildConfig;
import nemosofts.driving.exam.R;
import nemosofts.driving.exam.asyncTask.LoadAbout;
import nemosofts.driving.exam.asyncTask.LoadLogin;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.dialog.MaintenanceDialog;
import nemosofts.driving.exam.dialog.UpgradeDialog;
import nemosofts.driving.exam.interfaces.AboutListener;
import nemosofts.driving.exam.interfaces.LoginListener;
import nemosofts.driving.exam.utils.Helper;
import nemosofts.driving.exam.utils.SharedPref;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends ProSplashActivity implements ProductListener {

    private Helper helper;
    private Envato envato;
    private SharedPref sharedPref;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();

        envato = new Envato(this);
        helper = new Helper(this);
        sharedPref = new SharedPref(this);
        sharedPref.getThemeDetails();

        pb = findViewById(R.id.pb);

        loadAboutData();
    }

    private void loadAboutData() {
        if (helper.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(SplashActivity.this, new AboutListener() {
                @Override
                public void onStart() {
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message){
                    pb.setVisibility(View.GONE);
                    if (success.equals("1")){
                        if (!verifyStatus.equals("-1") && !verifyStatus.equals("-2")){
                            setSaveData();
                        } else if (verifyStatus.equals("-2")){
                            helper.getInvalidUserDialog(message);
                        } else {
                            errorDialog(getString(R.string.error_unauthorized_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.error_server), getString(R.string.error_server_not_connected));
                    }
                }
            });
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.error_internet_not_connected), getString(R.string.error_try_internet_connected));
        }
    }

    private void setSaveData() {
        if (envato.isNetworkAvailable()) {
            LoadProduct loadProduct = new LoadProduct(this, this);
            loadProduct.execute();
        } else {
            if (envato.getIsEnvato()){
                loadSettings();
            } else {
                setSaveData();
                Toast.makeText(SplashActivity.this, "Please wait a minute", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadSettings() {
        if (Callback.isAppUpdate && Callback.app_new_version != BuildConfig.VERSION_CODE){
            new UpgradeDialog(this);
        } else if(Callback.isMaintenance){
            new MaintenanceDialog(this);
        } else {
            if (sharedPref.getIsFirst()) {
                if (sharedPref.getIsIntroduction()) {
                    if (Callback.isLogin){
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("from", "");
                        startActivity(intent);
                        finish();
                    } else {
                        sharedPref.setIsFirst(false);
                        openMainActivity();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), LanguageActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                if (!sharedPref.getIsAutoLogin()) {
                    new Handler().postDelayed(this::openMainActivity, 2000);
                } else {
                    if (sharedPref.getLoginType().equals(Callback.LOGIN_TYPE_GOOGLE)) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            loadLogin(Callback.LOGIN_TYPE_GOOGLE, sharedPref.getAuthID());
                        } else {
                            sharedPref.setIsAutoLogin(false);
                            openMainActivity();
                        }
                    } else {
                        loadLogin(Callback.LOGIN_TYPE_NORMAL, "");
                    }
                }
            }
        }
    }

    private void loadLogin(final String loginType, final String authID) {
        if (helper.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name, String user_gender, String user_phone, String profile_img) {
                    pb.setVisibility(View.GONE);
                    if (success.equals("1")) {
                        if (!loginSuccess.equals("-1")) {
                            sharedPref.setLoginDetails(user_id, user_name, user_phone, sharedPref.getEmail(), user_gender, profile_img, authID, sharedPref.getIsRemember(), sharedPref.getPassword(), loginType);
                            sharedPref.setIsLogged(true);
                        }
                    }
                    openMainActivity();
                }
            }, helper.callAPI(Callback.METHOD_LOGIN, 0,"","","","","","",sharedPref.getEmail(),"","",sharedPref.getPassword(),authID,loginType,null));
            loadLogin.execute();
        } else {
            errorDialog(getString(R.string.error_internet_not_connected), getString(R.string.error_try_internet_connected));
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_splash;
    }

    @Override
    protected int setApplicationThemes() {
        return new SharedPref(this).getModeTheme();
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        if (title.equals(getString(R.string.error_internet_not_connected)) || title.equals(getString(R.string.error_server_not_connected))) {
            alertDialog.setNegativeButton(getString(R.string.retry), (dialog, which) -> loadAboutData());
        }
        alertDialog.setPositiveButton(getString(R.string.exit), (dialog, which) -> finish());
        alertDialog.show();
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onStartPairing() {
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected() {
        pb.setVisibility(View.GONE);
        loadSettings();
    }

    @Override
    public void onUnauthorized(String message) {
        pb.setVisibility(View.GONE);
        errorDialog(getString(R.string.error_unauthorized_access), message);
    }

    @Override
    public void onError() {
        pb.setVisibility(View.GONE);
        errorDialog(getString(R.string.error_server), getString(R.string.error_server_not_connected));
    }
}