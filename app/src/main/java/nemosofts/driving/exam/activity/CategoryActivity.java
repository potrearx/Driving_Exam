package nemosofts.driving.exam.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.lk.ProCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.adapter.AdapterCategory;
import nemosofts.driving.exam.asyncTask.LoadCat;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.ifSupported.IsRTL;
import nemosofts.driving.exam.ifSupported.IsScreenshot;
import nemosofts.driving.exam.interfaces.CategoryListener;
import nemosofts.driving.exam.item.ItemCat;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.DBHelper;
import nemosofts.driving.exam.utils.Helper;


public class CategoryActivity extends ProCompatActivity {

    private Helper helper;
    private DBHelper dbHelper;
    private RecyclerView rv;
    private ArrayList<ItemCat> arrayList;
    private GridLayoutManager glm;
    private ProgressBar pb;
    private FrameLayout frameLayout;
    private String error_msg;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        dbHelper = new DBHelper(this);
        helper = new Helper(this);

        helper = new Helper(this, (position, type) -> {
            Intent intent = new Intent(CategoryActivity.this, SignsActivity.class);
            intent.putExtra("cid", arrayList.get(position).getId());
            intent.putExtra("name", arrayList.get(position).getName());
            startActivity(intent);
        });

        arrayList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        pb = findViewById(R.id.pb);
        rv = findViewById(R.id.rv);
        frameLayout = findViewById(R.id.fl_empty);

        glm = new GridLayoutManager(CategoryActivity.this, 1);
        rv.setLayoutManager(glm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        fab = findViewById(R.id.fab);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = glm.findFirstVisibleItemPosition();

                if (firstVisibleItem > 6) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });

        fab.setOnClickListener(v -> rv.smoothScrollToPosition(0));

        arrayList = dbHelper.getCat();
        if (!arrayList.isEmpty()){
            setAdapterToListview();
        } else {
            getData();
        }

        findViewById(R.id.iv_refresh).setOnClickListener(view -> getData());

        LinearLayout adView = findViewById(R.id.ll_adView);
        helper.showBannerAd(adView);
    }

    private void getData() {
        if (helper.isNetworkAvailable()){
            LoadCat startLoad = new LoadCat(this, new CategoryListener() {
                @Override
                public void onStart() {
                    if (!arrayList.isEmpty()){
                        arrayList.clear();
                    }
                    rv.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> catArrayList) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            if (catArrayList.isEmpty()) {
                                error_msg = getString(R.string.error_no_data_found);
                                setEmpty();
                            } else {
                                arrayList = dbHelper.getCat();
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
            },helper.callAPI(Callback.METHOD_CAT, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            startLoad.execute();
        }else {
            error_msg = getString(R.string.error_internet_not_connected);
            setEmpty();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapterToListview() {
        AdapterCategory adapter = new AdapterCategory(arrayList, (itemCat, position) -> helper.showInter(position,""));
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

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_category;
    }

    @Override
    protected int setApplicationThemes() {
        return ApplicationUtil.isTheme();
    }
}