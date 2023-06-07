package nemosofts.driving.exam.ifSupported;

import android.app.Activity;
import android.view.View;

import nemosofts.driving.exam.callback.Callback;

public class IsRTL {

    public static void ifSupported(Activity mContext) {
        if (Callback.isRTL) {
            try {
                mContext.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
