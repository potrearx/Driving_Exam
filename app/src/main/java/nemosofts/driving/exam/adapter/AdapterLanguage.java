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

import org.jetbrains.annotations.NotNull;

import java.util.List;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.item.ItemLanguage;
import nemosofts.driving.exam.utils.ApplicationUtil;

public class AdapterLanguage extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ItemLanguage> arrayList;
    private final RecyclerItemClickListener listener;
    private int pos = -1;

    public AdapterLanguage(Context context, List<ItemLanguage> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
        this.mContext = context;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        final ItemLanguage item = arrayList.get(position);

        ((ViewHolder) holder).tv_language.setText(item.getName());

        if (pos == position){
            ((ViewHolder) holder).tv_language.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            ((ViewHolder) holder).rl_item_language.setBackgroundResource(R.drawable.bg_language_select);
        }else {
            if (ApplicationUtil.isDarkMode()){
                ((ViewHolder) holder).tv_language.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            }else {
                ((ViewHolder) holder).tv_language.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
            ((ViewHolder) holder).rl_item_language.setBackgroundResource(R.drawable.bg_language);
        }

        ((ViewHolder) holder).rl_item_language.setOnClickListener(v -> listener.onClickListener(item, position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void select(int position) {
        pos = position;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_language;
        RelativeLayout rl_item_language;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_language = itemView.findViewById(R.id.tv_item_language);
            rl_item_language = itemView.findViewById(R.id.rl_item_language);
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemLanguage itemData, int position);
    }

}
