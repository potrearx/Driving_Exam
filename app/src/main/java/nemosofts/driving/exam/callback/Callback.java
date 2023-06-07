package nemosofts.driving.exam.callback;

import java.io.Serializable;
import java.util.ArrayList;

import nemosofts.driving.exam.BuildConfig;
import nemosofts.driving.exam.item.ItemAbout;
import nemosofts.driving.exam.item.ItemQuiz;

public class Callback implements Serializable {
    private static final long serialVersionUID = 1L;

    // API URL
    public static String API_URL = BuildConfig.BASE_URL+"api.php";

    // TAG_API
    public static final String TAG_ROOT = "DRIVING_EXAM_APP";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MSG = "MSG";

    // Method
    public static final String METHOD_APP_DETAILS = "app_details";
    public static final String METHOD_LOGIN = "user_login";
    public static final String METHOD_REGISTER = "user_register";
    public static final String METHOD_PROFILE = "user_profile";
    public static final String METHOD_ACCOUNT_DELETE = "account_delete";
    public static final String METHOD_EDIT_PROFILE = "edit_profile";
    public static final String METHOD_USER_IMAGES_UPDATE = "user_images_update";
    public static final String METHOD_FORGOT_PASSWORD = "forgot_pass";
    public static final String METHOD_NOTIFICATION = "get_notification";
    public static final String METHOD_REMOVE_NOTIFICATION = "remove_notification";
    public static final String METHOD_REPORT = "post_report";

    public static final String METHOD_LANGUAGE = "get_language_list";
    public static final String METHOD_CAT = "get_cat_list";
    public static final String METHOD_CAT_ID = "get_cat_by";
    public static final String METHOD_QUIZ = "get_quiz";


    public static Boolean isDarkMode = false, isProfileUpdate = false;
    public static int isDarkModeTheme = 0;

    public static final String LOGIN_TYPE_NORMAL = "Normal";
    public static final String LOGIN_TYPE_GOOGLE = "Google";

    public static ArrayList<ItemQuiz> arrayList = new ArrayList<>();
    public static int okAnswer = 0, totelAnswer = 0, noAnswer = 0;

    // About Details
    public static ItemAbout itemAbout = new ItemAbout("","","","","","");

    public static int recentLimit = 10;

    public static Boolean isBannerAd = true, isInterAd = true, isNativeAd = true;
    public static String adNetwork = "admob", ad_publisher_id = "", startapp_id = "", unity_ads_id = "", iron_ads_id = ""
            ,ad_banner_id = "", ad_inter_id = "", ad_native_id = "";
    public static int adCount = 0, adInterstitialShow = 5, adNativeShow = 6;

    public static final String AD_TYPE_ADMOB = "admob", AD_TYPE_FACEBOOK = "facebook", AD_TYPE_STARTAPP = "startapp"
            ,AD_TYPE_APPLOVIN = "applovins", AD_TYPE_UNITY = "unity", AD_TYPE_IRONSOURCE = "iron";

    public static Boolean isAdsLimits = true;
    public static int adCountClick = 15;

    public static Boolean isCustomAds = false;
    public static int customAdCount = 0, customAdShow = 12;
    public static String custom_ads_img = "", custom_ads_link = "";

    public static Boolean isRTL = false, isVPN = false, isAPK = false, isMaintenance = false,
            isScreenshot = false, isLogin = false, isGoogleLogin = false;

    public static Boolean isAppUpdate = false;
    public static int app_new_version = 1;
    public static String app_update_desc = "", app_redirect_url = "";


    public static final String TAG_CID = "cid";
    public static final String TAG_CAT_NAME = "category_name";
    public static final String TAG_CAT_IMAGE = "category_image";

}