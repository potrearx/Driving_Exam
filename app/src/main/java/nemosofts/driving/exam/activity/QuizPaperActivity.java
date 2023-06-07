package nemosofts.driving.exam.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.lk.ProCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.adapter.AdapterResults;
import nemosofts.driving.exam.asyncTask.LoadQuiz;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.ifSupported.IsRTL;
import nemosofts.driving.exam.ifSupported.IsScreenshot;
import nemosofts.driving.exam.interfaces.QuizListener;
import nemosofts.driving.exam.item.ItemQuiz;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.Helper;
import nemosofts.driving.exam.utils.SharedPref;

public class QuizPaperActivity extends ProCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private RecyclerView rv;
    private ArrayList<ItemQuiz> arrayList;
    private FrameLayout frameLayout;
    private String error_msg;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        arrayList = new ArrayList<>();

        pb = findViewById(R.id.pb);
        rv = findViewById(R.id.rv);
        frameLayout = findViewById(R.id.fl_empty);

        LinearLayoutManager llm = new LinearLayoutManager(QuizPaperActivity.this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        getData();

        LinearLayout adView = findViewById(R.id.ll_adView);
        helper.showBannerAd(adView);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_quiz_paper;
    }

    @Override
    protected int setApplicationThemes() {
        return ApplicationUtil.isTheme();
    }

    private void getData() {
        if (helper.isNetworkAvailable()) {
            LoadQuiz loadQuiz = new LoadQuiz(new QuizListener() {
                @Override
                public void onStart() {
                    rv.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String message, ArrayList<ItemQuiz> arrayList_img, ArrayList<ItemQuiz> arrayList_no_img) {
                    if (success.equals("1")) {
                        if (arrayList_no_img.isEmpty() && arrayList_img.isEmpty()) {
                            error_msg = getString(R.string.error_no_data_found);
                            setEmpty();
                        } else {
                            if (!arrayList_img.isEmpty()){
                                arrayList.addAll(arrayList_img);
                            }
                            if (!arrayList_no_img.isEmpty()){
                                arrayList.addAll(arrayList_no_img);
                            }
                            setAdapterToListview();
                        }
                    } else {
                        error_msg = getString(R.string.error_server_not_connected);
                        setEmpty();
                    }
                }
            }, helper.callAPI(Callback.METHOD_QUIZ, 0, String.valueOf(sharedPref.getLanguage()), "","","","","","","","","","","",null));
            loadQuiz.execute();
        } else {
            error_msg = getString(R.string.error_internet_not_connected);
            setEmpty();
        }
    }

    private void setAdapterToListview() {
        AdapterResults adapter = new AdapterResults(this, arrayList, false);
        rv.setAdapter(adapter);
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
}