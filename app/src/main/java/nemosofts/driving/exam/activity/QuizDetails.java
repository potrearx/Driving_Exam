package nemosofts.driving.exam.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.nemosofts.lk.ProCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.adapter.AdapterQuiz;
import nemosofts.driving.exam.asyncTask.LoadQuiz;
import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.dialog.FeedBackDialog;
import nemosofts.driving.exam.ifSupported.IsRTL;
import nemosofts.driving.exam.ifSupported.IsScreenshot;
import nemosofts.driving.exam.interfaces.QuizListener;
import nemosofts.driving.exam.item.ItemQuiz;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.Helper;
import nemosofts.driving.exam.utils.SharedPref;

public class QuizDetails extends ProCompatActivity {

    private Helper helper;
    private SharedPref sharedPref;
    private ArrayList<ItemQuiz> arrayList;
    private AdapterQuiz adapter;
    private RecyclerView recyclerView;
    private TextView tv_details_Answer, iv_cont, tv_details_Answer_A, tv_details_Answer_B, tv_details_Answer_C, tv_details_Answer_D;
    private ImageView iv_image;
    private RadioButton rb_1, rb_2, rb_3, rb_4;
    private ProgressBar loading;
    private RelativeLayout rl_details_bottom;
    private RelativeLayout rl_bt_1;
    private RelativeLayout rl_bt_2;
    private RelativeLayout rl_bt_3;
    private RelativeLayout rl_bt_4;
    private ProgressBar pb_details;
    private NestedScrollView scrollView;
    private int selectedPosition = 0;
    private String myPosition = "";
    private Boolean isFinish = false;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IsRTL.ifSupported(this);
        IsScreenshot.ifSupported(this);

        helper = new Helper(this);
        sharedPref = new SharedPref(this);

        helper = new Helper(this, (position, type) -> {
            selectedPosition = position;
            adapter.select();
            loadData();
        });

        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        pb_details = findViewById(R.id.pb_details);
        TextView tv_details_su_title = findViewById(R.id.tv_details_su_title);
        countDownTimer =  new CountDownTimer(3600000, 1000) {

            public void onTick(long millisUntilFinished) {
                int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1));
                int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                tv_details_su_title.setText(timeLeftFormatted);
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                tv_details_su_title.setText("Finished");
                if (!Callback.arrayList.isEmpty()){
                    Callback.arrayList.clear();
                }
                Callback.arrayList.addAll(arrayList);
                if (!Callback.arrayList.isEmpty()){
                    Intent intent = new Intent(QuizDetails.this, ResultsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        Callback.okAnswer = 0;
        Callback.noAnswer = 0;
        Callback.totelAnswer = 0;

        arrayList = new ArrayList<>();

        recyclerView = findViewById(R.id.rv_filter);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        tv_details_Answer = findViewById(R.id.tv_details_Answer);
        iv_cont = findViewById(R.id.iv_cont);
        iv_image= findViewById(R.id.iv_image);
        rb_1 = findViewById(R.id.rb_1);
        rb_2 = findViewById(R.id.rb_2);
        rb_3 = findViewById(R.id.rb_3);
        rb_4 = findViewById(R.id.rb_4);
        tv_details_Answer_A = findViewById(R.id.tv_details_Answer_A);
        tv_details_Answer_B = findViewById(R.id.tv_details_Answer_B);
        tv_details_Answer_C = findViewById(R.id.tv_details_Answer_C);
        tv_details_Answer_D = findViewById(R.id.tv_details_Answer_D);
        loading = findViewById(R.id.loading);

        rl_details_bottom = findViewById(R.id.rl_details_bottom);
        rl_details_bottom.setBackgroundResource(R.drawable.bg_answer3);
        RelativeLayout rl_onBack = findViewById(R.id.rl_onBack);
        rl_onBack.setOnClickListener(v -> onBackPressed());

        rl_bt_1 = findViewById(R.id.rl_bt_1);
        rl_bt_2 = findViewById(R.id.rl_bt_2);
        rl_bt_3 = findViewById(R.id.rl_bt_3);
        rl_bt_4 = findViewById(R.id.rl_bt_4);

        getData();
        rl_details_bottom.setOnClickListener(v -> {
            int cont5 = selectedPosition + 1;
            int cont6 = arrayList.size() + 1;
            if (isFinish && cont5 == arrayList.size()){
                if (!Callback.arrayList.isEmpty()){
                    Callback.arrayList.clear();
                }
                Callback.arrayList.addAll(arrayList);
                if (!Callback.arrayList.isEmpty()){
                    Intent intent = new Intent(QuizDetails.this, ResultsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }else {
                if (!myPosition.equals("")){
                    if (!arrayList.get(selectedPosition).isEnabled()){
                        if (cont5 != cont6){
                            String myAnswer = arrayList.get(selectedPosition).getCorrectAnswer();
                            if (myAnswer.equals(myPosition)){
                                arrayList.get(selectedPosition).setAnswerDraw(2);
                                arrayList.get(selectedPosition).setEnabled(true);
                                Callback.okAnswer = Callback.okAnswer + 1;
                            } else {
                                arrayList.get(selectedPosition).setAnswerDraw(3);
                                arrayList.get(selectedPosition).setEnabled(true);
                                Callback.noAnswer = Callback.noAnswer + 1;
                            }
                            arrayList.get(selectedPosition).setMyAnswer(myPosition);

                            if (arrayList.size() != cont5){
                                selectedPosition = selectedPosition + 1;
                                recyclerView.scrollToPosition(selectedPosition);
                            }
                            adapter.select();
                            loadData();
                            if (cont5 == arrayList.size()){
                                rl_details_bottom.setBackgroundResource(R.drawable.bg_answer2);
                                isFinish = true;
                            }
                            myPosition = "";
                        }
                    }
                }
            }
        });

        rl_bt_1.setOnClickListener(v -> {
            if (!arrayList.get(selectedPosition).isEnabled()){
                setPosition("A", true);
            }
        });
        rl_bt_2.setOnClickListener(v -> {
            if (!arrayList.get(selectedPosition).isEnabled()){
                setPosition("B", true);
            }
        });
        rl_bt_3.setOnClickListener(v -> {
            if (!arrayList.get(selectedPosition).isEnabled()){
                setPosition("C", true);
            }
        });
        rl_bt_4.setOnClickListener(v -> {
            if (!arrayList.get(selectedPosition).isEnabled()){
                setPosition("D", true);
            }
        });
        rb_1.setOnClickListener(view -> setPosition("A", true));
        rb_2.setOnClickListener(view -> setPosition("B", true));
        rb_3.setOnClickListener(view -> setPosition("C", true));
        rb_4.setOnClickListener(view -> setPosition("D", true));

        findViewById(R.id.iv_feedback).setOnClickListener(v -> Feedback());

        LinearLayout adView = findViewById(R.id.ll_adView);
        helper.showBannerAd(adView);
    }

    private void Feedback() {
        new FeedBackDialog(this).showDialog("", "");
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_quiz_details;
    }

    @Override
    protected int setApplicationThemes() {
        return ApplicationUtil.isTheme();
    }

    private void setPosition(String position, @NonNull Boolean flag) {
        if (flag){ myPosition = position; }
        rb_1.setChecked(position.equals("A"));
        rb_2.setChecked(position.equals("B"));
        rb_3.setChecked(position.equals("C"));
        rb_4.setChecked(position.equals("D"));
    }

    private void getData() {
        if (helper.isNetworkAvailable()) {
            LoadQuiz loadQuiz = new LoadQuiz(new QuizListener() {
                @Override
                public void onStart() {
                    pb_details.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String message, ArrayList<ItemQuiz> arrayList_img, ArrayList<ItemQuiz> arrayList_no_img) {
                    if (success.equals("1")) {
                        if (arrayList_img.size() == 0) {
                            pb_details.setVisibility(View.GONE);
                        } else {
                            arrayList.addAll(arrayList_img);
                            arrayList.addAll(arrayList_no_img);
                            pb_details.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                            rl_details_bottom.setBackgroundResource(R.drawable.bg_answer);
                            setAdapterToListview();
                        }
                    } else {
                        pb_details.setVisibility(View.GONE);
                    }
                }
            }, helper.callAPI(Callback.METHOD_QUIZ, 0, String.valueOf(sharedPref.getLanguage()), "","","","","","","","","","","",null));
            loadQuiz.execute();
        } else {
            pb_details.setVisibility(View.GONE);
        }
    }

    private void setAdapterToListview() {
        countDownTimer.start();
        adapter = new AdapterQuiz(this, arrayList);
        recyclerView.setAdapter(adapter);
        adapter.select();
        Callback.totelAnswer = arrayList.size();
        adapter.setOnItemClickListener(position -> helper.showInter(position,""));
        loadData();
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        tv_details_Answer.setText(arrayList.get(selectedPosition).getAnswer());
        int cont = selectedPosition + 1;
        int cont2 = arrayList.size();
        iv_cont.setText(cont +" / "+ cont2);

        if (!arrayList.get(selectedPosition).getImage().equals("")){
            iv_image.setVisibility(View.VISIBLE);
            loading.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(arrayList.get(selectedPosition).getImage())
                    .placeholder(R.drawable.material_design_default)
                    .into(iv_image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            loading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            loading.setVisibility(View.GONE);
                        }
                    });

        } else {
            iv_image.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
        }

        tv_details_Answer_A.setText(arrayList.get(selectedPosition).getAnswerA());
        tv_details_Answer_B.setText(arrayList.get(selectedPosition).getAnswerB());
        tv_details_Answer_C.setText(arrayList.get(selectedPosition).getAnswerC());
        tv_details_Answer_D.setText(arrayList.get(selectedPosition).getAnswerD());

        rb_1.setChecked(false);
        rb_2.setChecked(false);
        rb_3.setChecked(false);
        rb_4.setChecked(false);

        if (arrayList.get(selectedPosition).isEnabled()){
            rb_1.setEnabled(false);
            rb_2.setEnabled(false);
            rb_3.setEnabled(false);
            rb_4.setEnabled(false);

            setMyAnswer(arrayList.get(selectedPosition).isMyAnswer());

            setCorrectAnswer(arrayList.get(selectedPosition).getCorrectAnswer());

        } else {
            rl_bt_1.setBackgroundResource(R.drawable.bg_an);
            rl_bt_2.setBackgroundResource(R.drawable.bg_an);
            rl_bt_3.setBackgroundResource(R.drawable.bg_an);
            rl_bt_4.setBackgroundResource(R.drawable.bg_an);

            rb_1.setEnabled(true);
            rb_2.setEnabled(true);
            rb_3.setEnabled(true);
            rb_4.setEnabled(true);
        }

        if (arrayList.get(selectedPosition).isMyAnswer().equals("A")){
            setPosition("A", false);
        } else if (arrayList.get(selectedPosition).isMyAnswer().equals("B")){
            setPosition("B", false);
        } else if (arrayList.get(selectedPosition).isMyAnswer().equals("C")){
            setPosition("C", false);
        } else if (arrayList.get(selectedPosition).isMyAnswer().equals("D")){
            setPosition("D", false);
        }
    }

    private void setCorrectAnswer(@NonNull String correctAnswer) {
        if (correctAnswer.equals("A")){
            rl_bt_1.setBackgroundResource(R.drawable.bg_an_ok);
        }
        if (correctAnswer.equals("B")){
            rl_bt_2.setBackgroundResource(R.drawable.bg_an_ok);
        }
        if (correctAnswer.equals("C")){
            rl_bt_3.setBackgroundResource(R.drawable.bg_an_ok);
        }
        if (correctAnswer.equals("D")){
            rl_bt_4.setBackgroundResource(R.drawable.bg_an_ok);
        }
    }

    private void setMyAnswer(@NonNull String answer) {
        rl_bt_1.setBackgroundResource(answer.equals("A") ? R.drawable.bg_an_error : R.drawable.bg_an);
        rl_bt_2.setBackgroundResource(answer.equals("B") ? R.drawable.bg_an_error : R.drawable.bg_an);
        rl_bt_3.setBackgroundResource(answer.equals("C") ? R.drawable.bg_an_error : R.drawable.bg_an);
        rl_bt_4.setBackgroundResource(answer.equals("D") ? R.drawable.bg_an_error : R.drawable.bg_an);
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void exitDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(QuizDetails.this, R.style.ThemeDialog);
        alert.setTitle(getString(R.string.quit));
        alert.setMessage(getString(R.string.sure_quit));
        alert.setPositiveButton(getString(R.string.quit), (dialogInterface, i) -> finish());
        alert.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
        });
        alert.show();
    }

}