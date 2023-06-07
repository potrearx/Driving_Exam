package nemosofts.driving.exam.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.callback.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApplicationUtil {

    public static Boolean isDarkMode() {
        return Callback.isDarkMode;
    }

    public static int isTheme() {
        return Callback.isDarkModeTheme;
    }

    @NonNull
    public static String responsePost(String url, RequestBody requestBody) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(25000, TimeUnit.MILLISECONDS)
                .writeTimeout(25000, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @NonNull
    public static String toBase64(@NonNull String input) {
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return new String(encodeValue);
    }

    @NonNull
    public static String viewFormat(@NonNull Integer number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    @NonNull
    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    public static int colorUtils(Context ctx) {
        if (Callback.isDarkModeTheme == 1) {
            return ContextCompat.getColor(ctx, R.color.titleNight);
        } else if (Callback.isDarkModeTheme == 2) {
            return ContextCompat.getColor(ctx, R.color.titleGrey);
        } else if (Callback.isDarkModeTheme == 3) {
            return ContextCompat.getColor(ctx, R.color.titleBlue);
        } else {
            return ContextCompat.getColor(ctx, R.color.titleLight);
        }
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    public static int colorUtilsWhite(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.white);
    }
}
