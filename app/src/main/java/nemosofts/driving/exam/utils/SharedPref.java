package nemosofts.driving.exam.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import nemosofts.driving.exam.callback.Callback;

public class SharedPref {

    private final EncryptData encryptData;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private static final String TAG_FIRST_OPEN = "firstopen", TAG_IS_LOGGED = "islogged", TAG_UID = "uid", TAG_USERNAME = "name",
            TAG_EMAIL = "email", TAG_MOBILE = "mobile", TAG_GENDER = "gender", TAG_REMEMBER = "rem" ,
            TAG_PASSWORD = "pass", SHARED_PREF_AUTOLOGIN = "autologin", TAG_LOGIN_TYPE = "loginType",
            TAG_AUTH_ID = "auth_id", TAG_IMAGES = "profile";

    private static final String TAG_PUBLISHER_ID = "publisher_id ", TAG_STARTAPP_ID = "startapp_id",
            TAG_UNITY_ID = "unity_id", TAG_IRON_ID = "iron_id", TAG_AD_IS_BANNER = "isbanner",
            TAG_AD_IS_INTER = "isinter", TAG_AD_IS_NATIVE = "isnative", TAG_AD_NETWORK = "ad_network",
            TAG_AD_ID_BANNER = "id_banner", TAG_AD_ID_INTER = "id_inter", TAG_AD_ID_NATIVE = "id_native",
            TAG_AD_NATIVE_POS = "native_pos", TAG_AD_INTER_POS = "inter_pos";

    private static final String TAG_NIGHT_MODE = "night_mode", TAG_THEME = "my_theme";

    public SharedPref(Context ctx) {
        encryptData = new EncryptData(ctx);
        sharedPreferences = ctx.getSharedPreferences("setting_app", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setIsFirst(Boolean flag) {
        editor.putBoolean(TAG_FIRST_OPEN, flag);
        editor.apply();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean(TAG_FIRST_OPEN, true);
    }

    public void setIsLogged(Boolean isLogged) {
        editor.putBoolean(TAG_IS_LOGGED, isLogged);
        editor.apply();
    }

    public boolean isLogged() {
        return sharedPreferences.getBoolean(TAG_IS_LOGGED, false);
    }

    public void setLoginDetails(String id, String name, String mobile, String email, String gender, @NonNull String profilePic, String authID, Boolean isRemember, String password, String loginType) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_UID, encryptData.encrypt(id));
        editor.putString(TAG_USERNAME, encryptData.encrypt(name));
        editor.putString(TAG_MOBILE, encryptData.encrypt(mobile));
        editor.putString(TAG_EMAIL, encryptData.encrypt(email));
        editor.putString(TAG_GENDER, encryptData.encrypt(gender));
        editor.putString(TAG_PASSWORD, encryptData.encrypt(password));
        editor.putString(TAG_LOGIN_TYPE, encryptData.encrypt(loginType));
        editor.putString(TAG_AUTH_ID, encryptData.encrypt(authID));
        editor.putString(TAG_IMAGES, encryptData.encrypt(profilePic.replace(" ", "%20")));
        editor.apply();
    }

    public void setRemember(Boolean isRemember) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, "");
        editor.apply();
    }

    public String getUserId() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_UID, ""));
    }

    public void setUserName(String userName) {
        editor.putString(TAG_USERNAME, encryptData.encrypt(userName));
        editor.apply();
    }

    public String getUserName() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_USERNAME, ""));
    }

    public void setEmail(String email) {
        editor.putString(TAG_EMAIL, encryptData.encrypt(email));
        editor.apply();
    }

    public String getEmail() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_EMAIL,""));
    }

    public void setUserMobile(String mobile) {
        editor.putString(TAG_MOBILE, encryptData.encrypt(mobile));
        editor.apply();
    }

    public String getUserMobile() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_MOBILE, ""));
    }

    public String getPassword() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_PASSWORD,""));
    }

    public Boolean getIsAutoLogin() { return sharedPreferences.getBoolean(SHARED_PREF_AUTOLOGIN, false); }

    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(SHARED_PREF_AUTOLOGIN, isAutoLogin);
        editor.apply();
    }

    public Boolean getIsRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }


    public String getLoginType() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_LOGIN_TYPE,""));
    }

    public String getAuthID() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_AUTH_ID,""));
    }
    public String getProfileImages() {
        return encryptData.decrypt(sharedPreferences.getString(TAG_IMAGES,""));
    }

    public void setDarkMode(Boolean state) {
        editor.putBoolean(TAG_NIGHT_MODE, state);
        editor.apply();
    }

    public int getModeTheme() {
        return sharedPreferences.getInt(TAG_THEME, 0);
    }

    public void setModeTheme(int state) {
        editor.putInt(TAG_THEME, state);
        editor.apply();
    }

    public void getThemeDetails() {
        Callback.isDarkMode = sharedPreferences.getBoolean(TAG_NIGHT_MODE, false);
        Callback.isDarkModeTheme = sharedPreferences.getInt(TAG_THEME, 0);
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }

    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public void setAdDetails(boolean isBanner, boolean isInter, boolean isNative, String adNetwork,
                             String publisher_id, String startapp_id, String unity_id, String iron_id,
                             String banner_id, String inter_id, String native_id,
                             int interPos, int nativePos) {
        editor.putString(TAG_AD_NETWORK, adNetwork);
        editor.putBoolean(TAG_AD_IS_BANNER, isBanner);
        editor.putBoolean(TAG_AD_IS_INTER, isInter);
        editor.putBoolean(TAG_AD_IS_NATIVE, isNative);
        editor.putString(TAG_PUBLISHER_ID, encryptData.encrypt(publisher_id));
        editor.putString(TAG_STARTAPP_ID, encryptData.encrypt(startapp_id));
        editor.putString(TAG_UNITY_ID, encryptData.encrypt(unity_id));
        editor.putString(TAG_IRON_ID, encryptData.encrypt(iron_id));
        editor.putString(TAG_AD_ID_BANNER, encryptData.encrypt(banner_id));
        editor.putString(TAG_AD_ID_INTER, encryptData.encrypt(inter_id));
        editor.putString(TAG_AD_ID_NATIVE, encryptData.encrypt(native_id));
        editor.putInt(TAG_AD_INTER_POS, interPos);
        editor.putInt(TAG_AD_NATIVE_POS, nativePos);
        editor.apply();
    }

    public void getAdDetails() {
        Callback.adNetwork = sharedPreferences.getString(TAG_AD_NETWORK, Callback.AD_TYPE_ADMOB);

        Callback.ad_publisher_id = encryptData.decrypt(sharedPreferences.getString(TAG_PUBLISHER_ID, ""));
        Callback.startapp_id = encryptData.decrypt(sharedPreferences.getString(TAG_STARTAPP_ID, ""));
        Callback.unity_ads_id = encryptData.decrypt(sharedPreferences.getString(TAG_UNITY_ID, ""));
        Callback.iron_ads_id = encryptData.decrypt(sharedPreferences.getString(TAG_IRON_ID, ""));

        Callback.ad_banner_id = encryptData.decrypt(sharedPreferences.getString(TAG_AD_ID_BANNER, ""));
        Callback.ad_inter_id = encryptData.decrypt(sharedPreferences.getString(TAG_AD_ID_INTER, ""));
        Callback.ad_native_id = encryptData.decrypt(sharedPreferences.getString(TAG_AD_ID_NATIVE, ""));

        Callback.adInterstitialShow = sharedPreferences.getInt(TAG_AD_INTER_POS, 5);
        Callback.adInterstitialShow = sharedPreferences.getInt(TAG_AD_NATIVE_POS, 6);
    }


    public Boolean getSelectLanguage() {
        return sharedPreferences.getBoolean("select_language", true);
    }
    public void setSelectLanguage(Boolean state) {
        editor.putBoolean("select_language", state);
        editor.apply();
    }
    public void setLanguage(int id, int position) {
        editor.putInt("language_id", id);
        editor.putInt("language_position", position);
        editor.apply();
    }
    public int getLanguage() {
        return sharedPreferences.getInt("language_id", 0);
    }
    public int getLanguagePosition() {
        return sharedPreferences.getInt("language_position", 0);
    }

    public boolean getIsIntroduction() {
        return sharedPreferences.getBoolean("IsIntroduction", false);
    }
    public void setIsIntroduction(Boolean state) {
        editor.putBoolean("IsIntroduction", state);
        editor.apply();
    }
}