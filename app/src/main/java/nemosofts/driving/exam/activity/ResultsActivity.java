package nemosofts.driving.exam.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.lk.ProCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.adapter.AdapterResults;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.ifSupported.IsRTL;
import nemosofts.driving.exam.ifSupported.IsScreenshot;
import nemosofts.driving.exam.item.ItemQuiz;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.DBHelper;

public class ResultsActivity extends ProCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        DBHelper dbHelper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rv = findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(ResultsActivity.this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        ArrayList<ItemQuiz> arrayList = new ArrayList<>(Callback.arrayList);

        if(!arrayList.isEmpty()){
            AdapterResults adapter = new AdapterResults(this, arrayList, true);
            rv.setAdapter(adapter);
        }

        TextView iv_cont = findViewById(R.id.iv_cont);
        iv_cont.setText(String.valueOf(Callback.okAnswer)+"/"+String.valueOf(Callback.totelAnswer));

        int totalProgress = (int)((double)Callback.okAnswer / Callback.totelAnswer * 100);

        TextView iv_cont_pa = findViewById(R.id.iv_cont_pa);
        iv_cont_pa.setText(String.valueOf(totalProgress)+"%");

        TextView results_type = findViewById(R.id.tv_results_type);
        if (totalProgress >= 0 && totalProgress < 40){
            results_type.setText("Fail");
            results_type.setTextColor(getResources().getColor(R.color.color_check_error));
        } else if (totalProgress >= 40 && totalProgress < 75) {
            results_type.setText("Normal");
            results_type.setTextColor(getResources().getColor(R.color.color_check_in));
        } else if (totalProgress >= 75 && totalProgress < 85){
            results_type.setText("Good");
            results_type.setTextColor(getResources().getColor(R.color.color_check_in));
        } else if (totalProgress >= 85 && totalProgress < 100){
            results_type.setText("Very Good");
            results_type.setTextColor(getResources().getColor(R.color.color_check_in));
        }

        findViewById(R.id.iv_close).setOnClickListener(view -> onBackPressed());

        @SuppressLint("SimpleDateFormat")
        String dateFormat = new SimpleDateFormat("d MMM yyyy").format(Calendar.getInstance().getTime());
        @SuppressLint("SimpleDateFormat")
        String time = new SimpleDateFormat("hh:mm aaa").format(Calendar.getInstance().getTime());
        String re = Callback.okAnswer+"/"+Callback.totelAnswer;
        dbHelper.addedResult(dateFormat, time, re);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_results;
    }

    @Override
    protected int setApplicationThemes() {
        return ApplicationUtil.isTheme();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}