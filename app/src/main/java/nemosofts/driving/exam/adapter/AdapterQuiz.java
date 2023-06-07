package nemosofts.driving.exam.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.item.ItemQuiz;
import nemosofts.driving.exam.utils.ApplicationUtil;

public class AdapterQuiz extends RecyclerView.Adapter<AdapterQuiz.ViewHolder> {

    private final List<ItemQuiz> dataList;
    private final Context mContext;
    private RvOnClickListener clickListener;

    public AdapterQuiz(Context context, List<ItemQuiz> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ItemQuiz item = dataList.get(position);

        int step = 1;
        for (int i = 1; i < position + 1; i++) {
            step++;
        }

        holder.tv_filter.setText(String.valueOf(step));

        if (item.isAnswerDraw()==1){
            holder.rl_filter_bg.setBackgroundResource(R.drawable.bg_item);
            if (ApplicationUtil.isDarkMode()) {
                holder.tv_filter.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                holder.tv_filter.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
        } else if (item.isAnswerDraw()==2) {
            holder.rl_filter_bg.setBackgroundResource(R.drawable.bg_item_good);
            holder.tv_filter.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else if (item.isAnswerDraw()==3) {
            holder.rl_filter_bg.setBackgroundResource(R.drawable.bg_item_no);
            holder.tv_filter.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            holder.rl_filter_bg.setBackgroundResource(R.drawable.bg_item);
            if (ApplicationUtil.isDarkMode()) {
                holder.tv_filter.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                holder.tv_filter.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
        }

        holder.rl_filter_bg.setOnClickListener(v -> clickListener.onItemClick(position));

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void select() {
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_filter;
        public RelativeLayout rl_filter_bg;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_filter_bg = itemView.findViewById(R.id.rl_filter_bg);
            tv_filter = itemView.findViewById(R.id.tv_filter);
        }
    }

    public interface RvOnClickListener {
        void onItemClick(int position);
    }

}
