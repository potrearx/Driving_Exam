package nemosofts.driving.exam.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.lk.ProCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.adapter.AdapterResultsOld;
import nemosofts.driving.exam.ifSupported.IsRTL;
import nemosofts.driving.exam.ifSupported.IsScreenshot;
import nemosofts.driving.exam.item.ItemResult;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.DBHelper;

public class OldResultsActivity extends ProCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        dbHelper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        ArrayList<ItemResult> arrayList = new ArrayList<>();

        RecyclerView rv = findViewById(R.id.rv_result);
        LinearLayoutManager llm_top_songs = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(llm_top_songs);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        try {
            arrayList.addAll(dbHelper.getResult(DBHelper.TABLE_RESULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (arrayList.size() != 0) {
            AdapterResultsOld adapterResult = new AdapterResultsOld(arrayList);
            rv.setAdapter(adapterResult);
        }

        findViewById(R.id.iv_clean_results).setOnClickListener(v -> {
            if (!arrayList.isEmpty()){
                dbHelper.removeAllResult();
                rv.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_old_results;
    }

    @Override
    protected int setApplicationThemes() {
        return ApplicationUtil.isTheme();
    }
}