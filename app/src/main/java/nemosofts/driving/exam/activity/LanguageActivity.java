package nemosofts.driving.exam.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.lk.ProCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.adapter.AdapterLanguage;
import nemosofts.driving.exam.asyncTask.LoadLanguage;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.ifSupported.IsRTL;
import nemosofts.driving.exam.ifSupported.IsScreenshot;
import nemosofts.driving.exam.interfaces.LanguageListener;
import nemosofts.driving.exam.item.ItemLanguage;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.DBHelper;
import nemosofts.driving.exam.utils.Helper;
import nemosofts.driving.exam.utils.SharedPref;

public class LanguageActivity extends ProCompatActivity {

    private Helper helper;
    private DBHelper dbHelper;
    private SharedPref sharedPref;
    private RecyclerView rv;
    private AdapterLanguage adapter;
    private ArrayList<ItemLanguage> arrayList;
    private ProgressBar pb;
    private FrameLayout frameLayout;
    private String error_msg;
    private Button btn_home;
    private AnimatorSet set;
    private Boolean add_btn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        helper = new Helper(this);
        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);

        helper = new Helper(this, (position, type) -> {
            String id = arrayList.get(position).getId();
            sharedPref.setLanguage(Integer.parseInt(id),position);
            adapter.select(position);
            getButton();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arrayList = new ArrayList<>();

        pb = findViewById(R.id.pb);
        rv = findViewById(R.id.rv);
        frameLayout = findViewById(R.id.fl_empty);

        GridLayoutManager glm = new GridLayoutManager(LanguageActivity.this, 2);
        rv.setLayoutManager(glm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        btn_home = findViewById(R.id.btn_lan);
        isAnimatorButton(btn_home);
        btn_home.setText("No Select");
        btn_home.setBackgroundResource(R.drawable.bg_button_normal);
        add_btn = false;

        btn_home.setOnClickListener(v -> {
            if (add_btn){
                sharedPref.setSelectLanguage(false);
                sharedPref.setIsIntroduction(true);
                if (Callback.isLogin){
                    Intent intent = new Intent(LanguageActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from", "");
                    startActivity(intent);
                    finish();
                } else {
                    sharedPref.setIsFirst(false);
                    Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        arrayList = dbHelper.getLanguage();
        if (!arrayList.isEmpty()){
            setAdapterToListview();
        } else {
            getData();
        }

        findViewById(R.id.iv_refresh).setOnClickListener(view -> getData());
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_language;
    }

    @Override
    protected int setApplicationThemes() {
        return ApplicationUtil.isTheme();
    }

    private void getData() {
        if (helper.isNetworkAvailable()){
            LoadLanguage startLoad = new LoadLanguage(this, new LanguageListener() {
                @Override
                public void onStart() {
                    if (!arrayList.isEmpty()){
                        arrayList.clear();
                    }
                    add_btn = false;
                    btn_home.setText("No Select");
                    btn_home.setBackgroundResource(R.drawable.bg_button_normal);
                    rv.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemLanguage> languageArrayList) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            if (languageArrayList.isEmpty()) {
                                error_msg = getString(R.string.error_no_data_found);
                                setEmpty();
                            } else {
                                arrayList = dbHelper.getLanguage();
                                if (!arrayList.isEmpty()){
                                    setAdapterToListview();
                                }else {
                                    error_msg = getString(R.string.error_no_data_found);
                                    setEmpty();
                                }
                            }
                        } else {
                            helper.getVerifyDialog(getString(R.string.error_unauthorized_access), message);
                        }
                    }else {
                        error_msg = getString(R.string.error_server_not_connected);
                        setEmpty();
                    }
                }
            },helper.callAPI(Callback.METHOD_LANGUAGE, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            startLoad.execute();
        }else {
            error_msg = getString(R.string.error_internet_not_connected);
            setEmpty();
        }
    }

    private void setAdapterToListview() {
        adapter = new AdapterLanguage(LanguageActivity.this, arrayList, (item, position) -> {
            helper.showInter(position,"");
        });
        rv.setAdapter(adapter);
        if (!sharedPref.getSelectLanguage()){
            adapter.select(sharedPref.getLanguagePosition());
            getButton();
        }
        setEmpty();
    }

    private void setEmpty() {
        if (arrayList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.INVISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            pb.setVisibility(View.INVISIBLE);
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_empty, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(error_msg);

            myView.findViewById(R.id.btn_empty_try).setOnClickListener(v -> getData());

            frameLayout.addView(myView);
        }
    }

    private void isAnimatorButton(Button view) {
        final float from = 1.0f;
        final float to = 1.3f;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, from, to);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y,  from, to);
        ObjectAnimator translationZ = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, from, to);

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(scaleX, scaleY, translationZ);
        set1.setDuration(100);
        set1.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator scaleXBack = ObjectAnimator.ofFloat(view, View.SCALE_X, to, from);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(view, View.SCALE_Y, to, from);
        ObjectAnimator translationZBack = ObjectAnimator.ofFloat(view, View.TRANSLATION_Z, to, from);

        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(0.5f, 1.3f);
        path.lineTo(0.75f, 0.8f);
        path.lineTo(1.0f, 1.0f);
        PathInterpolator pathInterpolator = new PathInterpolator(path);

        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(scaleXBack, scaleYBack, translationZBack);
        set2.setDuration(300);
        set2.setInterpolator(pathInterpolator);

        set = new AnimatorSet();
        set.playSequentially(set1, set2);
    }

    private void getButton() {
        if (!add_btn){
            btn_home.setText("Next");
            btn_home.setBackgroundResource(R.drawable.bg_button);
            set.start();
            add_btn = true;
        }
    }
}