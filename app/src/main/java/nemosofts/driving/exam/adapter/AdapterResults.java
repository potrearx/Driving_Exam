package nemosofts.driving.exam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.item.ItemQuiz;
import nemosofts.driving.exam.utils.ApplicationUtil;

public class AdapterResults extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ItemQuiz> arrayList;
    private final Boolean flag;

    public AdapterResults(Context context, List<ItemQuiz> arrayList, Boolean flag) {
        this.arrayList = arrayList;
        this.mContext = context;
        this.flag = flag;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        final ItemQuiz item = arrayList.get(position);

        int step = 1;
        for (int i = 1; i < position + 1; i++) {
            step++;
        }

        if (step >= 0 && step < 10) {
            ((ViewHolder) holder).tv_num.setText("(0"+String.valueOf(step)+")");
        }else {
            ((ViewHolder) holder).tv_num.setText("("+String.valueOf(step)+")");
        }

        ((ViewHolder) holder).tv_question.setText(item.getAnswer());

        ((ViewHolder) holder).tv_answer_1.setText(item.getAnswerA());
        ((ViewHolder) holder).tv_answer_2.setText(item.getAnswerB());
        ((ViewHolder) holder).tv_answer_3.setText(item.getAnswerC());
        ((ViewHolder) holder).tv_answer_4.setText(item.getAnswerD());

        if (item.isEnabled()){
            ((ViewHolder) holder).tv_not_answer.setVisibility(View.GONE);

            if (item.isMyAnswer().equals("A")){
                ((ViewHolder) holder).iv_answer_1.setImageResource(R.drawable.ic_remove_outline);
                ((ViewHolder) holder).tv_answer_1.setTextColor(ContextCompat.getColor(mContext, R.color.color_check_error));
            }else {
                ((ViewHolder) holder).iv_answer_1.setImageResource(R.drawable.ic_unchecked);
                if (ApplicationUtil.isDarkMode()){
                    ((ViewHolder) holder).tv_answer_1.setTextColor(ContextCompat.getColor(mContext, R.color.titleNight));
                }else {
                    ((ViewHolder) holder).tv_answer_1.setTextColor(ContextCompat.getColor(mContext, R.color.titleLight));
                }
            }

            if (item.isMyAnswer().equals("B")){
                ((ViewHolder) holder).iv_answer_2.setImageResource(R.drawable.ic_remove_outline);
                ((ViewHolder) holder).tv_answer_2.setTextColor(ContextCompat.getColor(mContext, R.color.color_check_error));
            }else {
                ((ViewHolder) holder).iv_answer_2.setImageResource(R.drawable.ic_unchecked);
                if (ApplicationUtil.isDarkMode()){
                    ((ViewHolder) holder).tv_answer_3.setTextColor(ContextCompat.getColor(mContext, R.color.titleNight));
                }else {
                    ((ViewHolder) holder).tv_answer_3.setTextColor(ContextCompat.getColor(mContext, R.color.titleLight));
                }
            }

            if (item.isMyAnswer().equals("C")){
                ((ViewHolder) holder).iv_answer_3.setImageResource(R.drawable.ic_remove_outline);
                ((ViewHolder) holder).tv_answer_3.setTextColor(ContextCompat.getColor(mContext, R.color.color_check_error));
            }else {
                ((ViewHolder) holder).iv_answer_3.setImageResource(R.drawable.ic_unchecked);
                if (ApplicationUtil.isDarkMode()){
                    ((ViewHolder) holder).tv_answer_3.setTextColor(ContextCompat.getColor(mContext, R.color.titleNight));
                }else {
                    ((ViewHolder) holder).tv_answer_3.setTextColor(ContextCompat.getColor(mContext, R.color.titleLight));
                }
            }

            if (item.isMyAnswer().equals("D")){
                ((ViewHolder) holder).iv_answer_4.setImageResource(R.drawable.ic_remove_outline);
                ((ViewHolder) holder).tv_answer_4.setTextColor(ContextCompat.getColor(mContext, R.color.color_check_error));
            }else {
                ((ViewHolder) holder).iv_answer_4.setImageResource(R.drawable.ic_unchecked);
                if (ApplicationUtil.isDarkMode()){
                    ((ViewHolder) holder).tv_answer_4.setTextColor(ContextCompat.getColor(mContext, R.color.titleNight));
                }else {
                    ((ViewHolder) holder).tv_answer_4.setTextColor(ContextCompat.getColor(mContext, R.color.titleLight));
                }
            }

        }else {
            if (flag){
                ((ViewHolder) holder).tv_not_answer.setVisibility(View.VISIBLE);
            }else {
                ((ViewHolder) holder).tv_not_answer.setVisibility(View.GONE);
            }
            ((ViewHolder) holder).iv_answer_1.setImageResource(R.drawable.ic_unchecked);
            ((ViewHolder) holder).iv_answer_2.setImageResource(R.drawable.ic_unchecked);
            ((ViewHolder) holder).iv_answer_3.setImageResource(R.drawable.ic_unchecked);
            ((ViewHolder) holder).iv_answer_4.setImageResource(R.drawable.ic_unchecked);
            if (ApplicationUtil.isDarkMode()){
                ((ViewHolder) holder).tv_answer_1.setTextColor(ContextCompat.getColor(mContext, R.color.titleNight));
                ((ViewHolder) holder).tv_answer_2.setTextColor(ContextCompat.getColor(mContext, R.color.titleNight));
                ((ViewHolder) holder).tv_answer_3.setTextColor(ContextCompat.getColor(mContext, R.color.titleNight));
                ((ViewHolder) holder).tv_answer_4.setTextColor(ContextCompat.getColor(mContext, R.color.titleNight));
            }else {
                ((ViewHolder) holder).tv_answer_1.setTextColor(ContextCompat.getColor(mContext, R.color.titleLight));
                ((ViewHolder) holder).tv_answer_2.setTextColor(ContextCompat.getColor(mContext, R.color.titleLight));
                ((ViewHolder) holder).tv_answer_3.setTextColor(ContextCompat.getColor(mContext, R.color.titleLight));
                ((ViewHolder) holder).tv_answer_4.setTextColor(ContextCompat.getColor(mContext, R.color.titleLight));
            }
        }

        if (item.getCorrectAnswer().equals("A")){
            ((ViewHolder) holder).iv_answer_1.setImageResource(R.drawable.ic_check);
            ((ViewHolder) holder).tv_answer_1.setTextColor(mContext.getResources().getColor(R.color.color_check_in));
        }
        if (item.getCorrectAnswer().equals("B")){
            ((ViewHolder) holder).iv_answer_2.setImageResource(R.drawable.ic_check);
            ((ViewHolder) holder).tv_answer_2.setTextColor(mContext.getResources().getColor(R.color.color_check_in));
        }
        if (item.getCorrectAnswer().equals("C")){
            ((ViewHolder) holder).iv_answer_3.setImageResource(R.drawable.ic_check);
            ((ViewHolder) holder).tv_answer_3.setTextColor(mContext.getResources().getColor(R.color.color_check_in));
        }
        if (item.getCorrectAnswer().equals("D")){
            ((ViewHolder) holder).iv_answer_4.setImageResource(R.drawable.ic_check);
            ((ViewHolder) holder).tv_answer_4.setTextColor(mContext.getResources().getColor(R.color.color_check_in));
        }

        if (item.getImage().isEmpty()){
            ((ViewHolder) holder).iv_image.setVisibility(View.GONE);
        }else {
            Picasso.get()
                    .load(arrayList.get(position).getImage())
                    .placeholder(R.drawable.material_design_default)
                    .into(((ViewHolder) holder).iv_image);
            ((ViewHolder) holder).iv_image.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv_num, tv_question, tv_not_answer;
        private final ImageView iv_image;
        private final ImageView iv_answer_1, iv_answer_2, iv_answer_3, iv_answer_4;
        private final TextView tv_answer_1, tv_answer_2, tv_answer_3, tv_answer_4;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_num = itemView.findViewById(R.id.tv_res_num);
            tv_question = itemView.findViewById(R.id.tv_res_question);
            iv_image = itemView.findViewById(R.id.iv_res_image);
            tv_not_answer = itemView.findViewById(R.id.tv_res_not_answer);

            iv_answer_1 = itemView.findViewById(R.id.iv_res_answer_1);
            iv_answer_2 = itemView.findViewById(R.id.iv_res_answer_2);
            iv_answer_3 = itemView.findViewById(R.id.iv_res_answer_3);
            iv_answer_4 = itemView.findViewById(R.id.iv_res_answer_4);

            tv_answer_1 = itemView.findViewById(R.id.tv_res_answer_1);
            tv_answer_2 = itemView.findViewById(R.id.tv_res_answer_2);
            tv_answer_3 = itemView.findViewById(R.id.tv_res_answer_3);
            tv_answer_4 = itemView.findViewById(R.id.tv_res_answer_4);
        }
    }
}
