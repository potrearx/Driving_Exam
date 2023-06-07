package nemosofts.driving.exam.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.nemosofts.lk.ProCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.adapter.AdapterSigns;
import nemosofts.driving.exam.asyncTask.LoadSigns;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.ifSupported.IsRTL;
import nemosofts.driving.exam.ifSupported.IsScreenshot;
import nemosofts.driving.exam.interfaces.SignsListener;
import nemosofts.driving.exam.item.ItemSigns;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.EndlessRecyclerViewScrollListener;
import nemosofts.driving.exam.utils.Helper;
import nemosofts.driving.exam.utils.SharedPref;

public class SignsActivity extends ProCompatActivity {

    private SharedPref sharedPref;
    private Helper helper;
    private RecyclerView rv;
    private AdapterSigns adapter;
    private ArrayList<ItemSigns> arrayList;
    private Boolean isOver = false, isScroll = false;
    private int page = 1;
    private GridLayoutManager grid;
    private ProgressBar pb;
    private FloatingActionButton fab;
    private String error_msg;
    private FrameLayout frameLayout;
    private String cid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        Intent intent = getIntent();
        cid = intent.getStringExtra("cid");

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        helper = new Helper(this, (position, type) -> {
            String name =  arrayList.get(position).getName();
            String img =  arrayList.get(position).getImage();
            showDialog(name, img);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        arrayList = new ArrayList<>();

        fab = findViewById(R.id.fab);
        pb = findViewById(R.id.pb);
        rv = findViewById(R.id.rv);
        frameLayout = findViewById(R.id.fl_empty);

        rv.setHasFixedSize(true);
        grid = new GridLayoutManager(SignsActivity.this,3);
        grid.setSpanCount(3);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (adapter.getItemViewType(position) >= 1000 || adapter.isHeader(position)) ? grid.getSpanCount() : 1;
            }
        });
        rv.setLayoutManager(grid);

        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        isScroll = true;
                        getData();
                    }, 0);
                } else {
                    adapter.hideHeader();
                }
            }
        });

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = grid.findFirstVisibleItemPosition();
                if (firstVisibleItem > 6) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        });

        fab.setOnClickListener(v -> rv.smoothScrollToPosition(0));

        getData();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_signs;
    }

    @Override
    protected int setApplicationThemes() {
        return ApplicationUtil.isTheme();
    }
    public void showDialog(String message, String img) {
        Dialog dialog_rate = new Dialog(SignsActivity.this);
        dialog_rate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_rate.setContentView(R.layout.item_dialog);

        final ImageView iv_close = dialog_rate.findViewById(R.id.iv_close);
        final TextView tv_dialog = dialog_rate.findViewById(R.id.tv_dialog);
        final ImageView iv_dialog = dialog_rate.findViewById(R.id.iv_dialog);

        tv_dialog.setText(message);

        Picasso.get()
                .load(img)
                .placeholder(R.drawable.material_design_default)
                .into(iv_dialog);

        iv_close.setOnClickListener(view -> dialog_rate.dismiss());

        dialog_rate.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_rate.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog_rate.show();
        Window window = dialog_rate.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void getData() {
        if (helper.isNetworkAvailable()) {
            LoadSigns startLoad = new LoadSigns(new SignsListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        frameLayout.setVisibility(View.GONE);
                        rv.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemSigns> signsArrayList) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            if (signsArrayList.isEmpty()) {
                                isOver = true;
                                try {
                                    adapter.hideHeader();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                error_msg = getString(R.string.error_no_data_found);
                                setEmpty();
                            } else {
                                for (int i = 0; i < signsArrayList.size(); i++) {
                                    arrayList.add(signsArrayList.get(i));
                                    if (Callback.isNativeAd) {
                                        int abc = arrayList.lastIndexOf(null);
                                        if (((arrayList.size() - (abc + 1)) % Callback.adNativeShow == 0) && (signsArrayList.size() - 1 != i)) {
                                            arrayList.add(null);
                                        }
                                    }
                                }
                                page = page + 1;
                                setAdapterToListview();
                            }
                        } else {
                            helper.getVerifyDialog(getString(R.string.error_unauthorized_access), message);
                        }
                    }else {
                        error_msg = getString(R.string.error_server_not_connected);
                        setEmpty();
                    }
                }
            },helper.callAPI(Callback.METHOD_CAT_ID, page, String.valueOf(sharedPref.getLanguage()), cid, "", "", "", "", "", "", "", "", "", "", null));
            startLoad.execute();
        } else {
            error_msg = getString(R.string.error_internet_not_connected);
            setEmpty();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapterToListview() {
        if(!isScroll) {
            adapter = new AdapterSigns(this,  arrayList, (itemSong, position) -> helper.showInter(position,""));
            rv.setAdapter(adapter);
            rv.setAdapter(adapter);
            setEmpty();
        } else {
            adapter.notifyDataSetChanged();
        }
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
    public void onDestroy() {
        if(adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}